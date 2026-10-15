package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WithdrawActionRequest(
    @Json(name = "operation") val operation: String, // "client_withdraw" or "agent_withdraw"
    @Json(name = "action") val action: String, // "start", "confirm_identity", "amount", "confirm_amount", "pin", "otp_channel", "otp"
    @Json(name = "operationId") val operationId: String? = null,
    
    // Identification
    @Json(name = "agentWalletId") val agentWalletId: String? = null,
    @Json(name = "clientWalletId") val clientWalletId: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "identifier") val identifier: String? = null,
    
    // Amount & Auth
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "pin") val pin: String? = null,
    @Json(name = "channel") val channel: String? = null,
    @Json(name = "otp") val otp: String? = null
)

@JsonClass(generateAdapter = true)
data class WithdrawActionResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "operationId") val operationId: String? = null,
    @Json(name = "operation") val operation: String? = null,
    @Json(name = "step") val step: String? = null,
    @Json(name = "nextStep") val nextStep: String? = null,
    @Json(name = "target") val target: TargetProfile? = null,
    
    // Financial/Preview
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "fee") val fee: Double? = null,
    @Json(name = "totalDebit") val totalDebit: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "balance") val balance: Double? = null,
    @Json(name = "missingAmount") val missingAmount: Double? = null,
    
    // Agent Withdraw Specifics
    @Json(name = "otpChannels") val otpChannels: List<String>? = null,
    @Json(name = "otpSent") val otpSent: Boolean? = null,
    @Json(name = "destination") val destination: String? = null,
    
    // Completed
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "commission") val commission: Double? = null,
    
    // Error
    @Json(name = "error") val error: String? = null,
    @Json(name = "preview") val preview: PreviewError? = null
)

@JsonClass(generateAdapter = true)
data class TargetProfile(
    @Json(name = "userId") val userId: Long? = null,
    @Json(name = "walletId") val walletId: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "firstName") val firstName: String? = null,
    @Json(name = "fullName") val fullName: String? = null,
    @Json(name = "avatar") val avatar: String? = null,
    @Json(name = "boutiqueName") val boutiqueName: String? = null,
    @Json(name = "role") val role: String? = null,
    @Json(name = "status") val status: String? = null
)

@JsonClass(generateAdapter = true)
data class PreviewError(
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "fee") val fee: Double? = null,
    @Json(name = "totalDebit") val totalDebit: Double? = null,
    @Json(name = "balance") val balance: Double? = null,
    @Json(name = "missingAmount") val missingAmount: Double? = null,
    @Json(name = "message") val message: String? = null
)
