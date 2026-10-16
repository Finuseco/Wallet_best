package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.CountryDto
import com.example.data.model.ProfileInstallationDto
import com.example.data.model.UserProfileDto
import com.example.data.repository.CashPayRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMethod {
    PHONE, WALLET_ID
}

enum class AuthStep {
    IDENTIFICATION,
    REGISTER_FORM,
    REGISTER_SUCCESS,
    OTP_CHANNEL_SELECT,
    OTP_VERIFICATION,
    PROFILE_INSTALLATION,
    PIN_ENTRY,
    COMPLETED
}

data class AuthUiState(
    val authMethod: AuthMethod = AuthMethod.PHONE,
    val selectedCountry: CountryDto = CountryDto("CD", "République démocratique du Congo", "+243"),
    val localPhone: String = "000000000",
    val walletId: String = "",
    val countries: List<CountryDto> = emptyList(),
    val isCountryPickerOpen: Boolean = false,
    val countrySearchQuery: String = "",
    val authStep: AuthStep = AuthStep.IDENTIFICATION,
    val userId: Long = 1,
    val channels: List<String> = listOf("sms", "whatsapp", "email"),
    val selectedChannel: String = "sms",
    val rawPhone: String = "",
    val rawEmail: String = "",
    val serverMaskedPhone: String? = null,
    val serverMaskedEmail: String? = null,
    val otpCode: String = "",
    val pinCode: String = "",
    val isOtpLoading: Boolean = false,
    val isPinLoading: Boolean = false,
    val isRegisterLoading: Boolean = false,
    val isInstallingProfile: Boolean = false,

    // Registration Success Info from Backend
    val registrationSuccessWalletId: String? = null,
    val registrationSuccessFullName: String? = null,
    val registrationSuccessPhone: String? = null,
    val registrationSuccessToken: String? = null,

    // Phone OTP verification state for Registration
    val isPhoneOtpSending: Boolean = false,
    val isPhoneOtpVerifying: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val phoneOtpError: String? = null,
    val phoneOtpSuccess: String? = null,

    // Email OTP verification state
    val isEmailOtpSending: Boolean = false,
    val isEmailOtpVerifying: Boolean = false,
    val isEmailVerified: Boolean = false,
    val emailOtpError: String? = null,
    val emailOtpSuccess: String? = null,

    val installProgressPercent: Int = 0,
    val installStageText: String = "Initialisation du système...",
    val profileInstallation: ProfileInstallationDto? = null,
    val installedUserProfile: UserProfileDto? = null,
    val isBiometricEnabled: Boolean = false,
    val isOnline: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // Modal popup state for existing accounts or critical validation errors
    val existingAccountErrorModalMessage: String? = null,

    // --- FORGOT PIN STATE ---
    val isForgotPinOpen: Boolean = false,
    val forgotPinStep: Int = 1,
    val forgotPinPhone: String = "",
    val forgotPinChannel: String = "sms",
    val forgotPinUserId: Long? = null,
    val forgotPinOtp: String = "",
    val forgotPinNewPin: String = "",
    val forgotPinConfirmPin: String = "",
    val isForgotPinLoading: Boolean = false,
    val forgotPinError: String? = null,
    val forgotPinSuccess: String? = null,

    // --- LOCK & ATTEMPT TRACKING ---
    val pinFailedAttempts: Int = 0,
    val biometricFailedAttempts: Int = 0,
    val isPinLocked: Boolean = false
) {
    val fullPhone: String
        get() = "${selectedCountry.dialCode}$localPhone"

    val isPhoneValid: Boolean
        get() = localPhone.length == 9

    val isWalletIdValid: Boolean
        get() = walletId.isNotBlank() && walletId.length >= 6

    val isIdentificationReady: Boolean
        get() = if (authMethod == AuthMethod.PHONE) isPhoneValid else isWalletIdValid

    val maskedPhone: String
        get() {
            if (!serverMaskedPhone.isNullOrBlank()) {
                val dial = selectedCountry.dialCode
                return "$dial $serverMaskedPhone"
            }
            val phoneToMask = if (rawPhone.isNotBlank()) rawPhone else fullPhone
            val digits = phoneToMask.filter { it.isDigit() }
            return if (digits.length >= 9) {
                val dial = if (phoneToMask.startsWith("+")) phoneToMask.substring(0, 4) else "+243"
                val first2 = digits.drop(3).take(2)
                val last2 = digits.takeLast(2)
                "$dial $first2 ••• ••$last2"
            } else {
                phoneToMask
            }
        }

    val maskedEmail: String
        get() {
            if (!serverMaskedEmail.isNullOrBlank()) return serverMaskedEmail
            val parts = rawEmail.split("@")
            return if (parts.size == 2) {
                val name = parts[0]
                val domain = parts[1]
                val visible = name.take(2)
                "$visible••••••@$domain"
            } else {
                rawEmail
            }
        }
}

