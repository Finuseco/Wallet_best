package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- AGENT DEPOSIT ---
@JsonClass(generateAdapter = true)
data class AgentDepositClientDto(
    @Json(name = "userId") val userId: Long? = null,
    @Json(name = "walletId") val walletId: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "firstName") val firstName: String? = null,
    @Json(name = "fullName") val fullName: String? = null,
    @Json(name = "role") val role: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "profilePhotoUrl") val profilePhotoUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentDepositDetailDto(
    @Json(name = "transactionId") val transactionId: Any? = null,
    @Json(name = "clientWalletId") val clientWalletId: String? = null,
    @Json(name = "clientName") val clientName: String? = null,
    @Json(name = "depositAmount") val depositAmount: Double? = null,
    @Json(name = "depositedAmount") val depositedAmount: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "agentFee") val agentFee: Double? = 0.0,
    @Json(name = "totalDebitAgent") val totalDebitAgent: Double? = null,
    @Json(name = "totalDebited") val totalDebited: Double? = null,
    @Json(name = "agentBalanceBefore") val agentBalanceBefore: Double? = null,
    @Json(name = "agentBalanceAfter") val agentBalanceAfter: Double? = null,
    @Json(name = "agentRemainingBalance") val agentRemainingBalance: Double? = null,
    @Json(name = "clientRemainingBalance") val clientRemainingBalance: Double? = null,
    @Json(name = "completedAt") val completedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentDepositPreviewDto(
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "fee") val fee: Double? = 0.0,
    @Json(name = "totalDebit") val totalDebit: Double? = null,
    @Json(name = "balance") val balance: Double? = null,
    @Json(name = "missingAmount") val missingAmount: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentDepositRequest(
    @Json(name = "step") val step: String, // "identify", "confirm_client", "amount", "pin"
    @Json(name = "clientWalletId") val clientWalletId: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "pin") val pin: String? = null,
    @Json(name = "clientRef") val clientRef: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentDepositResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "step") val step: String? = null,
    @Json(name = "nextStep") val nextStep: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "operation") val operation: String? = null,
    @Json(name = "client") val client: AgentDepositClientDto? = null,
    @Json(name = "agentBalances") val agentBalances: Map<String, Double>? = null,
    @Json(name = "deposit") val deposit: AgentDepositDetailDto? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "fee") val fee: Double? = 0.0,
    @Json(name = "totalDebit") val totalDebit: Double? = null,
    @Json(name = "balance") val balance: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "transactionId") val transactionId: Any? = null,
    @Json(name = "commission") val commission: Double? = 0.0,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "preview") val preview: AgentDepositPreviewDto? = null
)

// --- AGENT WITHDRAW ---
@JsonClass(generateAdapter = true)
data class AgentWithdrawRequest(
    @Json(name = "operation") val operation: String = "client_withdraw",
    @Json(name = "clientRef") val clientRef: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String,
    @Json(name = "authMethod") val authMethod: String = "pin", // "pin" or "otp"
    @Json(name = "authCode") val authCode: String, // 4-digit PIN or 6-digit OTP from client
    @Json(name = "agentPin") val agentPin: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentWithdrawResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "transactionId") val transactionId: Long? = null,
    @Json(name = "fee") val fee: Double? = 0.0,
    @Json(name = "totalDebit") val totalDebit: Double? = 0.0,
    @Json(name = "commission") val commission: Double? = 0.0,
    @Json(name = "error") val error: String? = null
)

// --- AGENT LOAN REPAYMENT ---
@JsonClass(generateAdapter = true)
data class AgentLoanRepaymentRequest(
    @Json(name = "clientRef") val clientRef: String,
    @Json(name = "currency") val currency: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class AgentLoanRepaymentResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "paid") val paid: Double? = 0.0,
    @Json(name = "commission") val commission: Double? = 0.0,
    @Json(name = "remaining") val remaining: Double? = 0.0,
    @Json(name = "currency") val currency: String? = "USD",
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentLoanTargetResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "client") val client: AgentClientInfo? = null,
    @Json(name = "loan") val loan: AgentLoanInfo? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentClientInfo(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "walletId") val walletId: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentLoanInfo(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "currency") val currency: String? = "USD",
    @Json(name = "remainingBalance") val remainingBalance: Double? = 0.0,
    @Json(name = "amount") val amount: Double? = 0.0
)

