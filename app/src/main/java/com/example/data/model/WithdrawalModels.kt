package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WithdrawalPreviewDto(
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "fee") val fee: Double = 0.0,
    @Json(name = "total") val total: Double = 0.0,
    @Json(name = "balance") val balance: Double = 0.0,
    @Json(name = "sufficient") val sufficient: Boolean = false,
    @Json(name = "missing") val missing: Double = 0.0,
    @Json(name = "canConfirm") val canConfirm: Boolean = false
)

@JsonClass(generateAdapter = true)
data class WithdrawalPreviewResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "preview") val preview: WithdrawalPreviewDto? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class WithdrawalConfirmRequest(
    @Json(name = "method") val method: String, // "agent_cash" or "mobile_money"
    @Json(name = "agent") val agent: String, // Wallet ID or operator name
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String,
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class WithdrawalConfirmResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)