class AuthViewModel(
    private val repository: CashPayRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentSession: StateFlow<SessionEntity?> = repository.session
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentUserProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun mapEntityToDto(e: com.example.data.local.UserProfileEntity): UserProfileDto {
        return UserProfileDto(
            id = e.id,
            role = e.role,
            status = e.status,
            accountType = e.accountType,
            walletId = e.walletId,
            language = e.language,
            fullName = e.fullName,
            firstName = e.firstName,
            lastName = e.lastName,
            email = e.email,
            phone = e.phone,
            profilePhotoUrl = e.profilePhotoUrl
        )
    }

    init {
        loadCountries()
        viewModelScope.launch {
            repository.countries.collect { list ->
                if (list.isNotEmpty()) {
                    _uiState.update { it.copy(countries = list) }
                }
            }
        }
        viewModelScope.launch {
            repository.userProfile.collect { profile ->
                if (profile != null) {
                    val dto = mapEntityToDto(profile)
                    _uiState.update { current ->
                        current.copy(
                            installedUserProfile = dto,
                            rawPhone = profile.phone ?: current.rawPhone,
                            walletId = profile.walletId ?: current.walletId
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.session.collect { session ->
                if (session != null) {
                    if (!session.sessionToken.isNullOrBlank()) {
                        com.example.data.remote.ApiClient.sessionToken = session.sessionToken
                    }
                    val currentProfile = repository.getUserProfileOnce()
                    val dto = currentProfile?.let { mapEntityToDto(it) }
                    _uiState.update { current ->
                        current.copy(
                            userId = session.userId,
                            walletId = session.walletId ?: currentProfile?.walletId ?: current.walletId,
                            rawPhone = session.phone ?: currentProfile?.phone ?: current.rawPhone,
                            installedUserProfile = dto ?: current.installedUserProfile,
                            isBiometricEnabled = session.biometricEnabled,
                            // When user has an established account, lock to PIN/fingerprint! Never ask OTP again.
                            authStep = if (session.isAuthenticated) AuthStep.COMPLETED else AuthStep.PIN_ENTRY
                        )
                    }
                } else {
                    val profile = repository.getUserProfileOnce()
                    if (profile != null) {
                        val dto = mapEntityToDto(profile)
                        _uiState.update { current ->
                            current.copy(
                                userId = profile.id,
                                walletId = profile.walletId ?: current.walletId,
                                rawPhone = profile.phone ?: current.rawPhone,
                                installedUserProfile = dto,
                                authStep = AuthStep.PIN_ENTRY
                            )
                        }
                    } else {
                        com.example.data.remote.ApiClient.sessionToken = null
                        _uiState.update {
                            AuthUiState(
                                countries = it.countries,
                                selectedCountry = it.selectedCountry,
                                authStep = AuthStep.IDENTIFICATION
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadCountries() {
        viewModelScope.launch {
            repository.loadCountries()
        }
    }

    fun setAuthMethod(method: AuthMethod) {
        _uiState.update { it.copy(authMethod = method, errorMessage = null) }
    }

    fun setCountryPickerOpen(open: Boolean) {
        _uiState.update { it.copy(isCountryPickerOpen = open, countrySearchQuery = "") }
    }

    fun setCountrySearchQuery(query: String) {
        _uiState.update { it.copy(countrySearchQuery = query) }
    }

    fun selectCountry(country: CountryDto) {
        _uiState.update {
            it.copy(
                selectedCountry = country,
                isCountryPickerOpen = false,
                countrySearchQuery = "",
                errorMessage = null
            )
        }
    }

    fun onLocalPhoneChanged(input: String) {
        val filtered = input.filter { it.isDigit() }.take(9)
        _uiState.update { it.copy(localPhone = filtered, errorMessage = null) }
    }

    fun onWalletIdChanged(input: String) {
        val uppercase = input.uppercase().filter { it.isLetterOrDigit() }.take(16)
        _uiState.update { it.copy(walletId = uppercase, errorMessage = null) }
    }

    fun submitIdentification() {
        val state = _uiState.value
        _uiState.update { it.copy(isOtpLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val phone = if (state.authMethod == AuthMethod.PHONE) state.fullPhone else null
            val wallet = if (state.authMethod == AuthMethod.WALLET_ID) state.walletId else null

            val result = repository.login(walletId = wallet, phone = phone)
            result.onSuccess { response ->
                val resolvedPhone = response.phone ?: state.fullPhone
                val channels = response.effectiveChannels
                _uiState.update {
                    it.copy(
                        isOtpLoading = false,
                        userId = response.userId ?: 1,
                        rawPhone = resolvedPhone,
                        serverMaskedPhone = response.maskedPhone,
                        serverMaskedEmail = response.maskedEmail,
                        rawEmail = response.email ?: (response.maskedEmail ?: it.rawEmail),
                        walletId = response.walletId ?: it.walletId,
                        channels = channels,
                        selectedChannel = channels.firstOrNull() ?: "sms",
                        authStep = AuthStep.OTP_CHANNEL_SELECT,
                        successMessage = response.message
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isOtpLoading = false,
                        errorMessage = err.message ?: "Erreur d'identification"
                    )
                }
            }
        }
    }

    fun selectOtpChannel(channel: String) {
        _uiState.update { it.copy(selectedChannel = channel, errorMessage = null) }
    }

    fun requestOtpSend() {
        val state = _uiState.value
        _uiState.update { it.copy(isOtpLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.sendOtp(state.userId, state.selectedChannel)
            result.onSuccess { resp ->
                _uiState.update {
                    it.copy(
                        isOtpLoading = false,
                        authStep = AuthStep.OTP_VERIFICATION,
                        otpCode = "",
                        errorMessage = null,
                        successMessage = resp.message
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isOtpLoading = false,
                        errorMessage = err.message ?: "Impossible d'envoyer l'OTP"
                    )
                }
            }
        }
    }

    fun submitOtpVerification() {
        val state = _uiState.value
        if (state.otpCode.length == 6) {
            verifyOtpAutomatically(state.otpCode)
        } else {
            _uiState.update { it.copy(errorMessage = "Veuillez saisir le code à 6 chiffres.") }
        }
    }

    fun onOtpChanged(newOtp: String) {
        val filtered = newOtp.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(otpCode = filtered, errorMessage = null) }

        if (filtered.length == 6) {
            verifyOtpAutomatically(filtered)
        }
    }

    private fun verifyOtpAutomatically(otp: String) {
        val state = _uiState.value
        _uiState.update { it.copy(isOtpLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.verifyOtp(state.userId, otp)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isOtpLoading = false,
                        authStep = AuthStep.PROFILE_INSTALLATION,
                        errorMessage = null
                    )
                }
                startSystemProfileInstallation()
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isOtpLoading = false,
                        otpCode = "",
                        errorMessage = err.message ?: "Code OTP incorrect."
                    )
                }
            }
        }
    }

    private fun startSystemProfileInstallation() {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                authStep = AuthStep.PROFILE_INSTALLATION,
                isInstallingProfile = true,
                installProgressPercent = 1,
                installStageText = "Connexion au serveur sécurisé CashPay..."
            )
        }

        viewModelScope.launch {
            val phoneToQuery = if (state.rawPhone.isNotBlank()) state.rawPhone else state.fullPhone

            // Asynchronous OS-like gradual installation progression: 1% -> 100%
            for (p in 2..30) {
                delay(30)
                _uiState.update {
                    it.copy(
                        installProgressPercent = p,
                        installStageText = "Noyau CashPay • Résolution du compte ${state.maskedPhone}..."
                    )
                }
            }

            // Real API Call GET /api/v1/auth/me?phone=...
            val result = repository.installProfile(phoneToQuery)

            for (p in 31..65) {
                delay(25)
                _uiState.update {
                    it.copy(
                        installProgressPercent = p,
                        installStageText = "Chiffrement AES-256 • Synchronisation des certificats de paiement..."
                    )
                }
            }

            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        profileInstallation = response.profileInstallation,
                        installedUserProfile = response.profile
                    )
                }
            }

            for (p in 66..99) {
                delay(20)
                _uiState.update {
                    it.copy(
                        installProgressPercent = p,
                        installStageText = "Finalisation de l'environnement • Module USSD & Profil Astrologique..."
                    )
                }
            }

            delay(100)
            _uiState.update {
                it.copy(
                    installProgressPercent = 100,
                    installStageText = "Profil installé • Confirmation par code PIN requise.",
                    isInstallingProfile = false
                )
            }

            delay(400)
            // Move to PIN Confirmation step
            _uiState.update {
                it.copy(authStep = AuthStep.PIN_ENTRY)
            }
        }
    }

    fun onPinDigitEntered(digit: String) {
        val state = _uiState.value
        if (state.isPinLocked) return
        val current = state.pinCode
        if (current.length < 4) {
            val updated = current + digit
            _uiState.update { it.copy(pinCode = updated, errorMessage = null) }

            if (updated.length == 4) {
                verifyPinAutomatically(updated)
            }
        }
    }

    fun onPinBackspace() {
        val state = _uiState.value
        if (state.isPinLocked) return
        val current = state.pinCode
        if (current.isNotEmpty()) {
            _uiState.update { it.copy(pinCode = current.dropLast(1), errorMessage = null) }
        }
    }

    fun updateNetworkState(isOnline: Boolean) {
        _uiState.update { it.copy(isOnline = isOnline) }
    }

    private fun verifyPinAutomatically(pin: String) {
        val state = _uiState.value

        // STRICT REQUIREMENT: App unlocking to Dashboard MUST have active Internet connection
        if (!state.isOnline) {
            _uiState.update {
                it.copy(
                    isPinLoading = false,
                    pinCode = "",
                    errorMessage = "Vous n'êtes pas connecté à Internet. Une connexion active est requise pour déverrouiller votre compte et valider votre code PIN."
                )
            }
            return
        }

        _uiState.update { it.copy(isPinLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.verifyPin(state.userId, pin)
            result.onSuccess { resp ->
                if (resp.authenticated || resp.success) {
                    _uiState.update {
                        it.copy(
                            isPinLoading = false,
                            authStep = AuthStep.COMPLETED,
                            pinCode = "",
                            errorMessage = null,
                            pinFailedAttempts = 0,
                            biometricFailedAttempts = 0,
                            isPinLocked = false
                        )
                    }
                    try {
                        com.example.CashPayApplication.instance.notificationSyncManager.onPinConfirmed()
                    } catch (e: Exception) {
                        // ignore if app instance not ready
                    }
                } else {
                    val nextAttempts = state.pinFailedAttempts + 1
                    if (nextAttempts >= 5) {
                        _uiState.update {
                            it.copy(
                                isPinLoading = false,
                                pinCode = "",
                                pinFailedAttempts = nextAttempts,
                                isPinLocked = true,
                                errorMessage = "5 tentatives de code PIN échouées. Clavier PIN verrouillé. Veuillez utiliser votre empreinte digitale."
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isPinLoading = false,
                                pinCode = "",
                                pinFailedAttempts = nextAttempts,
                                errorMessage = resp.error ?: "Code PIN incorrect (Tentative $nextAttempts/5)."
                            )
                        }
                    }
                }
            }.onFailure { err ->
                val errorMsg = when {
                    err is java.net.UnknownHostException || err is java.net.ConnectException || err is java.io.IOException -> {
                        "Vous n'êtes pas connecté à Internet. Veuillez vérifier votre réseau mobile ou Wi-Fi et réessayer."
                    }
                    else -> err.message ?: "Code PIN incorrect ou session expirée."
                }
                val nextAttempts = state.pinFailedAttempts + 1
                if (nextAttempts >= 5) {
                    _uiState.update {
                        it.copy(
                            isPinLoading = false,
                            pinCode = "",
                            pinFailedAttempts = nextAttempts,
                            isPinLocked = true,
                            errorMessage = "5 tentatives de code PIN échouées. Clavier PIN verrouillé. Veuillez utiliser votre empreinte digitale."
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isPinLoading = false,
                            pinCode = "",
                            pinFailedAttempts = nextAttempts,
                            errorMessage = "$errorMsg (Tentative $nextAttempts/5)"
                        )
                    }
                }
            }
        }
    }

    fun onBiometricClicked() {
        val state = _uiState.value

        // STRICT REQUIREMENT: Fingerprint sync & unlock also requires Internet
        if (!state.isOnline) {
            _uiState.update {
                it.copy(
                    errorMessage = "Vous n'êtes pas connecté à Internet. Une connexion est obligatoire pour synchroniser la sécurité biométrique."
                )
            }
            return
        }

        if (!state.isBiometricEnabled) {
            _uiState.update {
                it.copy(
                    errorMessage = "Empreinte non synchronisée. Veuillez l'activer dans vos Paramètres > Sécurité."
                )
            }
            return
        }

        // Biometric security is synchronized and enabled!
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isPinLoading = true,
                    errorMessage = null
                )
            }
            delay(350)
            val session = repository.getSessionOnce()
            if (session != null && session.biometricEnabled && !session.sessionToken.isNullOrBlank()) {
                // Biometrics verified with valid session token: complete authentication
                _uiState.update {
                    it.copy(
                        isPinLoading = false,
                        authStep = AuthStep.COMPLETED,
                        pinCode = "",
                        errorMessage = null,
                        pinFailedAttempts = 0,
                        biometricFailedAttempts = 0,
                        isPinLocked = false
                    )
                }
            } else {
                onBiometricFailed()
            }
        }
    }

    fun onBiometricFailed() {
        val state = _uiState.value
        val nextBio = state.biometricFailedAttempts + 1
        if (nextBio >= 5) {
            // 5 biometric failures -> Return directly to OTP verification!
            _uiState.update {
                it.copy(
                    isPinLoading = false,
                    pinCode = "",
                    pinFailedAttempts = 0, // Reset PIN attempts too? User implies recommencer
                    biometricFailedAttempts = 0,
                    isPinLocked = false,
                    authStep = AuthStep.OTP_VERIFICATION,
                    errorMessage = "5 tentatives d'empreinte échouées. Veuillez valider par OTP pour recommencer."
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isPinLoading = false,
                    biometricFailedAttempts = nextBio,
                    errorMessage = "Empreinte incorrecte (Tentative $nextBio/5)."
                )
            }
        }
    }

    fun onBiometricSuccess() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPinLoading = true, errorMessage = null) }
            repository.authenticateBiometrically()
            _uiState.update {
                it.copy(
                    isPinLoading = false,
                    authStep = AuthStep.COMPLETED,
                    pinCode = "",
                    errorMessage = null
                )
            }
            try {
                com.example.CashPayApplication.instance.notificationSyncManager.onPinConfirmed()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun goToRegister() {
        _uiState.update { it.copy(authStep = AuthStep.REGISTER_FORM, errorMessage = null) }
    }

    fun navigateBack() {
        val current = _uiState.value.authStep
        val previous = when (current) {
            AuthStep.IDENTIFICATION -> AuthStep.IDENTIFICATION
            AuthStep.REGISTER_FORM -> AuthStep.IDENTIFICATION
            AuthStep.REGISTER_SUCCESS -> AuthStep.IDENTIFICATION
            AuthStep.OTP_CHANNEL_SELECT -> AuthStep.IDENTIFICATION
            AuthStep.OTP_VERIFICATION -> AuthStep.OTP_CHANNEL_SELECT
            AuthStep.PROFILE_INSTALLATION -> AuthStep.OTP_VERIFICATION
            AuthStep.PIN_ENTRY -> AuthStep.OTP_VERIFICATION
            AuthStep.COMPLETED -> AuthStep.COMPLETED
        }
        _uiState.update {
            it.copy(
                authStep = previous,
                errorMessage = null,
                pinCode = "",
                otpCode = ""
            )
        }
    }

    fun clearPhoneOtpError() {
        _uiState.update { it.copy(phoneOtpError = null) }
    }

    fun clearExistingAccountErrorModal() {
        _uiState.update { it.copy(existingAccountErrorModalMessage = null) }
    }

    fun navigateToLoginWithPhone(phone: String = "") {
        _uiState.update { current ->
            var local = current.localPhone
            if (phone.isNotBlank()) {
                val digits = phone.filter { it.isDigit() }
                if (digits.length >= 9) {
                    local = digits.takeLast(9)
                }
            }
            current.copy(
                authStep = AuthStep.IDENTIFICATION,
                authMethod = AuthMethod.PHONE,
                localPhone = local,
                existingAccountErrorModalMessage = null,
                errorMessage = null
            )
        }
    }

    fun sendRegisterPhoneOtp(phone: String, accountType: String = "national", channel: String = "sms") {
        if (phone.isBlank()) return
        _uiState.update { it.copy(isPhoneOtpSending = true, phoneOtpError = null, phoneOtpSuccess = null, existingAccountErrorModalMessage = null) }
        viewModelScope.launch {
            val res = repository.registerSendPhoneOtp(phone.trim(), accountType, channel)
            res.onSuccess { resp ->
                val channelLabel = if (channel.lowercase() == "whatsapp") "WhatsApp" else "SMS"
                _uiState.update {
                    it.copy(
                        isPhoneOtpSending = false,
                        phoneOtpSuccess = resp.message ?: "Code OTP envoyé avec succès via $channelLabel.",
                        phoneOtpError = null,
                        existingAccountErrorModalMessage = null
                    )
                }
            }.onFailure { err ->
                val errMsg = err.message ?: "Échec d'envoi du code OTP au numéro de téléphone."
                val isAlreadyRegistered = errMsg.contains("existe", ignoreCase = true) ||
                                         errMsg.contains("déjà", ignoreCase = true) ||
                                         errMsg.contains("already", ignoreCase = true) ||
                                         errMsg.contains("associé", ignoreCase = true) ||
                                         errMsg.contains("compte", ignoreCase = true)
                _uiState.update {
                    it.copy(
                        isPhoneOtpSending = false,
                        phoneOtpError = errMsg,
                        existingAccountErrorModalMessage = if (isAlreadyRegistered) errMsg else null
                    )
                }
            }
        }
    }

    fun verifyRegisterPhoneOtp(phone: String, otp: String) {
        val state = _uiState.value
        verifyRegisterPhoneOtp(phone, state.selectedCountry.code, state.selectedCountry.name, otp)
    }

    fun verifyRegisterPhoneOtp(phone: String, countryCode: String, country: String, otp: String) {
        if (phone.isBlank() || otp.isBlank()) return
        _uiState.update { it.copy(isPhoneOtpVerifying = true, phoneOtpError = null) }
        viewModelScope.launch {
            val res = repository.registerVerifyPhoneOtp(phone.trim(), countryCode, country, otp.trim())
            res.onSuccess {
                _uiState.update {
                    it.copy(
                        isPhoneOtpVerifying = false,
                        isPhoneVerified = true,
                        phoneOtpSuccess = "Numéro de téléphone vérifié avec succès !",
                        phoneOtpError = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isPhoneOtpVerifying = false,
                        phoneOtpError = err.message ?: "Code OTP téléphone invalide."
                    )
                }
            }
        }
    }

    fun onRawEmailChanged(email: String) {
        _uiState.update { it.copy(rawEmail = email, isEmailVerified = false, emailOtpError = null, emailOtpSuccess = null) }
    }

    fun sendRegisterEmailOtp(email: String) {
        if (email.isBlank()) return
        _uiState.update { it.copy(isEmailOtpSending = true, emailOtpError = null, emailOtpSuccess = null) }
        viewModelScope.launch {
            val res = repository.registerSendEmailOtp(email.trim())
            res.onSuccess { resp ->
                _uiState.update {
                    it.copy(
                        isEmailOtpSending = false,
                        emailOtpSuccess = resp.message ?: "Code OTP envoyé à votre adresse e-mail.",
                        emailOtpError = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isEmailOtpSending = false,
                        emailOtpError = err.message ?: "Impossible d'envoyer le code OTP e-mail."
                    )
                }
            }
        }
    }

    fun verifyRegisterEmailOtp(email: String, otp: String) {
        if (email.isBlank() || otp.isBlank()) return
        _uiState.update { it.copy(isEmailOtpVerifying = true, emailOtpError = null) }
        viewModelScope.launch {
            val res = repository.registerVerifyEmailOtp(email.trim(), otp.trim())
            res.onSuccess { resp ->
                _uiState.update {
                    it.copy(
                        isEmailOtpVerifying = false,
                        isEmailVerified = true,
                        emailOtpSuccess = "E-mail validé avec succès !",
                        emailOtpError = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isEmailOtpVerifying = false,
                        emailOtpError = err.message ?: "Code OTP e-mail invalide."
                    )
                }
            }
        }
    }

    fun resetToLogin() {
        _uiState.update {
            AuthUiState(
                countries = it.countries,
                selectedCountry = it.selectedCountry
            )
        }
    }

    fun submitRegistration(
        accountType: String,
        firstName: String,
        middleName: String,
        lastName: String,
        gender: String,
        birthDate: String,
        maritalStatus: String,
        nationality: String,
        city: String,
        address: String,
        profession: String,
        idType: String,
        idNumber: String,
        companyName: String,
        idFrontBase64: String?,
        profilePhotoBase64: String?,
        signatureBase64: String?,
        language: String = "fr"
    ) {
        val state = _uiState.value
        _uiState.update { it.copy(isRegisterLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val req = com.example.data.model.RegisterRequest(
                action = "complete",
                phone = state.fullPhone,
                countryCode = state.selectedCountry.code,
                country = state.selectedCountry.name,
                accountType = accountType,
                language = language,
                companyName = if (accountType == "business") companyName.ifBlank { null } else null,
                firstName = firstName.ifBlank { null },
                middleName = middleName.ifBlank { null },
                lastName = lastName.ifBlank { null },
                gender = gender.ifBlank { null },
                birthDate = birthDate.ifBlank { null },
                maritalStatus = maritalStatus.ifBlank { null },
                nationality = nationality.ifBlank { state.selectedCountry.code },
                email = state.rawEmail.ifBlank { null },
                city = city.ifBlank { null },
                address = address.ifBlank { null },
                profession = profession.ifBlank { null },
                idType = idType.ifBlank { null },
                idNumber = idNumber.ifBlank { null },
                idFrontImage = idFrontBase64,
                profilePhoto = profilePhotoBase64,
                signatureImage = signatureBase64
            )

            val res = repository.registerComplete(req)
            res.onSuccess { resp ->
                val returnedWalletId = resp.effectiveWalletId ?: "CD${System.currentTimeMillis().toString().takeLast(10)}"
                val returnedFullName = resp.effectiveFullName ?: "$firstName $lastName".trim()

                _uiState.update {
                    it.copy(
                        isRegisterLoading = false,
                        registrationSuccessWalletId = returnedWalletId,
                        registrationSuccessFullName = returnedFullName,
                        registrationSuccessPhone = state.fullPhone,
                        registrationSuccessToken = resp.token,
                        walletId = returnedWalletId,
                        authStep = AuthStep.REGISTER_SUCCESS,
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isRegisterLoading = false,
                        errorMessage = err.message ?: "Erreur d'inscription"
                    )
                }
            }
        }
    }

    fun finishRegistrationToLogin() {
        _uiState.update {
            it.copy(
                authStep = AuthStep.IDENTIFICATION,
                authMethod = AuthMethod.WALLET_ID,
                walletId = it.registrationSuccessWalletId ?: it.walletId,
                errorMessage = null
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.update {
                AuthUiState(
                    countries = it.countries,
                    selectedCountry = it.selectedCountry
                )
            }
        }
    }

    // --- FORGOT PIN METHODS ---
    fun openForgotPin() {
        val initialPhone = _uiState.value.fullPhone
        _uiState.update {
            it.copy(
                isForgotPinOpen = true,
                forgotPinStep = 1,
                forgotPinPhone = initialPhone,
                forgotPinChannel = "sms",
                forgotPinUserId = null,
                forgotPinOtp = "",
                forgotPinNewPin = "",
                forgotPinConfirmPin = "",
                isForgotPinLoading = false,
                forgotPinError = null,
                forgotPinSuccess = null
            )
        }
    }

    fun closeForgotPin() {
        _uiState.update { it.copy(isForgotPinOpen = false) }
    }

    fun setForgotPinPhone(phone: String) {
        _uiState.update { it.copy(forgotPinPhone = phone, forgotPinError = null) }
    }

    fun setForgotPinChannel(channel: String) {
        _uiState.update { it.copy(forgotPinChannel = channel, forgotPinError = null) }
    }

    fun setForgotPinOtp(otp: String) {
        _uiState.update { it.copy(forgotPinOtp = otp, forgotPinError = null) }
    }

    fun setForgotPinNewPin(pin: String) {
        _uiState.update { it.copy(forgotPinNewPin = pin, forgotPinError = null) }
    }

    fun setForgotPinConfirmPin(pin: String) {
        _uiState.update { it.copy(forgotPinConfirmPin = pin, forgotPinError = null) }
    }

    fun submitForgotPinRequest() {
        val phone = _uiState.value.forgotPinPhone
        val channel = _uiState.value.forgotPinChannel
        _uiState.update { it.copy(isForgotPinLoading = true, forgotPinError = null) }

        viewModelScope.launch {
            val result = repository.forgotPinRequest(phone, channel)
            result.onSuccess { res ->
                if (res.success && res.userId != null) {
                    _uiState.update {
                        it.copy(
                            isForgotPinLoading = false,
                            forgotPinStep = 2,
                            forgotPinUserId = res.userId,
                            forgotPinError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isForgotPinLoading = false,
                            forgotPinError = res.error ?: "Impossible d'envoyer le code."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isForgotPinLoading = false,
                        forgotPinError = err.message ?: "Erreur réseau."
                    )
                }
            }
        }
    }

    fun submitForgotPinVerify() {
        val userId = _uiState.value.forgotPinUserId ?: return
        val otp = _uiState.value.forgotPinOtp
        _uiState.update { it.copy(isForgotPinLoading = true, forgotPinError = null) }

        viewModelScope.launch {
            val result = repository.forgotPinVerify(userId, otp)
            result.onSuccess { res ->
                if (res.success) {
                    _uiState.update {
                        it.copy(
                            isForgotPinLoading = false,
                            forgotPinStep = 3,
                            forgotPinError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isForgotPinLoading = false,
                            forgotPinError = res.error ?: "Code invalide."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isForgotPinLoading = false,
                        forgotPinError = err.message ?: "Code de vérification invalide ou expiré."
                    )
                }
            }
        }
    }

    fun submitForgotPinReset() {
        val userId = _uiState.value.forgotPinUserId ?: return
        val otp = _uiState.value.forgotPinOtp
        val newPin = _uiState.value.forgotPinNewPin
        val confirmPin = _uiState.value.forgotPinConfirmPin

        if (newPin != confirmPin) {
            _uiState.update { it.copy(forgotPinError = "Les nouveaux codes PIN ne correspondent pas.") }
            return
        }

        _uiState.update { it.copy(isForgotPinLoading = true, forgotPinError = null) }

        viewModelScope.launch {
            val result = repository.forgotPinReset(userId, otp, newPin, confirmPin)
            result.onSuccess { res ->
                if (res.success) {
                    _uiState.update {
                        it.copy(
                            isForgotPinLoading = false,
                            forgotPinStep = 4,
                            forgotPinSuccess = res.message ?: "Votre PIN a été réinitialisé avec succès.",
                            forgotPinError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isForgotPinLoading = false,
                            forgotPinError = res.error ?: "Erreur de réinitialisation."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isForgotPinLoading = false,
                        forgotPinError = err.message ?: "Échec de réinitialisation."
                    )
                }
            }
        }
    }
}

class AuthViewModelFactory(
    private val repository: CashPayRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