// --- AGENT COMMISSION TRANSFER / BASCULE ---
@JsonClass(generateAdapter = true)
data class AgentCommissionTransferRequest(
    @Json(name = "currency") val currency: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class AgentCommissionTransferResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "operation") val operation: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "transactionId") val transactionId: Long? = null,
    @Json(name = "amount") val amount: Double? = 0.0,
    @Json(name = "currency") val currency: String? = "USD",
    @Json(name = "remainingCommission") val remainingCommission: Double? = 0.0,
    @Json(name = "newMainBalance") val newMainBalance: Double? = 0.0,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

// --- AGENT BALANCES RETRIEVAL ---
@JsonClass(generateAdapter = true)
data class AgentCommissionsResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "balances") val balances: Map<String, Double>? = null,
    @Json(name = "commissionBalance") val commissionBalance: Map<String, Double>? = null,
    @Json(name = "error") val error: String? = null
)

// --- AGENT OPERATION RECORD ---
@JsonClass(generateAdapter = true)
data class AgentOperationRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: String, // "DEPOSIT", "WITHDRAW", "LOAN_REPAY", "COMMISSION_SWEEP"
    val title: String,
    val clientRef: String? = null,
    val amount: Double,
    val currency: String,
    val commission: Double = 0.0,
    val reference: String? = null,
    val date: String = "",
    val status: String = "Complété"
)

// --- AGENT CUSTOMER REGISTRATION (OPTIONS, REGISTER, LIST) ---

fun Any?.asOptionString(): String {
    if (this == null) return ""
    if (this is String) return this
    if (this is Map<*, *>) {
        val v = this["key"] ?: this["code"] ?: this["id"] ?: this["name"] ?: this["label"] ?: this["value"]
        return v?.toString() ?: ""
    }
    return this.toString()
}

data class AgentProvinceItem(
    val code: String,
    val name: String
)

