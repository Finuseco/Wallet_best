package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountryDto(
    @Json(name = "code") val code: String,
    @Json(name = "name") val name: String,
    @Json(name = "dialCode") val dialCode: String
) {
    val flagEmoji: String
        get() {
            if (code.length != 2) return ""
            val firstChar = Character.codePointAt(code.uppercase(), 0) - 0x41 + 0x1F1E6
            val secondChar = Character.codePointAt(code.uppercase(), 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
        }
}

@JsonClass(generateAdapter = true)
data class CountriesResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "countries") val countries: List<CountryDto> = emptyList(),
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "walletId") val walletId: String? = null,
    @Json(name = "phone") val phone: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "userId") val userId: Long? = null,
    @Json(name = "walletId") val walletId: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "maskedPhone") val maskedPhone: String? = null,
    @Json(name = "maskedEmail") val maskedEmail: String? = null,
    @Json(name = "isGoogleLinked") val isGoogleLinked: Boolean? = null,
    @Json(name = "availableChannels") val availableChannels: List<String>? = null,
    @Json(name = "channels") val channels: List<String>? = null,
    @Json(name = "token") val token: String? = null,
    @Json(name = "sessionToken") val sessionToken: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
) {
    val effectiveChannels: List<String>
        get() = availableChannels ?: channels ?: listOf("sms", "email", "whatsapp")
}

@JsonClass(generateAdapter = true)
data class SendOtpRequest(
    @Json(name = "userId") val userId: Long,
    @Json(name = "channel") val channel: String
)

@JsonClass(generateAdapter = true)
data class SendOtpResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String? = null,
    @Json(name = "channel") val channel: String? = null,
    @Json(name = "destination") val destination: String? = null,
    @Json(name = "userId") val userId: Long? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class VerifyOtpRequest(
    @Json(name = "userId") val userId: Long,
    @Json(name = "otp") val otp: String
)

@JsonClass(generateAdapter = true)
data class VerifyOtpResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String? = null,
    @Json(name = "token") val token: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class VerifyPinRequest(
    @Json(name = "userId") val userId: Long,
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class UserRefDto(
    @Json(name = "id") val id: Long
)

@JsonClass(generateAdapter = true)
data class VerifyPinResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "authenticated") val authenticated: Boolean = false,
    @Json(name = "user") val user: UserRefDto? = null,
    @Json(name = "token") val token: String? = null,
    @Json(name = "sessionToken") val sessionToken: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class AvatarDto(
    @Json(name = "available") val available: Boolean = false,
    @Json(name = "source") val source: String? = null,
    @Json(name = "url") val url: String? = null,
    @Json(name = "default") val default: Boolean = true
)

@JsonClass(generateAdapter = true)
data class ProfileInstallationDto(
    @Json(name = "ready") val ready: Boolean = false,
    @Json(name = "userId") val userId: Long = 0,
    @Json(name = "walletId") val walletId: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "avatar") val avatar: AvatarDto? = null
)

@JsonClass(generateAdapter = true)
data class ZodiacDto(
    @Json(name = "key") val key: String? = null,
    @Json(name = "symbol") val symbol: String? = null,
    @Json(name = "nameFr") val nameFr: String? = null,
    @Json(name = "nameEn") val nameEn: String? = null
)

@JsonClass(generateAdapter = true)
data class AstroProfileDto(
    @Json(name = "userId") val userId: Long? = null,
    @Json(name = "birthDate") val birthDate: String? = null,
    @Json(name = "zodiac") val zodiac: ZodiacDto? = null
)

@JsonClass(generateAdapter = true)
data class RepresentativeDto(
    @Json(name = "name") val name: String? = null,
    @Json(name = "contact") val contact: String? = null,
    @Json(name = "relation") val relation: String? = null
)

