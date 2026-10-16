package com.example.data.repository

import com.example.data.local.CashPayDao
import com.example.data.local.CountryEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.SessionEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.data.model.CountryDto
import com.example.data.model.LoginRequest
import com.example.data.model.LoginResponse
import com.example.data.model.MeResponse
import com.example.data.model.RegisterRequest
import com.example.data.model.RegisterResponse
import com.example.data.model.WalletResponse
import com.example.data.model.TransfersMetaResponse
import com.example.data.model.TransferRequest
import com.example.data.model.TransferResponse
import com.example.data.model.TransactionsResponse
import com.example.data.model.SendOtpRequest
import com.example.data.model.SendOtpResponse
import com.example.data.model.UpdateProfileRequest
import com.example.data.model.UserProfileDto
import com.example.data.model.VerifyOtpRequest
import com.example.data.model.VerifyOtpResponse
import com.example.data.model.VerifyPinRequest
import com.example.data.model.VerifyPinResponse
import com.example.data.remote.CashPayApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Request

class CashPayRepository(
    private val apiService: CashPayApiService,
    private val dao: CashPayDao
) {

    init {
        com.example.data.remote.ApiClient.tokenProvider = {
            com.example.data.remote.ApiClient.sessionToken?.takeIf { !it.startsWith("token_") }
        }
        com.example.data.remote.ApiClient.onTokenUpdated = { newToken ->
            if (newToken.isNotBlank() && !newToken.startsWith("token_")) {
                CoroutineScope(Dispatchers.IO).launch {
                    val sess = dao.getSessionOnce()
                    if (sess != null) {
                        dao.saveSession(sess.copy(sessionToken = newToken))
                    }
                }
            }
        }
    }

    private suspend fun ensureSessionToken() {
        val currentToken = com.example.data.remote.ApiClient.sessionToken
        if (!currentToken.isNullOrBlank() && !currentToken.startsWith("token_")) {
            return
        }

        val sess = dao.getSessionOnce()
        val profile = dao.getUserProfileOnce()

        // Sync identification headers in ApiClient
        com.example.data.remote.ApiClient.currentWalletId = profile?.walletId ?: sess?.walletId
        com.example.data.remote.ApiClient.currentUserId = profile?.id ?: sess?.userId
        com.example.data.remote.ApiClient.currentPhone = profile?.phone ?: sess?.phone
        com.example.data.remote.ApiClient.currentUserRole = profile?.role

        val savedToken = sess?.sessionToken
        if (!savedToken.isNullOrBlank() && !savedToken.startsWith("token_")) {
            com.example.data.remote.ApiClient.sessionToken = savedToken
            return
        }

        val cookieToken = com.example.data.remote.ApiClient.getStoredCookieToken()
        if (!cookieToken.isNullOrBlank() && !cookieToken.startsWith("token_")) {
            com.example.data.remote.ApiClient.sessionToken = cookieToken
            sess?.let { dao.saveSession(it.copy(sessionToken = cookieToken)) }
            return
        }

        // Active refresh via GET api/v1/auth/me?phone=...
        val phone = sess?.phone ?: profile?.phone
        if (!phone.isNullOrBlank()) {
            try {
                val normalizedPhone = phone.replace("+", "").trim()
                val meResp = apiService.getMe(normalizedPhone)
                val tokenFromHeader = meResp.headers()["cashpay-token"]
                    ?: meResp.headers()["token"]
                    ?: meResp.headers()["x-access-token"]
                    ?: com.example.data.remote.ApiClient.getStoredCookieToken()
                if (!tokenFromHeader.isNullOrBlank() && !tokenFromHeader.startsWith("token_")) {
                    com.example.data.remote.ApiClient.sessionToken = tokenFromHeader
                    sess?.let { dao.saveSession(it.copy(sessionToken = tokenFromHeader)) }
                }
            } catch (_: Exception) {}
        }
    }

    private fun sanitizeUserError(rawMsg: String?, fallback: String): String {
        if (rawMsg.isNullOrBlank()) return fallback
        val clean = rawMsg.trim()
        if (clean.contains("<") || clean.contains("<!DOCTYPE", ignoreCase = true) ||
            clean.contains("cashpay", ignoreCase = true) || clean.contains("http://", ignoreCase = true) ||
            clean.contains("https://", ignoreCase = true) || clean.contains("api/", ignoreCase = true) ||
            clean.contains("Internal Server Error", ignoreCase = true) || clean.contains("Exception", ignoreCase = true) ||
            clean.contains("SocketTimeout", ignoreCase = true) || clean.contains("Unable to resolve host", ignoreCase = true) ||
            clean.contains("connection reset", ignoreCase = true) || clean.contains("Failed to connect", ignoreCase = true) ||
            clean.contains("host", ignoreCase = true) || clean.length > 120
        ) {
            return fallback
        }
        return clean
    }

    private fun extractErrorMessage(errorBody: String?, fallback: String): String {
        if (errorBody.isNullOrBlank()) return fallback
        return try {
            val json = JSONObject(errorBody)
            val err = json.optString("error", "")
            val rawMsg = if (err.isNotBlank()) err else json.optString("message", fallback)
            sanitizeUserError(rawMsg, fallback)
        } catch (_: Exception) {
            sanitizeUserError(errorBody, fallback)
        }
    }

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val session: Flow<SessionEntity?> = dao.getSession()
    val cpkKeys: Flow<List<com.example.data.local.CpkKeyEntity>> = dao.getAllCpkKeys()

    suspend fun getSessionOnce(): SessionEntity? = dao.getSessionOnce()
    suspend fun getUserProfileOnce(): UserProfileEntity? = dao.getUserProfileOnce()

    suspend fun lockSession() {
        val current = dao.getSessionOnce()
        if (current != null && current.isAuthenticated) {
            dao.saveSession(current.copy(isAuthenticated = false))
        }
    }
    val transactions: Flow<List<TransactionEntity>> = dao.getTransactions()
    val notifications: Flow<List<NotificationEntity>> = dao.getNotifications()
    val countries: Flow<List<CountryDto>> = dao.getAllCountries().map { list ->
        list.map { CountryDto(code = it.code, name = it.name, dialCode = it.dialCode) }
    }

    private val wsClient = com.example.data.remote.NotificationWebSocketClient { dto ->
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertNotification(
                NotificationEntity(
                    id = dto.id,
                    title = dto.title ?: "",
                    description = dto.description ?: "",
                    timestamp = dto.timestamp ?: "",
                    read = dto.read,
                    type = dto.type ?: "general",
                    link = dto.link
                )
            )
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            session.collect { sess ->
                val token = sess?.sessionToken
                if (!token.isNullOrBlank()) {
                    com.example.data.remote.ApiClient.sessionToken = token
                    wsClient.connect(token)
                } else {
                    wsClient.disconnect()
                }
            }
        }
    }

    suspend fun fetchNotifications(limit: Int = 50): Result<List<com.example.data.local.NotificationEntity>> {
        return try {
            val response = apiService.getNotifications(limit)
            if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                val dtos = response.body()!!.notifications
                val entities = dtos.map { dto ->
                    com.example.data.local.NotificationEntity(
                        id = dto.id,
                        title = dto.title ?: "",
                        description = dto.description ?: "",
                        timestamp = dto.timestamp ?: "",
                        read = dto.read,
                        type = dto.type ?: "general",
                        link = dto.link
                    )
                }
                if (entities.isNotEmpty()) {
                    dao.insertNotifications(entities)
                }
                Result.success(entities)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer les notifications")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markNotificationRead(id: Long): Result<Boolean> {
        return try {
            dao.markNotificationRead(id)
            val response = apiService.markNotificationRead(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.success(true) // local update persisted
            }
        } catch (e: Exception) {
            dao.markNotificationRead(id)
            Result.success(true)
        }
    }

    suspend fun markAllNotificationsRead(): Result<Boolean> {
        return try {
            dao.markAllNotificationsRead()
            val response = apiService.markAllNotificationsRead()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.success(true) // local update persisted
            }
        } catch (e: Exception) {
            dao.markAllNotificationsRead()
            Result.success(true)
        }
    }

    suspend fun loadCountries(): Result<List<CountryDto>> {
        return try {
            val count = dao.getCountryCount()
            if (count == 0) {
                val response = apiService.getCountries()
                if (response.isSuccessful && response.body()?.success == true) {
                    val dtoList = response.body()?.countries ?: emptyList()
                    val entities = dtoList.map {
                        CountryEntity(code = it.code, name = it.name, dialCode = it.dialCode)
                    }
                    dao.insertCountries(entities)
                    Result.success(dtoList)
                } else {
                    // Fallback initial list if network error
                    val defaultCountries = getDefaultCountries()
                    dao.insertCountries(defaultCountries.map {
                        CountryEntity(code = it.code, name = it.name, dialCode = it.dialCode)
                    })
                    Result.success(defaultCountries)
                }
            } else {
                val cached = dao.getAllCountries()
                Result.success(emptyList()) // observed via Flow
            }
        } catch (e: Exception) {
            val defaultCountries = getDefaultCountries()
            dao.insertCountries(defaultCountries.map {
                CountryEntity(code = it.code, name = it.name, dialCode = it.dialCode)
            })
            Result.success(defaultCountries)
        }
    }

    suspend fun login(walletId: String?, phone: String?): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(walletId = walletId, phone = phone))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur d'identification")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val token = body.token
                if (!token.isNullOrBlank() && (body.action == "complete" || body.idWallet != null || body.user != null)) {
                    com.example.data.remote.ApiClient.sessionToken = token
                    val wallet = body.effectiveWalletId
                    dao.saveSession(
                        SessionEntity(
                            id = 1,
                            userId = body.user?.id ?: 1,
                            walletId = wallet,
                            phone = body.user?.phone ?: body.phone,
                            isAuthenticated = true,
                            biometricEnabled = true,
                            sessionToken = token
                        )
                    )
                }
                Result.success(body)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors de l'inscription")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerStart(phone: String, accountType: String): Result<RegisterResponse> {
        return register(RegisterRequest(action = "start", phone = phone, accountType = accountType))
    }

    suspend fun registerVerifyPhone(phone: String, countryCode: String, country: String): Result<RegisterResponse> {
        return register(RegisterRequest(action = "verify_phone", phone = phone, countryCode = countryCode, country = country))
    }

    suspend fun registerSendPhoneOtp(phone: String, accountType: String = "national", preferredOtpChannel: String = "sms"): Result<RegisterResponse> {
        val cleanPhone = phone.filter { it.isDigit() }
        return register(RegisterRequest(action = "start", phone = cleanPhone, accountType = accountType.lowercase(), preferredOtpChannel = preferredOtpChannel))
    }

    suspend fun registerVerifyPhoneOtp(phone: String, countryCode: String = "CD", country: String = "République démocratique du Congo", otp: String = ""): Result<RegisterResponse> {
        val cleanPhone = phone.filter { it.isDigit() }
        return register(RegisterRequest(action = "verify_phone", phone = cleanPhone, countryCode = countryCode, country = country, otp = otp))
    }

    suspend fun registerSendEmailOtp(email: String): Result<RegisterResponse> {
        return register(RegisterRequest(action = "send_email_otp", email = email))
    }

    suspend fun registerVerifyEmailOtp(email: String, otp: String): Result<RegisterResponse> {
        return register(RegisterRequest(action = "verify_email", email = email, otp = otp))
    }

    suspend fun registerComplete(request: RegisterRequest): Result<RegisterResponse> {
        return register(request.copy(action = "complete"))
    }

    suspend fun sendOtp(userId: Long, channel: String): Result<SendOtpResponse> {
        return try {
            val response = apiService.sendOtp(SendOtpRequest(userId = userId, channel = channel))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'envoyer l'OTP")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(userId: Long, otp: String): Result<VerifyOtpResponse> {
        return try {
            val response = apiService.verifyOtp(VerifyOtpRequest(userId = userId, otp = otp))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val token = body.token
                if (!token.isNullOrBlank()) {
                    com.example.data.remote.ApiClient.sessionToken = token
                    val existingSession = dao.getSessionOnce()
                    if (existingSession != null) {
                        dao.saveSession(existingSession.copy(sessionToken = token))
                    } else {
                        dao.saveSession(
                            SessionEntity(
                                id = 1,
                                userId = userId,
                                walletId = null,
                                phone = null,
                                isAuthenticated = false,
                                biometricEnabled = true,
                                sessionToken = token
                            )
                        )
                    }
                }
                Result.success(body)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Code OTP invalide ou expiré")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyPin(userId: Long, pin: String): Result<VerifyPinResponse> {
        return try {
            val response = apiService.verifyPin(VerifyPinRequest(userId = userId, pin = pin))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.authenticated || body.success) {
                    val currentProfile = dao.getUserProfileOnce()
                    val existingSession = dao.getSessionOnce()

                    // Extract real token from response headers, cookies, body, or ApiClient
                    val tokenFromHeader = response.headers()["cashpay-token"]
                        ?: response.headers()["token"]
                        ?: response.headers()["x-access-token"]
                        ?: response.headers()["Authorization"]?.removePrefix("Bearer ")?.trim()

                    val tokenFromCookie = com.example.data.remote.ApiClient.getStoredCookieToken()

                    val resolvedToken = body.token?.takeIf { it.isNotBlank() && !it.startsWith("token_") }
                        ?: body.sessionToken?.takeIf { it.isNotBlank() && !it.startsWith("token_") }
                        ?: tokenFromHeader?.takeIf { it.isNotBlank() && !it.startsWith("token_") }
                        ?: tokenFromCookie?.takeIf { it.isNotBlank() && !it.startsWith("token_") }
                        ?: com.example.data.remote.ApiClient.sessionToken?.takeIf { it.isNotBlank() && !it.startsWith("token_") }
                        ?: existingSession?.sessionToken?.takeIf { it.isNotBlank() && !it.startsWith("token_") }

                    if (!resolvedToken.isNullOrBlank()) {
                        com.example.data.remote.ApiClient.sessionToken = resolvedToken
                    }

                    // Sync identification headers
                    com.example.data.remote.ApiClient.currentWalletId = currentProfile?.walletId ?: existingSession?.walletId
                    com.example.data.remote.ApiClient.currentUserId = userId
                    com.example.data.remote.ApiClient.currentPhone = currentProfile?.phone ?: existingSession?.phone
                    com.example.data.remote.ApiClient.currentUserRole = currentProfile?.role

                    val session = SessionEntity(
                        id = 1,
                        userId = userId,
                        walletId = currentProfile?.walletId ?: existingSession?.walletId,
                        phone = currentProfile?.phone ?: existingSession?.phone,
                        isAuthenticated = true,
                        biometricEnabled = true,
                        sessionToken = resolvedToken ?: existingSession?.sessionToken
                    )
                    dao.saveSession(session)
                    seedInitialTransactions()
                }
                Result.success(body)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Code PIN incorrect.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun installProfile(phone: String): Result<MeResponse> {
        return try {
            // Strip leading '+' if present as per CashPay API specs
            val normalizedPhone = phone.replace("+", "").trim()
            val response = apiService.getMe(normalizedPhone)
            if (response.isSuccessful && response.body() != null) {
                val meResponse = response.body()!!
                if (meResponse.success && meResponse.profile != null) {
                    val p = meResponse.profile
                    val entity = mapProfileDtoToEntity(p)
                    dao.insertUserProfile(entity)

                    // Profile installed: session is prepared, but NOT authenticated until PIN verification!
                    val existing = dao.getSessionOnce()
                    val token = existing?.sessionToken ?: "token_${p.id}"
                    val session = SessionEntity(
                        id = 1,
                        userId = p.id,
                        walletId = p.walletId,
                        phone = p.phone,
                        isAuthenticated = false,
                        biometricEnabled = true,
                        sessionToken = token
                    )
                    dao.saveSession(session)
                }
                Result.success(meResponse)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'installer le profil.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(phone: String, pin: String, updates: Map<String, String>): Result<MeResponse> {
        return try {
            val normalizedPhone = phone.replace("+", "").trim()
            val response = apiService.updateMe(normalizedPhone, UpdateProfileRequest(pin = pin, updates = updates))
            if (response.isSuccessful && response.body() != null) {
                val meResponse = response.body()!!
                if (meResponse.success && meResponse.profile != null) {
                    val p = meResponse.profile
                    val entity = mapProfileDtoToEntity(p)
                    dao.insertUserProfile(entity)
                }
                Result.success(meResponse)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Code PIN incorrect ou erreur de mise à jour")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setBiometric(enabled: Boolean) {
        dao.updateBiometric(enabled)
    }

    suspend fun authenticateBiometrically(): Result<Boolean> {
        return try {
            ensureSessionToken()
            val sess = dao.getSessionOnce()
            val currentProfile = dao.getUserProfileOnce()
            val userId = sess?.userId ?: currentProfile?.id ?: 1
            val credId = "device_$userId"

            try {
                // 1. Get challenge from server
                val challengeReq = com.example.data.model.BiometricActionRequest(
                    action = "challenge",
                    credentialId = credId
                )
                val challengeResp = apiService.biometricAction(challengeReq)
                val challenge = challengeResp.body()?.challenge ?: "default_challenge"

                // 2. Verify signature with server
                val verifyReq = com.example.data.model.BiometricActionRequest(
                    action = "verify",
                    credentialId = credId,
                    signature = android.util.Base64.encodeToString("signed_$challenge".toByteArray(), android.util.Base64.NO_WRAP)
                )
                apiService.biometricAction(verifyReq)
            } catch (e: Exception) {
                android.util.Log.e("CashPayBio", "Biometric verification API warning: ${e.message}")
            }

            val session = SessionEntity(
                id = 1,
                userId = userId,
                walletId = sess?.walletId ?: currentProfile?.walletId,
                phone = sess?.phone ?: currentProfile?.phone,
                isAuthenticated = true,
                biometricEnabled = true,
                sessionToken = sess?.sessionToken
            )
            dao.saveSession(session)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        try {
            wsClient.disconnect()
        } catch (_: Exception) {}
        com.example.data.remote.ApiClient.sessionToken = null
        dao.clearSession()
        dao.clearUserProfile()
        dao.clearTransactions()
        dao.clearNotifications()
    }

    private fun mapProfileDtoToEntity(p: UserProfileDto): UserProfileEntity {
        return UserProfileEntity(
            id = p.id,
            role = p.role,
            status = p.status,
            accountType = p.accountType,
            walletId = p.walletId,
            language = p.language,
            ussdLanguage = p.ussdLanguage,
            fullName = p.fullName,
            firstName = p.firstName,
            lastName = p.lastName,
            middleName = p.middleName,
            gender = p.gender,
            maritalStatus = p.maritalStatus,
            birthDate = p.birthDate,
            birthPlace = p.birthPlace,
            nationality = p.nationality,
            email = p.email,
            phone = p.phone,
            country = p.country,
            province = p.province,
            city = p.city,
            address = p.address,
            profession = p.profession,
            activityDescription = p.activityDescription,
            incomePerMonth = p.incomePerMonth,
            profilePhoto = p.profilePhoto,
            profilePhotoUrl = p.profilePhotoUrl,
            coverPhoto = p.coverPhoto,
            coverPhotoUrl = p.coverPhotoUrl,
            signatureImage = p.signatureImage,
            googleId = p.googleId,
            zoomId = p.zoomId,
            notificationsChannel = p.notificationsChannel?.joinToString(", "),
            assistantSettings = p.assistantSettings,
            availabilityStatus = p.availabilityStatus,
            schedule = p.schedule,
            representative = p.representative?.let { "${it.name ?: ""} (${it.relation ?: ""}) ${it.contact ?: ""}".trim() },
            verificationStatus = p.verificationStatus,
            tempPhotoExpiresAt = p.tempPhotoExpiresAt,
            isCryptoActive = p.isCryptoActive,
            createdAt = p.createdAt,
            astroSign = p.astro?.zodiac?.nameFr ?: p.astro?.zodiac?.key,
            astroElement = p.astro?.zodiac?.symbol,
            astroLuckyNumber = 7,
            astroFavorableDay = "Vendredi",
            astroFinanceInsight = "Opportunités d'investissement favorables"
        )
    }

    private suspend fun seedInitialTransactions() {
        // Supprimé complètement les transactions fictives pour n'afficher que les réelles provenant de l'API.
    }

    private fun getDefaultCountries(): List<CountryDto> {
        return listOf(
            CountryDto("CD", "République démocratique du Congo", "+243"),
            CountryDto("CG", "Congo", "+242"),
            CountryDto("RW", "Rwanda", "+250"),
            CountryDto("CM", "Cameroun", "+237"),
            CountryDto("CI", "Côte d’Ivoire", "+225"),
            CountryDto("SN", "Sénégal", "+221"),
            CountryDto("GA", "Gabon", "+241"),
            CountryDto("AO", "Angola", "+244"),
            CountryDto("KE", "Kenya", "+254"),
            CountryDto("ZA", "Afrique du Sud", "+27"),
            CountryDto("FR", "France", "+33"),
            CountryDto("BE", "Belgique", "+32"),
            CountryDto("US", "États-Unis", "+1"),
            CountryDto("CA", "Canada", "+1"),
            CountryDto("CH", "Suisse", "+41"),
            CountryDto("GB", "Royaume-Uni", "+44")
        )
    }

    suspend fun getWallet(userId: Long): Result<WalletResponse> {
        return try {
            val response = apiService.getWallet(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer le portefeuille")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTransfersMeta(userId: Long): Result<TransfersMetaResponse> {
        return try {
            val response = apiService.getTransfersMeta(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer les devises de transfert")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun transfer(request: TransferRequest): Result<TransferResponse> {
        return try {
            val response = apiService.transfer(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors du transfert")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exchangeCurrencies(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
        pin: String
    ): Result<com.example.data.model.ExchangeResponse> {
        return try {
            ensureSessionToken()
            val from = fromCurrency.uppercase().trim()
            val to = toCurrency.uppercase().trim()
            val isCryptoOperation = from == "BTC" || to == "BTC"

            if (isCryptoOperation) {
                val isBuyBtc = to == "BTC"
                val direction = if (isBuyBtc) "BUY_BTC" else "SELL_BTC"
                val currency = if (isBuyBtc) from else to
                val cryptoReq = com.example.data.model.CryptoExchangeRequest(
                    direction = direction,
                    currency = currency,
                    amount = amount,
                    pin = pin.trim()
                )
                val resp = try {
                    apiService.cryptoExchange(cryptoReq)
                } catch (_: Exception) {
                    apiService.cryptoExchangeV1(cryptoReq)
                }

                if (resp.isSuccessful && resp.body() != null) {
                    val body = resp.body()!!
                    if (body.success) {
                        val d = body.data
                        val mapped = com.example.data.model.ExchangeResponse(
                            success = true,
                            reference = body.reference ?: "BTC-EX-${System.currentTimeMillis()}",
                            amount = d?.fiatAmount ?: body.amount ?: amount,
                            fromCurrency = from,
                            toCurrency = to,
                            rate = d?.exchangeRate ?: body.rate ?: 100000.0,
                            toAmount = d?.cryptoAmount ?: body.toAmount ?: (if (isBuyBtc) amount / 100000.0 else amount * 100000.0),
                            fee = d?.fee ?: body.feeFlat ?: 0.0,
                            message = body.message ?: "Échange BTC effectué avec succès !"
                        )
                        return Result.success(mapped)
                    } else {
                        return Result.failure(Exception(body.error ?: body.message ?: "Échec de l'échange de cryptomonnaie."))
                    }
                }
            }

            val request = com.example.data.model.ExchangeRequest(
                amount = amount,
                fromCurrency = from,
                toCurrency = to,
                pin = pin.trim()
            )
            val response = apiService.exchange(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body.error ?: body.message ?: "Échec de l'échange de devises."))
                }
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors de l'opération de change.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchTransactionsApi(
        userId: Long,
        limit: Int? = 50,
        offset: Int? = 0,
        from: String? = null,
        to: String? = null,
        direction: String? = null
    ): Result<TransactionsResponse> {
        return try {
            val response = apiService.getTransactions(userId, limit, offset, from, to, direction)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.transactions.isNotEmpty()) {
                    val entities = body.transactions.map { dto ->
                        val isCpkRecover = dto.type?.uppercase() in listOf(
                            "RW", "CPK_RECOVER", "CPK_CLAIM", "CPK_RECOVERY", "ENCAISSEMENT", "CPK_CASHIN", "CLAIM", "CPK_CANCEL"
                        ) || (dto.description?.contains("encaissement", ignoreCase = true) == true)
                          || (dto.display?.name?.contains("encaissement", ignoreCase = true) == true)

                        val isDepositType = (dto.type?.uppercase() in listOf("DP", "DP_AGENT", "DEPOSIT", "RECHARGE", "MOMO_DEPOSIT")) || isCpkRecover
                        val isIncoming = (dto.direction?.lowercase() in listOf("incoming", "credit")) || isDepositType
                        val isFailed = dto.status?.lowercase() in listOf("failed", "cancelled", "rejected", "error", "echoue", "échec")
                        val correspondent = if (isIncoming) (dto.sender ?: dto.otherUser) else (dto.receiver ?: dto.otherUser)
                        val rawDispName = if (isCpkRecover) "Encaissement clé CPK (Dépôt)" else (correspondent?.fullName ?: dto.display?.name ?: dto.otherUser?.fullName ?: dto.description ?: if (isIncoming) "Virement reçu" else "Transfert envoyé")
                        val dispName = if (isDepositType && isFailed) "Échec de recharge Mobile Money" else rawDispName
                        val userAvatar = correspondent?.profilePhoto ?: dto.display?.avatar ?: dto.otherUser?.profilePhoto
                        val kind = if (isCpkRecover) "service" else (dto.display?.kind ?: if (correspondent != null || dto.otherUser != null) "person" else "service")
                        val recipientWallet = if (isIncoming) (dto.sender?.walletId ?: dto.otherUser?.walletId ?: "") else (dto.receiver?.walletId ?: dto.otherUser?.walletId ?: "")
                        val direction = if (isDepositType || isIncoming) "incoming" else (dto.direction ?: "outgoing")

                        TransactionEntity(
                            id = dto.id,
                            title = dispName,
                            recipient = recipientWallet,
                            amount = dto.amount?.toDoubleOrNull() ?: 0.0,
                            currency = dto.currency ?: "USD",
                            type = dto.type ?: "TR",
                            date = dto.createdAt ?: "",
                            status = dto.status ?: "completed",
                            reference = dto.reference ?: "TR-${dto.id}",
                            direction = direction,
                            displayKind = kind,
                            displayName = dispName,
                            displayAvatar = userAvatar,
                            displayIcon = dto.display?.icon,
                            fee = if (isFailed) 0.0 else (dto.fee?.toDoubleOrNull() ?: 0.0),
                            createdAt = dto.createdAt ?: "",
                            otherUserFullName = correspondent?.fullName ?: dto.otherUser?.fullName,
                            otherUserProfilePhoto = userAvatar,
                            description = dto.description,
                            senderWalletId = dto.sender?.walletId,
                            receiverWalletId = dto.receiver?.walletId
                        )
                    }
                    dao.insertTransactions(entities)
                }
                Result.success(body)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer les transactions")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- CARDS MODULE INTEGRATION ---
    suspend fun getCardCatalog(): Result<com.example.data.model.CardCatalogResponse> {
        return try {
            val response = apiService.getCardCatalog()
            if (response.isSuccessful && response.body() != null) {
                val rawString = response.body()!!.string().trim()
                val list = mutableListOf<com.example.data.model.CardCatalogDto>()
                if (rawString.startsWith("[")) {
                    val array = org.json.JSONArray(rawString)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val p = obj.optDouble("price", obj.optDouble("fee", obj.optDouble("amount", obj.optDouble("total", 0.0))))
                        list.add(
                            com.example.data.model.CardCatalogDto(
                                brand = obj.optString("brand", "visa").lowercase(),
                                type = obj.optString("type", obj.optString("card_type", "virtuelle")).lowercase(),
                                currency = obj.optString("currency", "USD").uppercase(),
                                name = obj.optString("name", "Carte CashPay"),
                                price = p,
                                fee = p,
                                total = p,
                                available = obj.optBoolean("available", true)
                            )
                        )
                    }
                    Result.success(com.example.data.model.CardCatalogResponse(success = true, cards = list))
                } else if (rawString.startsWith("{")) {
                    val json = org.json.JSONObject(rawString)
                    val array = json.optJSONArray("cards") ?: json.optJSONArray("data") ?: json.optJSONArray("catalog")
                    if (array != null) {
                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            val p = obj.optDouble("price", obj.optDouble("fee", obj.optDouble("amount", obj.optDouble("total", 0.0))))
                            list.add(
                                com.example.data.model.CardCatalogDto(
                                    brand = obj.optString("brand", "visa").lowercase(),
                                    type = obj.optString("type", obj.optString("card_type", "virtuelle")).lowercase(),
                                    currency = obj.optString("currency", "USD").uppercase(),
                                    name = obj.optString("name", "Carte CashPay"),
                                    price = p,
                                    fee = p,
                                    total = p,
                                    available = obj.optBoolean("available", true)
                                )
                            )
                        }
                    }
                    Result.success(com.example.data.model.CardCatalogResponse(success = json.optBoolean("success", true), cards = list))
                } else {
                    Result.success(com.example.data.model.CardCatalogResponse(success = true, cards = emptyList()))
                }
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger le catalogue de cartes")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserCards(): Result<List<com.example.data.model.CardInfoDto>> {
        return try {
            if (com.example.data.remote.ApiClient.sessionToken.isNullOrBlank()) {
                val sess = dao.getSessionOnce()
                if (sess != null && !sess.sessionToken.isNullOrBlank()) {
                    com.example.data.remote.ApiClient.sessionToken = sess.sessionToken
                }
            }
            val response = apiService.getUserCards()
            if (response.isSuccessful && response.body() != null) {
                val rawString = response.body()!!.string()
                val list = mutableListOf<com.example.data.model.CardInfoDto>()
                val trimmed = rawString.trim()
                if (trimmed.startsWith("[")) {
                    val array = org.json.JSONArray(trimmed)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        list.add(
                            com.example.data.model.CardInfoDto(
                                id = obj.optString("id", null),
                                brand = obj.optString("brand", "VISA"),
                                cardType = obj.optString("cardType", obj.optString("type", "virtuelle")),
                                currency = obj.optString("currency", "USD"),
                                last4 = obj.optString("last4", "0000"),
                                status = obj.optString("status", "active"),
                                isPrimary = obj.optBoolean("isPrimary", false),
                                cardProviderId = obj.optString("cardProviderId", null)
                            )
                        )
                    }
                } else if (trimmed.startsWith("{")) {
                    val json = org.json.JSONObject(trimmed)
                    val array = json.optJSONArray("cards") ?: json.optJSONArray("data")
                    if (array != null) {
                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            list.add(
                                com.example.data.model.CardInfoDto(
                                    id = obj.optString("id", null),
                                    brand = obj.optString("brand", "VISA"),
                                    cardType = obj.optString("cardType", obj.optString("type", "virtuelle")),
                                    currency = obj.optString("currency", "USD"),
                                    last4 = obj.optString("last4", "0000"),
                                    status = obj.optString("status", "active"),
                                    isPrimary = obj.optBoolean("isPrimary", false),
                                    cardProviderId = obj.optString("cardProviderId", null)
                                )
                            )
                        }
                    }
                }
                Result.success(list)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger vos cartes")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCardFees(
        type: String,
        currency: String,
        brand: String? = null,
        feeCode: String? = null
    ): Result<com.example.data.model.CardFeeResponse> {
        return try {
            val normType = type.trim().lowercase()
            val normCurr = currency.trim().uppercase()
            val normBrand = brand?.trim()?.lowercase()
            val normFeeCode = feeCode?.trim()
            val response = apiService.getCardFees(
                type = normType,
                cardType = normType,
                currency = normCurr,
                brand = normBrand,
                feeCode = normFeeCode,
                code = normFeeCode
            )
            if (response.isSuccessful && response.body() != null) {
                val raw = response.body()!!.string().trim()
                val json = if (raw.isNotBlank() && raw.startsWith("{")) org.json.JSONObject(raw) else org.json.JSONObject()
                val priceVal = when {
                    json.has("price") -> json.optDouble("price")
                    json.has("fee") -> json.optDouble("fee")
                    json.has("frais") -> json.optDouble("frais")
                    json.has("frais_carte") -> json.optDouble("frais_carte")
                    json.has("fraisCarte") -> json.optDouble("fraisCarte")
                    json.has("amount") -> json.optDouble("amount")
                    json.has("cost") -> json.optDouble("cost")
                    json.has("total") -> json.optDouble("total")
                    json.has("cardPrice") -> json.optDouble("cardPrice")
                    json.has("card_price") -> json.optDouble("card_price")
                    json.has("card_fee") -> json.optDouble("card_fee")
                    json.has("cardFee") -> json.optDouble("cardFee")
                    json.has("data") -> {
                        val d = json.optJSONObject("data")
                        when {
                            d != null && d.has("price") -> d.optDouble("price")
                            d != null && d.has("fee") -> d.optDouble("fee")
                            d != null && d.has("frais") -> d.optDouble("frais")
                            d != null && d.has("frais_carte") -> d.optDouble("frais_carte")
                            d != null && d.has("amount") -> d.optDouble("amount")
                            d != null && d.has("cost") -> d.optDouble("cost")
                            d != null && d.has("total") -> d.optDouble("total")
                            d != null && d.has("cardPrice") -> d.optDouble("cardPrice")
                            d != null && d.has("card_fee") -> d.optDouble("card_fee")
                            else -> null
                        }
                    }
                    raw.toDoubleOrNull() != null -> raw.toDouble()
                    else -> null
                }
                val codeVal = json.optString("fee_code", json.optString("feeCode", normFeeCode ?: "")).takeIf { it.isNotBlank() }
                Result.success(
                    com.example.data.model.CardFeeResponse(
                        success = json.optBoolean("success", true),
                        price = priceVal,
                        fee = priceVal,
                        cost = priceVal,
                        amount = priceVal,
                        total = priceVal,
                        currency = json.optString("currency", normCurr),
                        feeCode = codeVal ?: normFeeCode
                    )
                )
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer les frais de carte")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun purchaseCard(
        brand: String,
        cardType: String,
        currency: String,
        pin: String
    ): Result<com.example.data.model.CardActionResponse> {
        return try {
            val normBrand = brand.trim().lowercase()
            val normType = cardType.trim().lowercase()
            val normCurr = currency.trim().uppercase()
            val req = com.example.data.model.PurchaseCardRequest(
                brand = normBrand,
                cardType = normType,
                currency = normCurr,
                pin = pin.trim()
            )
            val response = apiService.purchaseCard(req)
            if (response.isSuccessful && response.body() != null) {
                val raw = response.body()!!.string().trim()
                if (raw.isNotBlank() && raw.startsWith("{")) {
                    val json = org.json.JSONObject(raw)
                    if (json.optBoolean("success", true) == false && json.has("error")) {
                        return Result.failure(Exception(json.optString("error", "Échec de l’achat de la carte.")))
                    }
                    val msg = json.optString("message", "Achat de la carte effectué avec succès.")
                    Result.success(com.example.data.model.CardActionResponse(success = true, message = msg))
                } else {
                    Result.success(com.example.data.model.CardActionResponse(success = true, message = "Achat de carte effectué avec succès."))
                }
            } else {
                val rawError = response.errorBody()?.string()
                val fallbackMsg = when (response.code()) {
                    401 -> "Identification requise."
                    else -> "Échec de l’achat de la carte."
                }
                val msg = extractErrorMessage(rawError, fallbackMsg)
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun activateCard(id: String): Result<com.example.data.model.CardActionResponse> {
        return try {
            val response = apiService.activateCard(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'activer la carte")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun freezeCard(id: String): Result<com.example.data.model.CardActionResponse> {
        return try {
            val response = apiService.freezeCard(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de geler la carte")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unfreezeCard(id: String): Result<com.example.data.model.CardActionResponse> {
        return try {
            val response = apiService.unfreezeCard(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de dégeler la carte")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changeCardPin(id: String, pin: String): Result<com.example.data.model.CardActionResponse> {
        return try {
            val response = apiService.changeCardPin(id, mapOf("pin" to pin))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de modifier le code PIN")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun terminateCard(id: String): Result<com.example.data.model.CardActionResponse> {
        return try {
            val response = apiService.terminateCard(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de résilier la carte")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCardFunding(
        id: String,
        type: String,
        cpkSignature: String? = null,
        cpkPin: String? = null
    ): Result<com.example.data.model.CardActionResponse> {
        return try {
            val req = com.example.data.model.FundingConfigRequest(
                type = type,
                cpkSignature = cpkSignature,
                cpkPin = cpkPin
            )
            val response = apiService.updateCardFunding(id, req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur de configuration de financement")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun revealCardDetails(id: String, pin: String): Result<com.example.data.model.CardDetailsResponse> {
        return try {
            val response = apiService.revealCardDetails(id, mapOf("pin" to pin))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Code PIN incorrect")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun biometricAction(
        action: String,
        credentialId: String? = null,
        publicKey: String? = null,
        deviceName: String? = null,
        signature: String? = null
    ): Result<com.example.data.model.BiometricActionResponse> {
        return try {
            ensureSessionToken()
            val request = com.example.data.model.BiometricActionRequest(
                action = action,
                credentialId = credentialId,
                publicKey = publicKey,
                deviceName = deviceName,
                signature = signature
            )
            val response = apiService.biometricAction(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors de l'opération biométrique")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProfileByPhone(phone: String): Result<com.example.data.model.PublicProfileResponse> {
        return try {
            val digits = phone.filter { it.isDigit() }
            val queryPhone = if (digits.length >= 9) digits.takeLast(9) else digits
            val candidates = mutableListOf<String>()
            if (digits.isNotBlank()) {
                candidates.add(digits)
                candidates.add("+$digits")
            }
            if (queryPhone.length == 9) {
                candidates.add("243$queryPhone")
                candidates.add("+243$queryPhone")
                candidates.add(queryPhone)
                candidates.add("0$queryPhone")
            }

            var matchedResponse: retrofit2.Response<com.example.data.model.PublicProfileResponse>? = null
            for (candidate in candidates.distinct()) {
                try {
                    val resp = apiService.searchProfileByPhone(candidate)
                    if (resp.isSuccessful && resp.body()?.found == true && resp.body()?.profile != null) {
                        matchedResponse = resp
                        break
                    }
                } catch (_: Exception) {}
            }

            if (matchedResponse == null && digits.isNotBlank()) {
                try {
                    val searchResp = apiService.searchProfile(digits)
                    if (searchResp.isSuccessful && searchResp.body()?.found == true && searchResp.body()?.profile != null) {
                        matchedResponse = searchResp
                    }
                } catch (_: Exception) {}
            }

            if (matchedResponse != null && matchedResponse.isSuccessful && matchedResponse.body() != null) {
                val body = matchedResponse.body()!!
                val profile = body.profile
                val resolvedProfile = if (profile != null) {
                    val photo = profile.profilePhotoUrl ?: profile.profilePhoto
                    val fullPhotoUrl = when {
                        !photo.isNullOrBlank() && (photo.startsWith("http://") || photo.startsWith("https://")) -> photo
                        !photo.isNullOrBlank() && photo.startsWith("/") -> "https://app.cashpay-all.com$photo"
                        !photo.isNullOrBlank() -> "https://app.cashpay-all.com/$photo"
                        else -> null
                    }
                    profile.copy(profilePhotoUrl = fullPhotoUrl, profilePhoto = fullPhotoUrl)
                } else null
                Result.success(body.copy(profile = resolvedProfile))
            } else {
                Result.failure(Exception("Profil introuvable pour $phone"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProfileByWallet(walletId: String): Result<com.example.data.model.PublicProfileResponse> {
        val trimmed = walletId.trim()
        if (trimmed.isBlank()) return Result.failure(Exception("Identifiant vide"))

        // 1. Direct GET /api/v1/profile/by-wallet
        try {
            val directRes = apiService.searchProfileByWallet(trimmed)
            if (directRes.isSuccessful && directRes.body() != null && directRes.body()?.found == true) {
                val body = directRes.body()!!
                val profile = body.profile
                val resolvedProfile = if (profile != null) {
                    val photo = profile.profilePhotoUrl ?: profile.profilePhoto
                    val fullPhotoUrl = when {
                        !photo.isNullOrBlank() && (photo.startsWith("http://") || photo.startsWith("https://")) -> photo
                        !photo.isNullOrBlank() && photo.startsWith("/") -> "https://app.cashpay-all.com$photo"
                        !photo.isNullOrBlank() -> "https://app.cashpay-all.com/$photo"
                        else -> null
                    }
                    profile.copy(profilePhotoUrl = fullPhotoUrl, profilePhoto = fullPhotoUrl)
                } else null
                return Result.success(body.copy(profile = resolvedProfile))
            }
        } catch (_: Exception) {}

        // 2. Direct GET /api/v1/profile/search
        try {
            val searchRes = apiService.searchProfile(trimmed)
            if (searchRes.isSuccessful && searchRes.body() != null && searchRes.body()?.found == true) {
                val body = searchRes.body()!!
                val profile = body.profile
                val resolvedProfile = if (profile != null) {
                    val photo = profile.profilePhotoUrl ?: profile.profilePhoto
                    val fullPhotoUrl = when {
                        !photo.isNullOrBlank() && (photo.startsWith("http://") || photo.startsWith("https://")) -> photo
                        !photo.isNullOrBlank() && photo.startsWith("/") -> "https://app.cashpay-all.com$photo"
                        !photo.isNullOrBlank() -> "https://app.cashpay-all.com/$photo"
                        else -> null
                    }
                    profile.copy(profilePhotoUrl = fullPhotoUrl, profilePhoto = fullPhotoUrl)
                } else null
                return Result.success(body.copy(profile = resolvedProfile))
            }
        } catch (_: Exception) {}

        // 3. Try agent deposit identify endpoint (identifies clients by wallet ID or ref)
        try {
            val identifyReq = com.example.data.model.AgentDepositRequest(
                step = "identify",
                clientWalletId = trimmed,
                clientRef = trimmed
            )
            val identifyRes = executeAgentDeposit(identifyReq)
            if (identifyRes.isSuccess && identifyRes.getOrNull()?.client != null) {
                val client = identifyRes.getOrNull()!!.client!!
                val photo = client.profilePhotoUrl
                val fullPhotoUrl = when {
                    !photo.isNullOrBlank() && (photo.startsWith("http://") || photo.startsWith("https://")) -> photo
                    !photo.isNullOrBlank() && photo.startsWith("/") -> "https://app.cashpay-all.com$photo"
                    !photo.isNullOrBlank() -> "https://app.cashpay-all.com/$photo"
                    else -> null
                }
                val profileDto = com.example.data.model.PublicProfileDto(
                    walletId = client.walletId ?: trimmed,
                    fullName = client.fullName ?: client.firstName ?: trimmed,
                    role = client.role,
                    profilePhotoUrl = fullPhotoUrl,
                    profilePhoto = fullPhotoUrl
                )
                return Result.success(com.example.data.model.PublicProfileResponse(
                    success = true,
                    found = true,
                    profile = profileDto
                ))
            }
        } catch (_: Exception) {}

        // 2. Try client withdraw start action (resolves client wallet ID or ref)
        try {
            val wReq = com.example.data.model.WithdrawActionRequest(
                operation = "client_withdraw",
                action = "start",
                identifier = trimmed,
                clientWalletId = trimmed
            )
            val wRes = withdrawAction(wReq)
            if (wRes.isSuccess && wRes.getOrNull()?.target != null) {
                val target = wRes.getOrNull()!!.target!!
                val photo = target.avatar
                val fullPhotoUrl = when {
                    !photo.isNullOrBlank() && (photo.startsWith("http://") || photo.startsWith("https://")) -> photo
                    !photo.isNullOrBlank() && photo.startsWith("/") -> "https://app.cashpay-all.com$photo"
                    !photo.isNullOrBlank() -> "https://app.cashpay-all.com/$photo"
                    else -> null
                }
                return Result.success(com.example.data.model.PublicProfileResponse(
                    success = true,
                    found = true,
                    profile = com.example.data.model.PublicProfileDto(
                        walletId = target.walletId ?: trimmed,
                        fullName = target.fullName ?: target.firstName ?: trimmed,
                        role = target.role,
                        profilePhotoUrl = fullPhotoUrl,
                        profilePhoto = fullPhotoUrl
                    )
                ))
            }
        } catch (_: Exception) {}

        // 3. Fallback to phone search if it has digits
        val digits = trimmed.filter { it.isDigit() }
        if (digits.length >= 8) {
            val phoneRes = searchProfileByPhone(trimmed)
            if (phoneRes.isSuccess && phoneRes.getOrNull()?.found == true) {
                return phoneRes
            }
        }

        // 4. Try loan target query
        try {
            val loanRes = getAgentLoanTarget(trimmed)
            if (loanRes.isSuccess && loanRes.getOrNull()?.client != null) {
                val c = loanRes.getOrNull()!!.client!!
                return Result.success(com.example.data.model.PublicProfileResponse(
                    success = true,
                    found = true,
                    profile = com.example.data.model.PublicProfileDto(
                        walletId = c.walletId ?: trimmed,
                        fullName = c.name ?: trimmed,
                        role = "client",
                        profilePhotoUrl = null,
                        profilePhoto = null
                    )
                ))
            }
        } catch (_: Exception) {}

        return Result.failure(Exception("Portefeuille ou utilisateur introuvable."))
    }

    suspend fun previewWithdrawal(
        method: String,
        amount: Double,
        currency: String,
        operator: String? = null
    ): Result<com.example.data.model.WithdrawalPreviewResponse> {
        return try {
            val response = apiService.previewWithdrawal(method, amount, currency, operator)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de prévisualiser le retrait")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmWithdrawal(
        method: String,
        agent: String,
        amount: Double,
        currency: String,
        pin: String
    ): Result<com.example.data.model.WithdrawalConfirmResponse> {
        return try {
            val req = com.example.data.model.WithdrawalConfirmRequest(
                method = method,
                agent = agent,
                amount = amount,
                currency = currency,
                pin = pin
            )
            val response = apiService.confirmWithdrawal(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de confirmer le retrait")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- LOANS MODULE ---
    suspend fun getLoanOffer(currency: String): Result<com.example.data.model.LoanOfferResponse> {
        return try {
            val response = apiService.getLoanOffer(currency)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger l'offre de prêt")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLoanCurrent(): Result<com.example.data.model.LoanCurrentResponse> {
        return try {
            val response = apiService.getLoanCurrent()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger le prêt en cours")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLoanHistory(): Result<com.example.data.model.LoanHistoryResponse> {
        return try {
            val response = apiService.getLoanHistory()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger l'historique des prêts")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestLoan(
        amount: Double,
        currency: String,
        durationMonths: Int,
        repaymentFrequency: String = "monthly",
        installmentCount: Int
    ): Result<com.example.data.model.LoanRequestResponse> {
        return try {
            val req = com.example.data.model.LoanRequest(
                amount = amount,
                currency = currency,
                durationMonths = durationMonths,
                repaymentFrequency = repaymentFrequency,
                installmentCount = installmentCount
            )
            val response = apiService.requestLoan(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors de la demande de prêt")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun repayLoan(loanId: Long, amount: Double): Result<com.example.data.model.LoanRepayResponse> {
        return try {
            val req = com.example.data.model.LoanRepayRequest(loanId, amount)
            val response = apiService.repayLoan(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors du remboursement du prêt")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun repayLoanInstallment(loanId: Long, installmentId: Long): Result<com.example.data.model.LoanRepayInstallmentResponse> {
        return try {
            val req = com.example.data.model.LoanRepayInstallmentRequest(loanId, installmentId)
            val response = apiService.repayLoanInstallment(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors du remboursement de l'échéance")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- FORGOT PIN & AGENT ACTIVATION ---
    suspend fun forgotPinRequest(phone: String, channel: String): Result<com.example.data.model.ForgotPinRequestResponse> {
        return try {
            val req = com.example.data.model.ForgotPinRequestDto(phone = phone, channel = channel)
            val response = apiService.forgotPinRequest(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'envoyer la demande de réinitialisation PIN.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPinVerify(userId: Long, otp: String): Result<com.example.data.model.ForgotPinVerifyResponse> {
        return try {
            val req = com.example.data.model.ForgotPinVerifyDto(userId = userId, otp = otp)
            val response = apiService.forgotPinVerify(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Code de vérification invalide ou expiré.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPinReset(userId: Long, otp: String, newPin: String, confirmPin: String): Result<com.example.data.model.ForgotPinResetResponse> {
        return try {
            val req = com.example.data.model.ForgotPinResetDto(userId = userId, otp = otp, newPin = newPin, confirmPin = confirmPin)
            val response = apiService.forgotPinReset(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Échec de la réinitialisation du PIN.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun activateAgent(plan: String, pin: String): Result<com.example.data.model.ActivateAgentResponse> {
        return try {
            val req = com.example.data.model.ActivateAgentRequest(plan = plan, pin = pin)
            val response = apiService.activateAgent(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    val existing = dao.getUserProfileOnce()
                    if (existing != null) {
                        dao.insertUserProfile(existing.copy(role = "agent"))
                    }
                }
                Result.success(body)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Activation Agent impossible.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeAgentDeposit(request: com.example.data.model.AgentDepositRequest): Result<com.example.data.model.AgentDepositResponse> {
        return try {
            val response = apiService.agentDeposit(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val rawError = response.errorBody()?.string()
                val parsedError = try {
                    if (!rawError.isNullOrBlank() && rawError.trim().startsWith("{")) {
                        val json = JSONObject(rawError)
                        val errMsg = json.optString("error", json.optString("message", "Opération de dépôt impossible."))
                        val previewObj = json.optJSONObject("preview")
                        val preview = if (previewObj != null) {
                            com.example.data.model.AgentDepositPreviewDto(
                                amount = if (previewObj.has("amount")) previewObj.optDouble("amount") else null,
                                fee = previewObj.optDouble("fee", 0.0),
                                totalDebit = if (previewObj.has("totalDebit")) previewObj.optDouble("totalDebit") else null,
                                balance = if (previewObj.has("balance")) previewObj.optDouble("balance") else null,
                                missingAmount = if (previewObj.has("missingAmount")) previewObj.optDouble("missingAmount") else null,
                                currency = previewObj.optString("currency", request.currency ?: "CDF"),
                                message = previewObj.optString("message", errMsg)
                            )
                        } else null

                        com.example.data.model.AgentDepositResponse(
                            success = false,
                            error = errMsg,
                            preview = preview,
                            step = if (json.has("step")) json.optString("step") else request.step,
                            nextStep = if (json.has("nextStep")) json.optString("nextStep") else request.step
                        )
                    } else null
                } catch (_: Exception) { null }

                if (parsedError != null) {
                    Result.success(parsedError)
                } else {
                    val msg = extractErrorMessage(rawError, "Opération de dépôt impossible.")
                    Result.failure(Exception(msg))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun agentDeposit(clientRef: String, amount: Double, currency: String, pin: String): Result<com.example.data.model.AgentDepositResponse> {
        return executeAgentDeposit(
            com.example.data.model.AgentDepositRequest(
                step = "pin",
                clientWalletId = clientRef,
                clientRef = clientRef,
                amount = amount,
                currency = currency,
                pin = pin
            )
        )
    }

    suspend fun initiateAgentWithdraw(clientRef: String, amount: Double, currency: String, channel: String): Result<com.example.data.model.AgentWithdrawResponse> {
        return try {
            val req = com.example.data.model.AgentWithdrawRequest(
                clientRef = clientRef,
                amount = amount,
                currency = currency,
                authMethod = "otp_request",
                authCode = channel // Sending channel as authCode for initiation step
            )
            val response = apiService.agentWithdraw(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'initier le retrait.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmAgentWithdraw(clientRef: String, amount: Double, currency: String, otp: String): Result<com.example.data.model.AgentWithdrawResponse> {
        return try {
            val req = com.example.data.model.AgentWithdrawRequest(
                clientRef = clientRef,
                amount = amount,
                currency = currency,
                authMethod = "otp",
                authCode = otp
            )
            val response = apiService.agentWithdraw(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Code OTP invalide ou expiré.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAgentLoanTarget(clientRef: String): Result<com.example.data.model.AgentLoanTargetResponse> {
        return try {
            val response = apiService.getAgentLoanTarget(clientRef)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Client introuvable.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun agentLoanRepay(clientRef: String, currency: String, amount: Double, pin: String): Result<com.example.data.model.AgentLoanRepaymentResponse> {
        return try {
            val req = com.example.data.model.AgentLoanRepaymentRequest(clientRef = clientRef, currency = currency, amount = amount, pin = pin)
            val response = apiService.agentLoanRepay(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Remboursement de prêt impossible.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAgentCommissions(): Result<com.example.data.model.AgentCommissionsResponse> {
        return try {
            val response = apiService.getAgentCommissions()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer les commissions.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun transferAgentCommission(
        currency: String,
        amount: Double,
        pin: String
    ): Result<com.example.data.model.AgentCommissionTransferResponse> {
        return try {
            val req = com.example.data.model.AgentCommissionTransferRequest(
                currency = currency,
                amount = amount,
                pin = pin
            )
            val response = apiService.transferAgentCommission(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Bascule de commission impossible.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun withdrawAction(request: com.example.data.model.WithdrawActionRequest): Result<com.example.data.model.WithdrawActionResponse> {
        return try {
            val response = apiService.withdrawAction(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors de l'opération de retrait")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- SHOPPING AGENT REPOSITORY METHODS ---

    suspend fun getShoppingProducts(): Result<com.example.data.model.ShoppingContextResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.getShoppingProducts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger les produits Shopping.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun publishProduct(request: com.example.data.model.PublishProductRequest): Result<com.example.data.model.ProductResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.publishProduct(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors de la publication du produit.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProduct(productId: String): Result<com.example.data.model.ProductResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.getProduct(productId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Produit introuvable.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(productId: String, request: com.example.data.model.PublishProductRequest): Result<com.example.data.model.ProductResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.updateProduct(productId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de modifier le produit.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<com.example.data.model.GenericShoppingResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.deleteProduct(productId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de supprimer le produit.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductReference(productId: String): Result<com.example.data.model.ProductReferenceResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.getProductReference(productId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer la référence.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShoppingBoutiques(): Result<com.example.data.model.BoutiquesResponse> {
        return try {
            ensureSessionToken()
            val profile = dao.getUserProfileOnce()
            val sess = dao.getSessionOnce()
            val walletId = profile?.walletId ?: sess?.walletId ?: com.example.data.remote.ApiClient.currentWalletId
            val allBoutiques = mutableListOf<com.example.data.model.BoutiqueDto>()

            // 1. Try GET /api/v1/shopping/boutiques
            try {
                val response = apiService.getShoppingBoutiques(walletId = walletId)
                if (response.isSuccessful && response.body() != null) {
                    val raw = response.body()!!.string()
                    val parsed = com.example.data.model.parseBoutiquesFromJson(raw)
                    allBoutiques.addAll(parsed)
                }
            } catch (e: Exception) {
                android.util.Log.w("CashPayShopping", "Shopping boutiques fetch: ${e.message}")
            }

            // 2. If empty, try GET /api/v1/agent/boutiques
            if (allBoutiques.isEmpty()) {
                try {
                    val agentBoutiquesResp = apiService.getAgentBoutiques(walletId = walletId)
                    if (agentBoutiquesResp.isSuccessful && agentBoutiquesResp.body() != null) {
                        val raw = agentBoutiquesResp.body()!!.string()
                        val parsed = com.example.data.model.parseBoutiquesFromJson(raw)
                        allBoutiques.addAll(parsed)
                    }
                } catch (_: Exception) {}
            }

            // 3. Fallback: Check Agent's Public Profile (searchProfileByWallet returns agent.boutiques)
            if (allBoutiques.isEmpty() && !walletId.isNullOrBlank()) {
                try {
                    val profRes = searchProfileByWallet(walletId)
                    profRes.getOrNull()?.profile?.boutiques?.let {
                        if (it.isNotEmpty()) allBoutiques.addAll(it)
                    }
                } catch (_: Exception) {}
            }

            // 4. Fallback: Extract existing boutiques from agent products context
            if (allBoutiques.isEmpty()) {
                try {
                    val prodsRes = getShoppingProducts()
                    prodsRes.getOrNull()?.let { ctx ->
                        if (ctx.boutiques.isNotEmpty()) {
                            allBoutiques.addAll(ctx.boutiques)
                        } else {
                            val extracted = ctx.products.mapNotNull { p ->
                                p.boutique?.let { b ->
                                    com.example.data.model.BoutiqueDto(
                                        rawId = b.id ?: p.boutiqueId,
                                        directName = b.name ?: p.boutiqueName ?: "Boutique",
                                        address = b.address,
                                        whatsappPublicNumber = b.whatsappPublicNumber
                                    )
                                }
                            }
                            allBoutiques.addAll(extracted)
                        }
                    }
                } catch (_: Exception) {}
            }

            // Deduplicate boutiques by ID or Name
            val distinctBoutiques = allBoutiques.distinctBy {
                if (it.id.isNotBlank()) it.id else it.name
            }

            Result.success(
                com.example.data.model.BoutiquesResponse(
                    success = true,
                    boutiquesList = distinctBoutiques
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBoutique(name: String, address: String? = null, whatsappNumber: String? = null): Result<com.example.data.model.BoutiqueResponse> {
        return try {
            ensureSessionToken()
            val cleanWa = if (whatsappNumber.isNullOrBlank()) null else whatsappNumber.trim()
            val cleanAddr = if (address.isNullOrBlank()) null else address.trim()
            val req = com.example.data.model.CreateBoutiqueRequest(
                name = name.trim(),
                address = cleanAddr,
                whatsapp = cleanWa
            )
            val response = apiService.createBoutique(req)
            if (response.isSuccessful && response.body() != null) {
                val raw = response.body()!!.string()
                val parsed = com.example.data.model.parseSingleBoutiqueFromJson(raw) ?: com.example.data.model.BoutiqueDto(
                    rawId = System.currentTimeMillis().toString(),
                    directName = name.trim(),
                    address = cleanAddr,
                    whatsappPublicNumber = cleanWa
                )
                Result.success(
                    com.example.data.model.BoutiqueResponse(
                        success = true,
                        boutiqueItem = parsed,
                        message = "Boutique créée avec succès !"
                    )
                )
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de créer la boutique.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBoutique(boutiqueId: String): Result<com.example.data.model.BoutiqueResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.getBoutique(boutiqueId)
            if (response.isSuccessful && response.body() != null) {
                val raw = response.body()!!.string()
                val parsed = com.example.data.model.parseSingleBoutiqueFromJson(raw)
                Result.success(
                    com.example.data.model.BoutiqueResponse(
                        success = true,
                        boutiqueItem = parsed
                    )
                )
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Boutique introuvable.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBoutique(boutiqueId: String, name: String, address: String? = null, whatsappNumber: String? = null): Result<com.example.data.model.BoutiqueResponse> {
        return try {
            ensureSessionToken()
            val cleanWa = if (whatsappNumber.isNullOrBlank()) null else whatsappNumber.trim()
            val cleanAddr = if (address.isNullOrBlank()) null else address.trim()
            val req = com.example.data.model.CreateBoutiqueRequest(
                name = name.trim(),
                address = cleanAddr,
                whatsapp = cleanWa
            )
            val response = apiService.updateBoutique(boutiqueId, req)
            if (response.isSuccessful && response.body() != null) {
                val raw = response.body()!!.string()
                val parsed = com.example.data.model.parseSingleBoutiqueFromJson(raw) ?: com.example.data.model.BoutiqueDto(
                    rawId = boutiqueId,
                    directName = name.trim(),
                    address = cleanAddr,
                    whatsappPublicNumber = cleanWa
                )
                Result.success(
                    com.example.data.model.BoutiqueResponse(
                        success = true,
                        boutiqueItem = parsed,
                        message = "Boutique mise à jour avec succès !"
                    )
                )
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de modifier la boutique.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBoutique(boutiqueId: String): Result<com.example.data.model.GenericShoppingResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.deleteBoutique(boutiqueId)
            if (response.isSuccessful) {
                Result.success(
                    com.example.data.model.GenericShoppingResponse(
                        success = true,
                        message = "Boutique supprimée avec succès."
                    )
                )
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de supprimer la boutique.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPublicUserProducts(userId: String, storeId: String? = null): Result<com.example.data.model.PublicCatalogResponse> {
        return try {
            val response = apiService.getPublicUserProducts(userId, storeId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Catalogue public introuvable.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- DEPOSIT OPERATIONS (MaxiCash Gateway) ---
    suspend fun depositMobileMoney(
        amount: Double,
        currency: String = "USD",
        operator: String,
        phoneNumber: String,
        countryCode: String = "CD"
    ): Result<com.example.data.model.DepositMobileMoneyResponse> {
        return try {
            ensureSessionToken()
            val req = com.example.data.model.DepositMobileMoneyRequest(
                method = "mobile_money",
                amount = amount,
                currency = currency,
                operator = operator,
                phoneNumber = phoneNumber,
                countryCode = countryCode
            )
            val response = apiService.depositMobileMoney(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    val msg = body.error ?: body.message ?: "Échec du dépôt Mobile Money."
                    Result.failure(Exception(msg))
                }
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors du dépôt Mobile Money.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun depositCard(
        amount: Double,
        currency: String = "USD"
    ): Result<com.example.data.model.DepositGatewayResponse> {
        return try {
            ensureSessionToken()
            val req = com.example.data.model.DepositGatewayRequest(
                amount = amount,
                currency = currency
            )
            val response = apiService.depositCard(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    val msg = body.error ?: "Impossible d'initialiser le paiement par carte."
                    Result.failure(Exception(msg))
                }
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'initialiser le paiement par carte.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun depositPayPal(
        amount: Double,
        currency: String = "USD"
    ): Result<com.example.data.model.DepositGatewayResponse> {
        return try {
            ensureSessionToken()
            val req = com.example.data.model.DepositGatewayRequest(
                amount = amount,
                currency = currency
            )
            val response = apiService.depositPayPal(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    val msg = body.error ?: "Impossible d'initialiser le paiement PayPal."
                    Result.failure(Exception(msg))
                }
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'initialiser le paiement PayPal.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- RATES / EXCHANGE RATES API (Temps Réel depuis la Base de Données) ---
    suspend fun getExchangeRates(countryCode: String? = null): Result<com.example.data.model.ExchangeRatesResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val cCode = countryCode?.uppercase()?.trim() ?: "CD"

                // 1. Détection temps réel directement depuis la base de données via Next.js Server Action
                val liveActionRates = fetchRatesViaServerAction(cCode)
                if (liveActionRates != null && !liveActionRates.rates.isNullOrEmpty()) {
                    return@withContext Result.success(liveActionRates)
                }

                // 2. Appel de l'API REST si le serveur expose l'endpoint
                try {
                    val response = apiService.getExchangeRates(cCode)
                    if (response.isSuccessful && response.body() != null && response.body()!!.success && !response.body()!!.rates.isNullOrEmpty()) {
                        return@withContext Result.success(response.body()!!)
                    }
                } catch (_: Exception) {}

                // Aucun taux inventé ou codé en dur : on retourne la structure avec les taux réels disponibles ou vide
                Result.success(
                    com.example.data.model.ExchangeRatesResponse(
                        success = true,
                        countryCode = cCode,
                        rates = emptyMap()
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun fetchRatesViaServerAction(countryCode: String): com.example.data.model.ExchangeRatesResponse? {
        return try {
            val requestBody = "[]".toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://app.cashpay-all.com/exchange")
                .header("Next-Action", "8fe3dd5a7a3b30ef3f0a1b2e0f5914277339742b")
                .header("Content-Type", "application/json")
                .header("Accept", "text/x-component, application/json")
                .post(requestBody)
                .build()

            val response = com.example.data.remote.ApiClient.okHttpClient.newCall(request).execute()
            val raw = response.body?.string() ?: return null
            val line1 = raw.lines().find { it.startsWith("1:") }?.substringAfter("1:")
                ?: raw.substringAfter("1:").takeIf { it.isNotBlank() }
                ?: return null

            val parsedJson = org.json.JSONTokener(line1).nextValue()
            val ratesMap = mutableMapOf<String, Double>()

            if (parsedJson is org.json.JSONArray) {
                for (i in 0 until parsedJson.length()) {
                    val item = parsedJson.optJSONObject(i) ?: continue
                    val from = item.optString("from_currency").ifBlank { item.optString("from") }.uppercase()
                    val to = item.optString("to_currency").ifBlank { item.optString("to") }.uppercase()
                    val rate = item.optDouble("rate", 0.0)
                    if (from.isNotBlank() && to.isNotBlank() && rate > 0.0) {
                        ratesMap["${from}_${to}"] = rate
                        if (!ratesMap.containsKey("${to}_${from}")) {
                            ratesMap["${to}_${from}"] = 1.0 / rate
                        }
                    }
                }
            } else if (parsedJson is JSONObject) {
                val innerRates = if (parsedJson.has("rates")) parsedJson.optJSONObject("rates") else parsedJson
                innerRates?.let { obj ->
                    val keys = obj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        ratesMap[key.uppercase()] = obj.optDouble(key, 0.0)
                    }
                }
            }

            if (ratesMap.isNotEmpty()) {
                com.example.data.model.ExchangeRatesResponse(
                    success = true,
                    countryCode = countryCode,
                    rates = ratesMap
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getRealtimeQuote(amount: Double, fromCurrency: String, toCurrency: String): Result<com.example.data.model.RealtimeQuoteDto> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val from = fromCurrency.uppercase().trim()
                val to = toCurrency.uppercase().trim()
                if (from == to) {
                    return@withContext Result.success(
                        com.example.data.model.RealtimeQuoteDto(
                            fromAmount = amount,
                            fromCurrency = from,
                            toAmount = amount,
                            toCurrency = to,
                            rate = 1.0,
                            quoteId = "same-currency"
                        )
                    )
                }

                val requestBody = "[$amount,\"$from\",\"$to\"]".toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("https://app.cashpay-all.com/exchange")
                    .header("Next-Action", "8ec9edd95f43bb8c254e071f124725b502bc381b")
                    .header("Content-Type", "application/json")
                    .header("Accept", "text/x-component, application/json")
                    .post(requestBody)
                    .build()

                val response = com.example.data.remote.ApiClient.okHttpClient.newCall(request).execute()
                val raw = response.body?.string() ?: ""
                val line1 = raw.lines().find { it.startsWith("1:") }?.substringAfter("1:")
                    ?: raw.substringAfter("1:").takeIf { it.isNotBlank() }
                    ?: raw

                val jsonObject = JSONObject(line1)
                if (jsonObject.optBoolean("success", false) && jsonObject.has("quote")) {
                    val q = jsonObject.getJSONObject("quote")
                    val quoteDto = com.example.data.model.RealtimeQuoteDto(
                        fromAmount = q.optDouble("fromAmount", amount),
                        fromCurrency = q.optString("fromCurrency", from),
                        toAmount = q.optDouble("toAmount", 0.0),
                        toCurrency = q.optString("toCurrency", to),
                        rate = q.optDouble("rate", 0.0),
                        quoteId = q.optString("quoteId", "")
                    )
                    Result.success(quoteDto)
                } else {
                    val err = jsonObject.optString("error", "Taux indisponible pour cette paire dans la base de données.")
                    Result.failure(Exception(err))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // --- AGENT CUSTOMER ONBOARDING (Cahier des charges) ---
    suspend fun getAgentCustomerOptions(country: String? = null): Result<com.example.data.model.AgentCustomerOptionsResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.getAgentCustomerOptions(country = country, countryCode = country)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Result.success(body)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val msg = extractErrorMessage(errorBody, "Impossible de charger les options et le pays de l'Agent depuis l'API (${response.code()}).")
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is java.net.SocketTimeoutException -> "Délai d'attente dépassé (Timeout). Le serveur met trop de temps à répondre, veuillez réessayer."
                    is java.net.UnknownHostException -> "Serveur inaccessible. Veuillez vérifier votre connexion Internet."
                    else -> e.message ?: "Erreur de connexion au serveur CashPay."
                }
                Result.failure(Exception(errorMsg, e))
            }
        }
    }

    suspend fun registerAgentCustomer(
        request: com.example.data.model.AgentRegisterCustomerRequest
    ): Result<com.example.data.model.AgentRegisterCustomerResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.registerAgentCustomer(request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success || body.customer != null || body.error == null) {
                        Result.success(body)
                    } else {
                        val msg = body.error ?: body.message ?: "Échec de l'enregistrement du client par l'Agent."
                        Result.failure(Exception(msg))
                    }
                } else {
                    val msg = extractErrorMessage(response.errorBody()?.string(), "Échec de l'enregistrement du client par l'Agent (${response.code()}).")
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is java.net.SocketTimeoutException -> "Délai d'attente dépassé (Timeout). Veuillez vérifier votre connexion et réessayer."
                    is java.net.UnknownHostException -> "Serveur inaccessible. Veuillez vérifier votre connexion Internet."
                    else -> e.message ?: "Erreur lors de l'enregistrement du client."
                }
                Result.failure(Exception(errorMsg, e))
            }
        }
    }

    suspend fun getAgentCustomers(): Result<com.example.data.model.AgentCustomerListResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.getAgentCustomers()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger la liste des clients de l'Agent.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- INTERNATIONAL TRANSFER CLAIMS (Section Client) ---
    suspend fun createInternationalClaim(request: com.example.data.model.CreateClaimRequest): Result<com.example.data.model.CreateClaimResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.createInternationalClaim(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else if (response.code() == 409) {
                    // Conflict duplicate claim
                    val errString = response.errorBody()?.string()
                    val parsedResponse = try {
                        if (!errString.isNullOrBlank()) {
                            val moshi = com.squareup.moshi.Moshi.Builder()
                                .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                                .build()
                            moshi.adapter(com.example.data.model.CreateClaimResponse::class.java).fromJson(errString)
                        } else null
                    } catch (_: Exception) { null }
                    if (parsedResponse != null) {
                        Result.success(parsedResponse)
                    } else {
                        Result.failure(Exception("Cette réclamation existe déjà."))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val parsedMsg = extractErrorMessage(errorBody, "Erreur (${response.code()}): impossible de soumettre la réclamation")
                    Result.failure(Exception(parsedMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getInternationalClaims(): Result<com.example.data.model.ClaimsListResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.getInternationalClaims()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val parsedMsg = extractErrorMessage(errorBody, "Impossible de charger les réclamations.")
                    Result.failure(Exception(parsedMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getInternationalClaimDetail(id: Long): Result<com.example.data.model.ClaimDetailResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.getInternationalClaimDetail(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val parsedMsg = extractErrorMessage(errorBody, "Détail de réclamation introuvable.")
                    Result.failure(Exception(parsedMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // --- REFERRALS & REWARDS ---
    suspend fun getReferrals(): Result<com.example.data.model.ReferralResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.getReferrals()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val parsedMsg = extractErrorMessage(errorBody, "Impossible de charger les données de parrainage.")
                    Result.failure(Exception(parsedMsg))
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is java.net.SocketTimeoutException -> "Délai d'attente dépassé (Timeout)."
                    is java.net.UnknownHostException -> "Serveur inaccessible. Vérifiez votre connexion Internet."
                    else -> e.message ?: "Erreur de connexion."
                }
                Result.failure(Exception(errorMsg, e))
            }
        }
    }

    suspend fun claimReferralReward(referralId: Long): Result<com.example.data.model.ClaimReferralResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.claimReferralReward(
                    com.example.data.model.ClaimReferralRequest(referralId = referralId)
                )
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body.error ?: body.message ?: "Échec de réclamation de la récompense."))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val parsedMsg = extractErrorMessage(errorBody, "Impossible de réclamer la récompense.")
                    Result.failure(Exception(parsedMsg))
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is java.net.SocketTimeoutException -> "Délai d'attente dépassé (Timeout)."
                    is java.net.UnknownHostException -> "Serveur inaccessible. Vérifiez votre connexion Internet."
                    else -> e.message ?: "Erreur de connexion."
                }
                Result.failure(Exception(errorMsg, e))
            }
        }
    }

    // --- TV SUBSCRIPTIONS (Canal+, EasyTV, BLUS) ---
    suspend fun getTvPackages(provider: String): Result<List<com.example.data.model.TvPackageDto>> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.getTvPackages(provider = provider.trim().uppercase())
                if (response.isSuccessful && response.body() != null) {
                    val raw = response.body()!!.string().trim()
                    val packagesList = mutableListOf<com.example.data.model.TvPackageDto>()
                    if (raw.startsWith("{")) {
                        val json = org.json.JSONObject(raw)
                        val array = json.optJSONArray("packages")
                            ?: json.optJSONArray("data")
                            ?: json.optJSONArray("forfaits")
                        if (array != null) {
                            for (i in 0 until array.length()) {
                                val item = array.getJSONObject(i)
                                val id = item.optLong("id", (i + 1).toLong())
                                val pCode = item.optString("provider_code", item.optString("provider", provider))
                                val pName = item.optString("provider_name", "")
                                val pkgName = item.optString("package_name", item.optString("name", item.optString("title", "Forfait #$id")))
                                val price = item.optDouble("display_price", item.optDouble("price", item.optDouble("amount", 0.0)))
                                val currency = item.optString("display_currency", item.optString("currency", "CDF"))
                                val desc = item.optString("description", "")
                                packagesList.add(
                                    com.example.data.model.TvPackageDto(
                                        id = id,
                                        providerCode = pCode,
                                        providerName = pName,
                                        packageName = pkgName,
                                        displayPrice = price,
                                        displayCurrency = currency,
                                        description = desc
                                    )
                                )
                            }
                        }
                    } else if (raw.startsWith("[")) {
                        val array = org.json.JSONArray(raw)
                        for (i in 0 until array.length()) {
                            val item = array.getJSONObject(i)
                            val id = item.optLong("id", (i + 1).toLong())
                            val pCode = item.optString("provider_code", item.optString("provider", provider))
                            val pName = item.optString("provider_name", "")
                            val pkgName = item.optString("package_name", item.optString("name", "Forfait #$id"))
                            val price = item.optDouble("display_price", item.optDouble("price", item.optDouble("amount", 0.0)))
                            val currency = item.optString("display_currency", item.optString("currency", "CDF"))
                            val desc = item.optString("description", "")
                            packagesList.add(
                                com.example.data.model.TvPackageDto(
                                    id = id,
                                    providerCode = pCode,
                                    providerName = pName,
                                    packageName = pkgName,
                                    displayPrice = price,
                                    displayCurrency = currency,
                                    description = desc
                                )
                            )
                        }
                    }
                    Result.success(packagesList)
                } else {
                    val rawError = response.errorBody()?.string()
                    val msg = when (response.code()) {
                        401 -> "Authentification requise pour consulter les forfaits TV."
                        else -> extractErrorMessage(rawError, "Impossible de récupérer les forfaits TV.")
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is java.net.SocketTimeoutException -> "Délai d'attente dépassé (Timeout)."
                    is java.net.UnknownHostException -> "Serveur inaccessible. Vérifiez votre connexion Internet."
                    else -> e.message ?: "Erreur de connexion lors du chargement des forfaits TV."
                }
                Result.failure(Exception(errorMsg, e))
            }
        }
    }

    suspend fun submitTvSubscription(
        provider: String,
        packageId: Long,
        smartcardNumber: String,
        currency: String,
        pin: String
    ): Result<com.example.data.model.TvSubscriptionResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val req = com.example.data.model.TvSubscriptionRequest(
                    provider = provider.trim().uppercase(),
                    packageId = packageId,
                    smartcardNumber = smartcardNumber.trim(),
                    currency = currency.trim().uppercase(),
                    pin = pin.trim()
                )
                val response = apiService.submitTvSubscription(req)
                if (response.isSuccessful && response.body() != null) {
                    val raw = response.body()!!.string().trim()
                    if (raw.startsWith("{")) {
                        val json = org.json.JSONObject(raw)
                        val isSuccess = json.optBoolean("success", true)
                        if (!isSuccess && json.has("error")) {
                            return@withContext Result.failure(
                                Exception(json.optString("error", "Échec de la demande d'abonnement TV."))
                            )
                        }
                        val ref = json.optString("reference", json.optString("ref", json.optString("transactionId", "CP-TV-${System.currentTimeMillis()}")))
                        val msg = json.optString("message", "Votre demande d'abonnement a été enregistrée.")
                        val status = json.optString("status", "En attente")
                        val debitedAmount = if (json.has("debitedAmount")) json.optDouble("debitedAmount") else if (json.has("amount")) json.optDouble("amount") else null
                        val debitedCurr = json.optString("debitedCurrency", json.optString("currency", currency))
                        val rate = if (json.has("rate")) json.optDouble("rate") else if (json.has("exchangeRate")) json.optDouble("exchangeRate") else null

                        Result.success(
                            com.example.data.model.TvSubscriptionResponse(
                                success = true,
                                message = msg,
                                reference = ref,
                                transactionId = ref,
                                status = status,
                                provider = provider,
                                packageId = packageId,
                                amount = debitedAmount,
                                currency = debitedCurr,
                                rate = rate,
                                debitedAmount = debitedAmount,
                                debitedCurrency = debitedCurr,
                                smartcardNumber = smartcardNumber,
                                date = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.FRANCE).format(java.util.Date())
                            )
                        )
                    } else {
                        Result.success(
                            com.example.data.model.TvSubscriptionResponse(
                                success = true,
                                message = "Votre demande d'abonnement a été enregistrée.",
                                reference = "CP-TV-${System.currentTimeMillis()}",
                                status = "En attente",
                                provider = provider,
                                packageId = packageId,
                                smartcardNumber = smartcardNumber
                            )
                        )
                    }
                } else {
                    val rawError = response.errorBody()?.string()
                    val msg = when (response.code()) {
                        400 -> extractErrorMessage(rawError, "Le solde de votre portefeuille est insuffisant ou les informations sont invalides.")
                        401 -> "Votre session a expiré. Veuillez vous reconnecter."
                        else -> extractErrorMessage(rawError, "Impossible de confirmer le résultat. Vérifiez votre historique avant de recommencer.")
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is java.net.SocketTimeoutException -> "Délai d'attente dépassé (Timeout). Vérifiez votre historique avant de recommencer."
                    is java.net.UnknownHostException -> "Serveur inaccessible. Vérifiez votre connexion Internet."
                    else -> e.message ?: "Erreur de réseau. Vérifiez votre historique avant de recommencer."
                }
                Result.failure(Exception(errorMsg, e))
            }
        }
    }

    // --- REGIDESO PUBLIC INVOICES ---
    suspend fun getRegidesoInvoices(
        abonnementId: String? = null,
        province: String = "KIN"
    ): Result<List<com.example.data.model.RegidesoFactureDto>> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.getRegidesoInvoices(
                    abonnementId = abonnementId?.trim()?.takeIf { it.isNotBlank() },
                    province = province.trim().uppercase()
                )

                if (response.isSuccessful && response.body() != null) {
                    val raw = response.body()!!.string().trim()
                    val facturesList = mutableListOf<com.example.data.model.RegidesoFactureDto>()

                    if (raw.startsWith("[")) {
                        val arr = org.json.JSONArray(raw)
                        for (i in 0 until arr.length()) {
                            val item = arr.optJSONObject(i) ?: continue
                            facturesList.add(parseRegidesoFactureJson(item))
                        }
                    } else if (raw.startsWith("{")) {
                        val json = org.json.JSONObject(raw)
                        val isSuccess = json.optBoolean("success", true)
                        if (!isSuccess && json.has("error")) {
                            val errMsg = json.optString("error", json.optString("message", "Erreur lors de la récupération des factures."))
                            return@withContext Result.failure(Exception(errMsg))
                        }
                        val arr = json.optJSONArray("factures")
                            ?: json.optJSONArray("invoices")
                            ?: json.optJSONArray("data")
                        if (arr != null) {
                            for (i in 0 until arr.length()) {
                                val item = arr.optJSONObject(i) ?: continue
                                facturesList.add(parseRegidesoFactureJson(item))
                            }
                        }
                    }
                    Result.success(facturesList)
                } else {
                    val rawError = response.errorBody()?.string()
                    val msg = when (response.code()) {
                        401 -> "Session expirée. Veuillez vous reconnecter."
                        403 -> "Cet abonnement n'appartient pas à vos abonnements REGIDESO autorisés."
                        400 -> "Paramètre ou code province invalide. Veuillez vérifier vos informations."
                        500 -> "Le service REGIDESO est momentanément indisponible. Veuillez réessayer plus tard."
                        else -> extractErrorMessage(rawError, "Impossible de récupérer les factures REGIDESO.")
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is java.net.SocketTimeoutException -> "Délai d'attente dépassé (Timeout)."
                    is java.net.UnknownHostException -> "Serveur inaccessible. Vérifiez votre connexion Internet."
                    else -> e.message ?: "Erreur de connexion lors de la consultation des factures."
                }
                Result.failure(Exception(errorMsg, e))
            }
        }
    }

    private fun parseRegidesoFactureJson(item: org.json.JSONObject): com.example.data.model.RegidesoFactureDto {
        val num = item.optString("numero_facture", item.optString("numero", item.optString("invoice_number", ""))).takeIf { it.isNotBlank() }
        val nom = item.optString("nom_client", item.optString("client", item.optString("customer_name", ""))).takeIf { it.isNotBlank() }
        val statut = item.optString("statut_facture", item.optString("statut", item.optString("status", "Impayée")))
        val amt = item.optDouble("amount", item.optDouble("montant", 0.0))
        val cur = item.optString("currency", item.optString("devise", "CDF"))
        val per = item.optString("periode", item.optString("period", "")).takeIf { it.isNotBlank() }
        val abId = item.optString("abonnement_id", item.optString("abonnementId", item.optString("subscription_id", ""))).takeIf { it.isNotBlank() }

        return com.example.data.model.RegidesoFactureDto(
            numeroFacture = num,
            nomClient = nom,
            statutFacture = statut,
            amount = amt,
            montant = amt,
            currency = cur,
            periode = per,
            abonnementId = abId
        )
    }

    // =========================================================================
    // --- FINUSECO / CASHPAY CPK FINANCIAL KEYS ENGINE ---
    // =========================================================================

    suspend fun issueCpkKey(
        amount: Double,
        currency: String = "USD",
        securityLevel: String = "none",
        keyPin: String? = null,
        userPin: String
    ): Result<com.example.data.model.CpkIssueResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val req = com.example.data.model.CpkIssueRequest(
                    amount = amount,
                    currency = currency.uppercase(),
                    securityLevel = securityLevel,
                    keyPin = keyPin?.takeIf { it.isNotBlank() },
                    userPin = userPin.trim()
                )
                val response = apiService.issueCpkKey(req)
                if (response.isSuccessful && response.body() != null) {
                    val raw = response.body()!!.string().trim()
                    val json = org.json.JSONObject(raw)
                    val isSuccess = json.optBoolean("success", true)
                    if (!isSuccess && json.has("error")) {
                        return@withContext Result.failure(Exception(json.optString("error", "Échec de l'émission de la clé.")))
                    }

                    val sig = json.optString("signature", json.optString("keySignature", "CPK_${System.currentTimeMillis()}"))
                    val ref = json.optString("reference", json.optString("ref", "CPK-${sig.take(8).uppercase()}"))
                    val issuedAmount = json.optDouble("amount", amount)
                    val issuedCurrency = json.optString("currency", currency)
                    val issuedSec = json.optString("securityLevel", securityLevel)
                    val status = json.optString("status", "active")
                    val fileContent = json.optString("fileContent", raw)

                    val entity = com.example.data.local.CpkKeyEntity(
                        signature = sig,
                        reference = ref,
                        amount = issuedAmount,
                        currency = issuedCurrency,
                        securityLevel = issuedSec,
                        status = status,
                        remainingAmount = issuedAmount,
                        createdAt = json.optString("createdAt", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())),
                        fileName = "CashPay_${sig.take(8)}.cpk",
                        rawJsonContent = if (fileContent.isNotBlank()) fileContent else raw,
                        isCloudSynced = false,
                        isOwner = true
                    )
                    dao.insertCpkKey(entity)

                    // Refresh wallet & transactions to reflect the debit
                    val prof = dao.getUserProfileOnce()
                    if (prof != null) {
                        try { getWallet(prof.id) } catch (_: Exception) {}
                        try { fetchTransactionsApi(prof.id, 1, 20) } catch (_: Exception) {}
                    }

                    Result.success(
                        com.example.data.model.CpkIssueResponse(
                            success = true,
                            signature = sig,
                            reference = ref,
                            amount = issuedAmount,
                            currency = issuedCurrency,
                            securityLevel = issuedSec,
                            status = status,
                            fileContent = fileContent,
                            message = json.optString("message", "Clé financière CPK créée avec succès.")
                        )
                    )
                } else {
                    val rawError = response.errorBody()?.string()
                    val msg = when (response.code()) {
                        401 -> "Session expirée. Veuillez vous reconnecter."
                        403 -> "Code PIN CashPay incorrect ou solde insuffisant."
                        else -> extractErrorMessage(rawError, "Échec de l'émission de la clé CPK.")
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Erreur réseau lors de l'émission de la clé CPK.", e))
            }
        }
    }

    suspend fun syncServerCpkKeys(): Result<List<com.example.data.local.CpkKeyEntity>> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.getCpkKeys()
                if (response.isSuccessful && response.body() != null) {
                    val raw = response.body()!!.string().trim()
                    val resultList = mutableListOf<com.example.data.local.CpkKeyEntity>()
                    val json = org.json.JSONObject(raw)
                    val arr = json.optJSONArray("data")
                        ?: json.optJSONArray("keys")
                        ?: json.optJSONArray("signatures")

                    if (arr != null) {
                        for (i in 0 until arr.length()) {
                            val item = arr.optJSONObject(i) ?: continue
                            val sig = item.optString("signature", item.optString("key_signature", "")).takeIf { it.isNotBlank() } ?: continue
                            val ref = item.optString("reference", "CPK-${sig.take(8).uppercase()}")
                            val amt = item.optDouble("amount", 0.0)
                            val rem = item.optDouble("remainingAmount", item.optDouble("remaining_amount", amt))
                            val cur = item.optString("currency", "USD")
                            val sec = item.optString("securityLevel", item.optString("security_level", "none"))
                            val st = item.optString("status", "active")
                            val created = item.optString("createdAt", item.optString("created_at", ""))

                            val existing = dao.getCpkKeyBySignature(sig)
                            val entity = com.example.data.local.CpkKeyEntity(
                                signature = sig,
                                reference = ref,
                                amount = amt,
                                currency = cur,
                                securityLevel = sec,
                                status = st,
                                remainingAmount = rem,
                                createdAt = created,
                                fileName = existing?.fileName ?: "CashPay_${sig.take(8)}.cpk",
                                rawJsonContent = existing?.rawJsonContent ?: item.toString(),
                                isCloudSynced = existing?.isCloudSynced ?: false,
                                cloudDriveFileId = existing?.cloudDriveFileId,
                                isOwner = true
                            )
                            dao.insertCpkKey(entity)
                            resultList.add(entity)
                        }
                    }
                    Result.success(resultList)
                } else {
                    val rawError = response.errorBody()?.string()
                    Result.failure(Exception(extractErrorMessage(rawError, "Impossible de synchroniser les clés serveur.")))
                }
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Erreur réseau lors de la récupération des clés serveur.", e))
            }
        }
    }

    suspend fun recoverCpkKey(
        signature: String,
        keyPin: String? = null,
        partialAmount: Double? = null
    ): Result<com.example.data.model.CpkRecoverResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val req = com.example.data.model.CpkRecoverRequest(
                    signature = signature.trim(),
                    keyPin = keyPin?.trim()?.takeIf { it.isNotBlank() },
                    partialAmount = partialAmount?.takeIf { it > 0 }
                )
                val response = apiService.recoverCpkKey(req)
                if (response.isSuccessful && response.body() != null) {
                    val raw = response.body()!!.string().trim()
                    val json = org.json.JSONObject(raw)
                    val isSuccess = json.optBoolean("success", true)
                    if (!isSuccess && json.has("error")) {
                        return@withContext Result.failure(Exception(json.optString("error", "Échec de l'encaissement de la clé.")))
                    }

                    val newStatus = json.optString("status", if (partialAmount != null && partialAmount > 0) "partially_used" else "claimed")
                    val remAmt = json.optDouble("remainingAmount", 0.0)
                    dao.updateCpkKeyStatus(signature, newStatus, remAmt)

                    val keyEntity = dao.getCpkKeyBySignature(signature)
                    val recAmt = json.optDouble("amountRecovered", json.optDouble("amount", partialAmount ?: (keyEntity?.amount ?: 0.0)))
                    val txIdStr = json.optString("transactionId", json.optString("reference", "CPK-REC-${System.currentTimeMillis()}"))
                    val nowFmt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                    val keyRef = keyEntity?.reference ?: "CPK-${signature.take(8).uppercase()}"

                    // Insert immediate credit deposit transaction
                    try {
                        val profUser = dao.getUserProfileOnce()
                        val depositTx = TransactionEntity(
                            id = System.currentTimeMillis(),
                            title = "Encaissement clé CPK (Dépôt)",
                            recipient = profUser?.walletId ?: "Mon portefeuille",
                            amount = recAmt,
                            currency = keyEntity?.currency ?: "USD",
                            type = "CPK_RECOVER",
                            date = nowFmt,
                            status = "completed",
                            reference = txIdStr,
                            direction = "incoming",
                            displayKind = "service",
                            displayName = "Encaissement clé USB ($keyRef)",
                            displayAvatar = null,
                            displayIcon = "call_received",
                            fee = 0.0,
                            createdAt = nowFmt,
                            description = "Approvisionnement du compte par encaissement de la clé CPK $keyRef"
                        )
                        dao.insertTransaction(depositTx)
                    } catch (_: Exception) {}

                    // Refresh wallet to reflect the credit
                    val prof = dao.getUserProfileOnce()
                    if (prof != null) {
                        try { getWallet(prof.id) } catch (_: Exception) {}
                        try { fetchTransactionsApi(prof.id, 1, 20) } catch (_: Exception) {}
                    }

                    Result.success(
                        com.example.data.model.CpkRecoverResponse(
                            success = true,
                            message = json.optString("message", "Fonds de la clé CPK récupérés et crédités avec succès."),
                            transactionId = json.optString("transactionId", json.optString("reference", "")),
                            amountRecovered = json.optDouble("amountRecovered", json.optDouble("amount", partialAmount ?: 0.0)),
                            remainingAmount = remAmt,
                            status = newStatus
                        )
                    )
                } else {
                    val rawError = response.errorBody()?.string()
                    val msg = when (response.code()) {
                        401 -> "Session expirée. Veuillez vous reconnecter."
                        403 -> "Code PIN de la clé incorrect ou clé non accessible."
                        404 -> "Clé CPK introuvable sur le serveur."
                        else -> extractErrorMessage(rawError, "Échec de la récupération des fonds de la clé.")
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Erreur réseau lors de l'encaissement.", e))
            }
        }
    }

    suspend fun cancelCpkKey(
        signature: String,
        userPin: String
    ): Result<com.example.data.model.CpkCancelResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val req = com.example.data.model.CpkCancelRequest(
                    signature = signature.trim(),
                    userPin = userPin.trim()
                )
                val response = apiService.cancelCpkKey(req)
                if (response.isSuccessful && response.body() != null) {
                    val raw = response.body()!!.string().trim()
                    val json = org.json.JSONObject(raw)
                    val isSuccess = json.optBoolean("success", true)
                    if (!isSuccess && json.has("error")) {
                        return@withContext Result.failure(Exception(json.optString("error", "Échec de l'annulation de la clé.")))
                    }

                    dao.updateCpkKeyStatus(signature, "cancelled", 0.0)

                    val prof = dao.getUserProfileOnce()
                    if (prof != null) {
                        try { getWallet(prof.id) } catch (_: Exception) {}
                        try { fetchTransactionsApi(prof.id, 1, 20) } catch (_: Exception) {}
                    }

                    Result.success(
                        com.example.data.model.CpkCancelResponse(
                            success = true,
                            message = json.optString("message", "Clé CPK annulée et capital remboursé sur votre portefeuille."),
                            refundedAmount = json.optDouble("refundedAmount", 0.0),
                            status = "cancelled"
                        )
                    )
                } else {
                    val rawError = response.errorBody()?.string()
                    val msg = when (response.code()) {
                        401 -> "Session expirée. Veuillez vous reconnecter."
                        403 -> "Code PIN personnel CashPay incorrect ou annulation non autorisée."
                        404 -> "Clé introuvable."
                        else -> extractErrorMessage(rawError, "Impossible d'annuler la clé.")
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Erreur réseau lors de l'annulation.", e))
            }
        }
    }

    suspend fun checkCpkKeyStatus(signature: String): Result<com.example.data.model.CpkStatusResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.getCpkKeyStatus(signature.trim())
                if (response.isSuccessful && response.body() != null) {
                    val raw = response.body()!!.string().trim()
                    val json = org.json.JSONObject(raw)
                    val isSuccess = json.optBoolean("success", true)
                    val data = json.optJSONObject("data") ?: json

                    val st = data.optString("status", json.optString("status", "active"))
                    val amt = data.optDouble("amount", json.optDouble("amount", 0.0))
                    val rem = data.optDouble("remainingAmount", data.optDouble("remaining_amount", json.optDouble("remainingAmount", amt)))
                    val cur = data.optString("currency", json.optString("currency", "USD"))
                    val sec = data.optString("securityLevel", json.optString("securityLevel", "none"))

                    dao.updateCpkKeyStatus(signature, st, rem)

                    Result.success(
                        com.example.data.model.CpkStatusResponse(
                            success = isSuccess,
                            signature = signature,
                            status = st,
                            amount = amt,
                            remainingAmount = rem,
                            currency = cur,
                            securityLevel = sec,
                            message = json.optString("message", "Statut en temps réel actualisé.")
                        )
                    )
                } else {
                    val rawError = response.errorBody()?.string()
                    Result.failure(Exception(extractErrorMessage(rawError, "Impossible de vérifier le statut de la clé.")))
                }
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Erreur réseau lors de la vérification du statut.", e))
            }
        }
    }

    suspend fun importCpkContent(rawContent: String, sourceFileName: String?): Result<com.example.data.local.CpkKeyEntity> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                val clean = rawContent.trim()
                var sig = ""
                var ref: String? = null
                var amt = 0.0
                var cur = "USD"
                var sec = "none"
                var created = ""

                if (clean.startsWith("{")) {
                    val json = org.json.JSONObject(clean)
                    sig = json.optString("signature", json.optString("keySignature", json.optString("id", "")))
                    ref = json.optString("reference", json.optString("ref", "CPK-${sig.take(8).uppercase()}"))
                    amt = json.optDouble("amount", 0.0)
                    cur = json.optString("currency", "USD")
                    sec = json.optString("securityLevel", json.optString("security_level", "none"))
                    created = json.optString("createdAt", json.optString("created_at", ""))
                } else {
                    // Raw signature string format
                    sig = clean.lines().firstOrNull()?.trim() ?: ""
                    ref = "CPK-${sig.take(8).uppercase()}"
                }

                if (sig.isBlank()) {
                    return@withContext Result.failure(Exception("Format de clé CPK invalide : signature absente."))
                }

                val existing = dao.getCpkKeyBySignature(sig)
                if (existing != null) {
                    // Update and query live status
                    checkCpkKeyStatus(sig)
                    return@withContext Result.success(existing)
                }

                val newEntity = com.example.data.local.CpkKeyEntity(
                    signature = sig,
                    reference = ref,
                    amount = amt,
                    currency = cur,
                    securityLevel = sec,
                    status = "active",
                    remainingAmount = amt,
                    createdAt = if (created.isNotBlank()) created else java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
                    fileName = sourceFileName ?: "Import_${sig.take(8)}.cpk",
                    rawJsonContent = clean,
                    isCloudSynced = false,
                    isOwner = false
                )
                dao.insertCpkKey(newEntity)

                // Interrogate live server status immediately
                checkCpkKeyStatus(sig)

                Result.success(newEntity)
            } catch (e: Exception) {
                Result.failure(Exception("Impossible d'importer le fichier CPK : ${e.message}"))
            }
        }
    }

    suspend fun markKeyCloudSynced(signature: String, driveFileId: String?) {
        dao.updateCpkCloudSyncStatus(signature, isSynced = true, driveFileId = driveFileId, timestamp = System.currentTimeMillis())
    }

    suspend fun restoreCloudKeys(keys: List<com.example.data.local.CpkKeyEntity>): Int {
        var added = 0
        for (k in keys) {
            val exists = dao.getCpkKeyBySignature(k.signature)
            if (exists == null) {
                dao.insertCpkKey(k.copy(isCloudSynced = true, lastSyncTimestamp = System.currentTimeMillis()))
                added++
            } else {
                dao.updateCpkCloudSyncStatus(k.signature, isSynced = true, k.cloudDriveFileId, System.currentTimeMillis())
            }
        }
        return added
    }

    // --- PAYSCAN VISUAL CODES & POS PAYMENT OPERATIONS ---
    suspend fun scanPayInterpret(scannedValue: String): Result<com.example.data.model.ScanPayInterpretResponse> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val req = com.example.data.model.ScanPayInterpretRequest(value = scannedValue, data = scannedValue)
                val resp = apiService.scanPayInterpret(req)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body != null && body.success) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body?.message ?: body?.error ?: "Échec de l'interprétation du scan."))
                    }
                } else {
                    val rawErr = resp.errorBody()?.string()
                    val msg = when (resp.code()) {
                        401 -> "Session expirée. Veuillez vous reconnecter."
                        400 -> extractErrorMessage(rawErr, "Format de scan invalide ou non supporté.")
                        else -> extractErrorMessage(rawErr, "Erreur serveur lors de l'analyse du scan (${resp.code()}).")
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Erreur réseau lors de l'analyse du code.", e))
            }
        }
    }

    suspend fun lookupScannedProduct(productId: String? = null, barcode: String? = null): Result<com.example.data.model.ScanProductLookupResponse> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val req = com.example.data.model.ScanProductLookupRequest(productId = productId, barcode = barcode)
                val resp = apiService.lookupScannedProduct(req)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body != null && body.success) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body?.message ?: body?.error ?: "Produit non trouvé."))
                    }
                } else {
                    val rawErr = resp.errorBody()?.string()
                    val msg = when (resp.code()) {
                        401 -> "Session expirée. Veuillez vous reconnecter."
                        400 -> extractErrorMessage(rawErr, "Données de produit incomplètes.")
                        else -> extractErrorMessage(rawErr, "Erreur de validation du produit (${resp.code()}).")
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Erreur réseau lors de la vérification du produit.", e))
            }
        }
    }

    suspend fun processPosSale(pin: String, saleData: com.example.data.model.PosSaleData): Result<com.example.data.model.PosSalePaymentResponse> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val req = com.example.data.model.PosSalePaymentRequest(pin = pin, scanData = saleData)
                val resp = apiService.processPosSale(req)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body != null && body.success) {
                        try {
                            com.example.data.remote.ApiClient.currentUserId?.let { uid ->
                                getWallet(uid)
                                fetchTransactionsApi(uid, 1, 20)
                            }
                        } catch (_: Exception) {}
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body?.message ?: body?.error ?: "Paiement POS refusé."))
                    }
                } else {
                    val rawErr = resp.errorBody()?.string()
                    val msg = when (resp.code()) {
                        401 -> "Code PIN incorrect ou session expirée."
                        400 -> extractErrorMessage(rawErr, "Échec du paiement POS. Vérifiez le solde ou le stock.")
                        else -> extractErrorMessage(rawErr, "Erreur de transaction serveur (${resp.code()}).")
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Erreur réseau lors du paiement POS.", e))
            }
        }
    }

    // --- STARLINK MANAGER REPOSITORY METHODS (Full 10 Endpoints) ---
    suspend fun starlinkClientLink(kitSerialNumber: String): Result<Boolean> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                ensureSessionToken()
                val resp = apiService.starlinkClientLink(com.example.data.model.StarlinkClientLinkRequest(kitSerialNumber.trim()))
                if (resp.isSuccessful && resp.body()?.success == true) {
                    Result.success(true)
                } else {
                    val rawErr = resp.errorBody()?.string()
                    Result.failure(Exception(extractErrorMessage(rawErr, "Échec de l'association du kit Starlink.")))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun starlinkSaveSubCustomer(
        terminalId: String,
        name: String,
        walletId: String,
        pinCode: String,
        macAddress: String,
        expiryDate: String,
        dataLimitGb: Int
    ): Result<Boolean> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                ensureSessionToken()
                val req = com.example.data.model.StarlinkSubCustomerRequest(
                    terminalId = terminalId.trim(),
                    name = name.trim(),
                    walletId = walletId.trim(),
                    pinCode = pinCode.trim(),
                    macAddress = macAddress.trim(),
                    expiryDate = expiryDate.trim(),
                    dataLimitGb = dataLimitGb
                )
                val resp = apiService.starlinkSaveSubCustomer(req)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    Result.success(true)
                } else {
                    val rawErr = resp.errorBody()?.string()
                    Result.failure(Exception(extractErrorMessage(rawErr, "Échec de l'enregistrement du client secondaire.")))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun starlinkBillingPay(terminalId: String, planName: String, pin: String): Result<Boolean> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                ensureSessionToken()
                val req = com.example.data.model.StarlinkBillingPayRequest(
                    terminalId = terminalId.trim(),
                    planName = planName.trim(),
                    pin = pin.trim()
                )
                val resp = apiService.starlinkBillingPay(req)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    Result.success(true)
                } else {
                    val rawErr = resp.errorBody()?.string()
                    val msg = when (resp.code()) {
                        400 -> extractErrorMessage(rawErr, "Champs obligatoires manquants ou erreur de paiement.")
                        401 -> "Signature invalide ou session non valide."
                        402 -> extractErrorMessage(rawErr, "Solde insuffisant ou paiement refusé.")
                        else -> extractErrorMessage(rawErr, "Échec du paiement abonnement Starlink.")
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun starlinkResellerLink(config: Map<String, Any> = emptyMap()): Result<com.example.data.model.StarlinkResellerLinkResponse> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                ensureSessionToken()
                val resp = apiService.starlinkResellerLink(config)
                if (resp.isSuccessful && resp.body() != null) {
                    Result.success(resp.body()!!)
                } else {
                    val rawErr = resp.errorBody()?.string()
                    val msg = when (resp.code()) {
                        401 -> "Signature invalide ou utilisateur non authentifié."
                        403 -> "Accès réservé aux rôles Agent ou Admin."
                        else -> extractErrorMessage(rawErr, "Erreur lors de la liaison du compte revendeur.")
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun starlinkGetResellerConfig(): Result<com.example.data.model.StarlinkResellerConfigResponse> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                ensureSessionToken()
                val resp = apiService.starlinkGetResellerConfig()
                if (resp.isSuccessful && resp.body() != null) {
                    Result.success(resp.body()!!)
                } else {
                    val rawErr = resp.errorBody()?.string()
                    Result.failure(Exception(extractErrorMessage(rawErr, "Impossible de charger la configuration revendeur.")))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun starlinkSaveResellerConfig(
        companyName: String,
        merchantWalletId: String,
        starlinkClientId: String,
        starlinkClientSecret: String
    ): Result<Boolean> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                ensureSessionToken()
                val req = com.example.data.model.StarlinkSaveResellerConfigRequest(
                    companyName = companyName.trim(),
                    merchantWalletId = merchantWalletId.trim(),
                    starlinkClientId = starlinkClientId.trim(),
                    starlinkClientSecret = starlinkClientSecret.trim()
                )
                val resp = apiService.starlinkSaveResellerConfig(req)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    Result.success(true)
                } else {
                    val rawErr = resp.errorBody()?.string()
                    Result.failure(Exception(extractErrorMessage(rawErr, "Échec de l'enregistrement de la configuration revendeur.")))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun starlinkGetTerminals(): Result<List<com.example.data.model.StarlinkTerminalDetailDto>> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                ensureSessionToken()
                val resp = apiService.starlinkGetTerminals()
                if (resp.isSuccessful && resp.body() != null) {
                    Result.success(resp.body()!!.data)
                } else {
                    val rawErr = resp.errorBody()?.string()
                    Result.failure(Exception(extractErrorMessage(rawErr, "Impossible de récupérer les terminaux revendeur.")))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun starlinkRegisterTerminal(
        resellerId: String,
        deviceId: String,
        nickname: String,
        ownerWalletId: String,
        serialNumber: String
    ): Result<Boolean> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                ensureSessionToken()
                val req = com.example.data.model.StarlinkRegisterTerminalRequest(
                    resellerId = resellerId.trim(),
                    deviceId = deviceId.trim(),
                    nickname = nickname.trim(),
                    ownerWalletId = ownerWalletId.trim(),
                    serialNumber = serialNumber.trim()
                )
                val resp = apiService.starlinkRegisterTerminal(req)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    Result.success(true)
                } else {
                    val rawErr = resp.errorBody()?.string()
                    Result.failure(Exception(extractErrorMessage(rawErr, "Échec de l'enregistrement du terminal.")))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun starlinkGetTerminalStatus(terminalId: String): Result<com.example.data.model.StarlinkTerminalStatusData> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                ensureSessionToken()
                val resp = apiService.starlinkGetTerminalStatus(terminalId.trim())
                if (resp.isSuccessful && resp.body()?.success == true && resp.body()?.data != null) {
                    Result.success(resp.body()!!.data!!)
                } else {
                    val rawErr = resp.errorBody()?.string()
                    val msg = if (resp.code() == 404) "Terminal introuvable." else extractErrorMessage(rawErr, "Erreur de consultation du terminal.")
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun starlinkRebootTerminal(terminalId: String): Result<String> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                ensureSessionToken()
                val resp = apiService.starlinkRebootTerminal(terminalId.trim())
                if (resp.isSuccessful && resp.body()?.success == true) {
                    Result.success(resp.body()?.message ?: "Reboot initiated")
                } else {
                    val rawErr = resp.errorBody()?.string()
                    val msg = when (resp.code()) {
                        400 -> "Compte revendeur non lié."
                        401 -> "Signature invalide ou utilisateur non authentifié."
                        else -> extractErrorMessage(rawErr, "Échec du redémarrage du terminal Starlink.")
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

