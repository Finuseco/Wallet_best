package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DepositMobileMoneyRequest(
    @Json(name = "method") val method: String = "mobile_money",
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "operator") val operator: String,
    @Json(name = "phoneNumber") val phoneNumber: String,
    @Json(name = "countryCode") val countryCode: String = "CD"
)

@JsonClass(generateAdapter = true)
data class DepositMobileMoneyResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "status") val status: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class DepositGatewayRequest(
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String = "USD"
)

@JsonClass(generateAdapter = true)
data class DepositGatewayResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "status") val status: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "paymentUrl") val paymentUrl: String? = null,
    @Json(name = "error") val error: String? = null
)

enum class MobileMoneyOperator(val code: String, val displayName: String) {
    MPESA("mpesa", "M-Pesa"),
    ORANGE_MONEY("orange_money", "Orange Money"),
    AFRIMONEY("afrimoney", "Afrimoney"),
    AIRTEL_MONEY("airtel_money", "Airtel Money"),
    MTN_MOMO("mtn_momo", "MTN MoMo");

    companion object {
        fun getOperatorsForCountry(countryCode: String?): List<MobileMoneyOperator> {
            return listOf(MPESA, ORANGE_MONEY, AFRIMONEY, AIRTEL_MONEY, MTN_MOMO)
        }
    }
}