@JsonClass(generateAdapter = true)
data class AgentCountryOptionDto(
    @Json(name = "code") val code: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "dial_code") val dialCode: String? = null,
    @Json(name = "currency_code") val currencyCode: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentProvinceOptionDto(
    @Json(name = "code") val code: String? = null,
    @Json(name = "name") val name: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentCustomerOptionsMap(
    @Json(name = "account_type") val accountType: List<Any>? = emptyList(),
    @Json(name = "gender") val gender: List<Any>? = emptyList(),
    @Json(name = "marital_status") val maritalStatus: List<Any>? = emptyList(),
    @Json(name = "language") val language: List<Any>? = emptyList(),
    @Json(name = "ussd_language") val ussdLanguage: List<Any>? = emptyList(),
    @Json(name = "id_type") val idType: List<Any>? = emptyList(),
    @Json(name = "representative_relation") val representativeRelation: List<Any>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class AgentCustomerOptionsResponse(
    @Json(name = "success") val success: Boolean? = true,
    @Json(name = "country") val country: AgentCountryOptionDto? = null,
    @Json(name = "provinces") val provinces: List<Any>? = emptyList(),
    @Json(name = "account_type") val accountType: List<Any>? = null,
    @Json(name = "options") val options: AgentCustomerOptionsMap? = null,
    @Json(name = "error") val error: String? = null
) {
    fun getNormalizedProvinces(): List<AgentProvinceItem> {
        val raw = provinces ?: emptyList()
        return raw.mapNotNull { item ->
            when (item) {
                is Map<*, *> -> {
                    val n = item["name"]?.toString() ?: item["label"]?.toString() ?: item["value"]?.toString()
                    val c = item["code"]?.toString() ?: item["id"]?.toString() ?: n
                    if (!n.isNullOrBlank()) AgentProvinceItem(code = c ?: n, name = n) else null
                }
                is String -> {
                    if (item.isNotBlank()) AgentProvinceItem(code = item, name = item) else null
                }
                else -> null
            }
        }
    }

    fun getAccountTypes(): List<String> {
        val rootList = accountType?.mapNotNull { it.asOptionString().ifBlank { null } }
        val mapList = options?.accountType?.mapNotNull { it.asOptionString().ifBlank { null } }
        val combined = (rootList ?: mapList ?: emptyList()).filter { it.isNotBlank() }
        return if (combined.isNotEmpty()) combined else listOf("national", "diaspora", "business")
    }
}

@JsonClass(generateAdapter = true)
data class AgentCustomerRepresentativeDto(
    @Json(name = "name") val name: String,
    @Json(name = "contact") val contact: String,
    @Json(name = "relation") val relation: String
)

@JsonClass(generateAdapter = true)
data class AgentRegisterCustomerRequest(
    @Json(name = "account_type") val accountType: String = "national",
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "phone") val phone: String, // normalized with country prefix or raw
    @Json(name = "province") val province: String,
    @Json(name = "country") val country: String? = null,
    @Json(name = "country_code") val countryCode: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "middle_name") val middleName: String? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "marital_status") val maritalStatus: String? = null,
    @Json(name = "birth_date") val birthDate: String? = null,
    @Json(name = "birth_place") val birthPlace: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "id_type") val idType: String? = null,
    @Json(name = "id_number") val idNumber: String? = null,
    @Json(name = "id_issued_date") val idIssuedDate: String? = null,
    @Json(name = "id_expiry_date") val idExpiryDate: String? = null,
    @Json(name = "profession") val profession: String? = null,
    @Json(name = "nationality") val nationality: String? = null,
    @Json(name = "income_per_month") val incomePerMonth: Double? = null,
    @Json(name = "language") val language: String? = "fr",
    @Json(name = "ussd_language") val ussdLanguage: String? = "fr",
    @Json(name = "company_name") val companyName: String? = null,
    @Json(name = "activity_description") val activityDescription: String? = null,
    @Json(name = "profile_photo") val profilePhoto: String? = null,
    @Json(name = "id_front_image") val idFrontImage: String? = null,
    @Json(name = "id_back_image") val idBackImage: String? = null,
    @Json(name = "signature_image") val signatureImage: String? = null,
    @Json(name = "representative") val representative: AgentCustomerRepresentativeDto? = null
)

@JsonClass(generateAdapter = true)
data class AgentCreatedCustomerDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "id_wallet") val idWallet: String? = null,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "last_name") val lastName: String? = null,
    @Json(name = "avatar") val avatar: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "countryCode") val countryCode: String? = null,
    @Json(name = "province") val province: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentCustomerReferralDto(
    @Json(name = "enabled") val enabled: Boolean? = true,
    @Json(name = "referrer_id") val referrerId: Long? = null,
    @Json(name = "referrer_id_wallet") val referrerIdWallet: String? = null,
    @Json(name = "status") val status: String? = "Pending Deposit"
)

@JsonClass(generateAdapter = true)
data class AgentCustomerCreatedByDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "id_wallet") val idWallet: String? = null,
    @Json(name = "full_name") val fullName: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentRegisterCustomerResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "code") val code: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "customer") val customer: AgentCreatedCustomerDto? = null,
    @Json(name = "referral") val referral: AgentCustomerReferralDto? = null,
    @Json(name = "created_by") val createdBy: AgentCustomerCreatedByDto? = null,
    @Json(name = "temporary_password") val temporaryPassword: String? = null,
    @Json(name = "temp_password") val tempPassword: String? = null,
    @Json(name = "pin") val pin: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentCustomerItemDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "id_wallet") val idWallet: String? = null,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "last_name") val lastName: String? = null,
    @Json(name = "avatar") val avatar: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "province") val province: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "referral") val referral: AgentCustomerReferralDto? = null
)

@JsonClass(generateAdapter = true)
data class AgentCustomerListResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "count") val count: Int? = 0,
    @Json(name = "customers") val customers: List<AgentCustomerItemDto> = emptyList(),
    @Json(name = "error") val error: String? = null
)