@JsonClass(generateAdapter = true)
data class UserProfileDto(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "role") val role: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "accountType") val accountType: String? = null,
    @Json(name = "walletId") val walletId: String? = null,
    @Json(name = "language") val language: String? = "fr",
    @Json(name = "ussdLanguage") val ussdLanguage: String? = "fr",
    @Json(name = "fullName") val fullName: String? = null,
    @Json(name = "firstName") val firstName: String? = null,
    @Json(name = "lastName") val lastName: String? = null,
    @Json(name = "middleName") val middleName: String? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "maritalStatus") val maritalStatus: String? = null,
    @Json(name = "birthDate") val birthDate: String? = null,
    @Json(name = "birthPlace") val birthPlace: String? = null,
    @Json(name = "nationality") val nationality: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "country") val country: String? = "République démocratique du Congo",
    @Json(name = "province") val province: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "profession") val profession: String? = null,
    @Json(name = "activityDescription") val activityDescription: String? = null,
    @Json(name = "incomePerMonth") val incomePerMonth: String? = null,
    @Json(name = "profilePhoto") val profilePhoto: String? = null,
    @Json(name = "profilePhotoUrl") val profilePhotoUrl: String? = null,
    @Json(name = "coverPhoto") val coverPhoto: String? = null,
    @Json(name = "coverPhotoUrl") val coverPhotoUrl: String? = null,
    @Json(name = "signatureImage") val signatureImage: String? = null,
    @Json(name = "googleId") val googleId: String? = null,
    @Json(name = "zoomId") val zoomId: String? = null,
    @Json(name = "notificationsChannel") val notificationsChannel: List<String>? = null,
    @Json(name = "assistantSettings") val assistantSettings: String? = null,
    @Json(name = "availabilityStatus") val availabilityStatus: String? = "offline",
    @Json(name = "schedule") val schedule: String? = null,
    @Json(name = "representative") val representative: RepresentativeDto? = null,
    @Json(name = "verificationStatus") val verificationStatus: String? = null,
    @Json(name = "tempPhotoExpiresAt") val tempPhotoExpiresAt: String? = null,
    @Json(name = "isCryptoActive") val isCryptoActive: Boolean? = false,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "astro") val astro: AstroProfileDto? = null,
    @Json(name = "boutiques") val boutiques: List<BoutiqueDto>? = null
)

@JsonClass(generateAdapter = true)
data class MeResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "found") val found: Boolean? = null,
    @Json(name = "profileInstallation") val profileInstallation: ProfileInstallationDto? = null,
    @Json(name = "profile") val profile: UserProfileDto? = null,
    @Json(name = "boutiques") val boutiques: List<BoutiqueDto>? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    @Json(name = "pin") val pin: String,
    @Json(name = "updates") val updates: Map<String, String>
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "action") val action: String,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "countryCode") val countryCode: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "account_type") val accountType: String? = null,
    @Json(name = "preferred_otp_channel") val preferredOtpChannel: String? = null,
    @Json(name = "language") val language: String? = "fr",
    @Json(name = "company_name") val companyName: String? = null,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "middle_name") val middleName: String? = null,
    @Json(name = "last_name") val lastName: String? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "birth_date") val birthDate: String? = null,
    @Json(name = "marital_status") val maritalStatus: String? = null,
    @Json(name = "nationality") val nationality: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "otp") val otp: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "profession") val profession: String? = null,
    @Json(name = "id_type") val idType: String? = null,
    @Json(name = "id_number") val idNumber: String? = null,
    @Json(name = "id_front_image") val idFrontImage: String? = null,
    @Json(name = "profile_photo") val profilePhoto: String? = null,
    @Json(name = "signature_image") val signatureImage: String? = null,
    @Json(name = "representative") val representative: RepresentativeDto? = null
)

@JsonClass(generateAdapter = true)
data class RegisterUserDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "id_wallet") val idWallet: String? = null,
    @Json(name = "walletId") val walletId: String? = null,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "firstName") val firstName: String? = null,
    @Json(name = "lastName") val lastName: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "account_type") val accountType: String? = null
) {
    val effectiveWalletId: String
        get() = idWallet ?: walletId ?: ""
    val effectiveFullName: String
        get() = fullName ?: "${firstName.orEmpty()} ${lastName.orEmpty()}".trim()
}

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "action") val action: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "countryCode") val countryCode: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "account_type") val accountType: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "emailVerificationRequired") val emailVerificationRequired: Boolean? = null,
    @Json(name = "phoneVerificationRequired") val phoneVerificationRequired: Boolean? = null,
    @Json(name = "phoneVerified") val phoneVerified: Boolean? = null,
    @Json(name = "emailVerified") val emailVerified: Boolean? = null,
    @Json(name = "next") val next: String? = null,
    @Json(name = "id_wallet") val idWallet: String? = null,
    @Json(name = "idWallet") val idWalletAlt: String? = null,
    @Json(name = "token") val token: String? = null,
    @Json(name = "user") val user: RegisterUserDto? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
) {
    val effectiveWalletId: String?
        get() = idWallet ?: idWalletAlt ?: user?.effectiveWalletId
    val effectiveFullName: String?
        get() = user?.effectiveFullName
}

@JsonClass(generateAdapter = true)
data class WalletInfoDto(
    @Json(name = "id") val id: Long,
    @Json(name = "userId") val userId: Long,
    @Json(name = "walletId") val walletId: String,
    @Json(name = "isGroupWallet") val isGroupWallet: Boolean = false,
    @Json(name = "lastUpdated") val lastUpdated: String? = null
)

@JsonClass(generateAdapter = true)
data class NationalCurrencyDto(
    @Json(name = "countryCode") val countryCode: String? = null,
    @Json(name = "code") val code: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "symbol") val symbol: String? = null,
    @Json(name = "rate") val rate: Double? = null,
    @Json(name = "exchangeRate") val exchangeRate: Double? = null,
    @Json(name = "rateToUsd") val rateToUsd: Double? = null,
    @Json(name = "usdRate") val usdRate: Double? = null
) {
    val effectiveRate: Double?
        get() = rate ?: exchangeRate ?: rateToUsd ?: usdRate
}

@JsonClass(generateAdapter = true)
data class CryptoBalanceDto(
    @Json(name = "currency") val currency: String,
    @Json(name = "balance") val balance: Double
)

@JsonClass(generateAdapter = true)
data class BalancesDto(
    @Json(name = "fiat") val fiat: Map<String, Double> = emptyMap(),
    @Json(name = "crypto") val crypto: List<CryptoBalanceDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class BitcoinInfoDto(
    @Json(name = "address") val address: String? = null,
    @Json(name = "balance") val balance: Double = 0.0,
    @Json(name = "available") val available: Boolean = false
)

@JsonClass(generateAdapter = true)
data class CardInfoDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "brand") val brand: String? = null,
    @Json(name = "cardType") val cardType: String? = null,
    @Json(name = "currency") val currency: String? = "USD",
    @Json(name = "last4") val last4: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "isPrimary") val isPrimary: Boolean = false,
    @Json(name = "cardProviderId") val cardProviderId: String? = null
)

@JsonClass(generateAdapter = true)
data class WalletResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "wallet") val wallet: WalletInfoDto? = null,
    @Json(name = "nationalCurrency") val nationalCurrency: NationalCurrencyDto? = null,
    @Json(name = "balances") val balances: BalancesDto? = null,
    @Json(name = "bitcoin") val bitcoin: BitcoinInfoDto? = null,
    @Json(name = "cards") val cards: List<CardInfoDto> = emptyList(),
    @Json(name = "rate") val rate: Double? = null,
    @Json(name = "exchangeRate") val exchangeRate: Double? = null,
    @Json(name = "rates") val rates: Map<String, Double>? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class TransferCurrencyDto(
    @Json(name = "code") val code: String,
    @Json(name = "name") val name: String,
    @Json(name = "symbol") val symbol: String,
    @Json(name = "type") val type: String,
    @Json(name = "balance") val balance: Double,
    @Json(name = "rate") val rate: Double? = null,
    @Json(name = "exchangeRate") val exchangeRate: Double? = null
)

@JsonClass(generateAdapter = true)
data class TransfersMetaResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "country") val country: Map<String, String>? = null,
    @Json(name = "localCurrency") val localCurrency: NationalCurrencyDto? = null,
    @Json(name = "currencies") val currencies: List<TransferCurrencyDto> = emptyList(),
    @Json(name = "rate") val rate: Double? = null,
    @Json(name = "exchangeRate") val exchangeRate: Double? = null,
    @Json(name = "rates") val rates: Map<String, Double>? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class TransferRequest(
    @Json(name = "userId") val userId: Long,
    @Json(name = "receiverWalletId") val receiverWalletId: String? = null,
    @Json(name = "recipientAddress") val recipientAddress: String? = null,
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String,
    @Json(name = "pin") val pin: String? = null,
    @Json(name = "preview") val preview: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class TransactionDetailsDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "currency") val currency: NationalCurrencyDto? = null,
    @Json(name = "senderWalletId") val senderWalletId: String? = null,
    @Json(name = "receiverWalletId") val receiverWalletId: String? = null,
    @Json(name = "fee") val fee: Double? = null,
    @Json(name = "senderNewBalance") val senderNewBalance: Double? = null,
    @Json(name = "receiverNewBalance") val receiverNewBalance: Double? = null
)

@JsonClass(generateAdapter = true)
data class TransferResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "status") val status: Int? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "fee") val fee: Double? = null,
    @Json(name = "totalDebit") val totalDebit: Double? = null,
    @Json(name = "transaction") val transaction: TransactionDetailsDto? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class TransactionUserDto(
    @Json(name = "userId") val userId: Long? = null,
    @Json(name = "walletId") val walletId: String? = null,
    @Json(name = "fullName") val fullName: String? = null,
    @Json(name = "profilePhoto") val profilePhoto: String? = null
)

@JsonClass(generateAdapter = true)
data class TransactionDisplayDto(
    @Json(name = "kind") val kind: String? = null, // "person" or "service"
    @Json(name = "name") val name: String? = null,
    @Json(name = "avatar") val avatar: String? = null,
    @Json(name = "icon") val icon: String? = null
)

@JsonClass(generateAdapter = true)
data class TransactionItemDto(
    @Json(name = "id") val id: Long,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "status") val status: String? = "completed",
    @Json(name = "direction") val direction: String? = null, // "incoming" or "outgoing"
    @Json(name = "amount") val amount: String? = "0.00",
    @Json(name = "currency") val currency: String? = "USD",
    @Json(name = "fee") val fee: String? = "0.00",
    @Json(name = "description") val description: String? = null,
    @Json(name = "sender") val sender: TransactionUserDto? = null,
    @Json(name = "receiver") val receiver: TransactionUserDto? = null,
    @Json(name = "otherUser") val otherUser: TransactionUserDto? = null,
    @Json(name = "display") val display: TransactionDisplayDto? = null,
    @Json(name = "createdAt") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class TransactionsResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "userId") val userId: Long? = null,
    @Json(name = "count") val count: Int? = 0,
    @Json(name = "limit") val limit: Int? = 50,
    @Json(name = "offset") val offset: Int? = 0,
    @Json(name = "transactions") val transactions: List<TransactionItemDto> = emptyList(),
    @Json(name = "error") val error: String? = null
)

// --- FORGOT PIN MODELS ---
@JsonClass(generateAdapter = true)
data class ForgotPinRequestDto(
    @Json(name = "phone") val phone: String,
    @Json(name = "channel") val channel: String // "sms" or "whatsapp"
)

@JsonClass(generateAdapter = true)
data class ForgotPinRequestResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "channel") val channel: String? = null,
    @Json(name = "destination") val destination: String? = null,
    @Json(name = "userId") val userId: Long? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ForgotPinVerifyDto(
    @Json(name = "userId") val userId: Long,
    @Json(name = "otp") val otp: String
)

@JsonClass(generateAdapter = true)
data class ForgotPinVerifyResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ForgotPinResetDto(
    @Json(name = "userId") val userId: Long,
    @Json(name = "otp") val otp: String,
    @Json(name = "newPin") val newPin: String,
    @Json(name = "confirmPin") val confirmPin: String
)

@JsonClass(generateAdapter = true)
data class ForgotPinResetResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

// --- ACTIVATE AGENT MODELS ---
@JsonClass(generateAdapter = true)
data class ActivateAgentRequest(
    @Json(name = "plan") val plan: String, // "promo" or "normal"
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class ActivateAgentResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

// --- BIOMETRIC API MODELS ---
@JsonClass(generateAdapter = true)
data class BiometricActionRequest(
    @Json(name = "action") val action: String,
    @Json(name = "credentialId") val credentialId: String? = null,
    @Json(name = "publicKey") val publicKey: String? = null,
    @Json(name = "deviceName") val deviceName: String? = null,
    @Json(name = "signature") val signature: String? = null
)

@JsonClass(generateAdapter = true)
data class BiometricActionResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "challenge") val challenge: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)




