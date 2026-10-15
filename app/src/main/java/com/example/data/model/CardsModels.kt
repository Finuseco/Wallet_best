package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CardCatalogDto(
    @Json(name = "brand") val brand: String = "", // "visa", "mastercard"
    @Json(name = "type") val type: String = "", // "virtuelle", "physique"
    @Json(name = "currency") val currency: String = "USD", // "USD", "EUR"
    @Json(name = "name") val name: String = "",
    @Json(name = "price") val price: Double = 0.0,
    @Json(name = "fee") val fee: Double = 0.0,
    @Json(name = "total") val total: Double = 0.0,
    @Json(name = "available") val available: Boolean = true
)

@JsonClass(generateAdapter = true)
data class CardCatalogResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "cards") val cards: List<CardCatalogDto> = emptyList(),
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class PurchaseCardRequest(
    @Json(name = "brand") val brand: String,
    @Json(name = "card_type") val cardType: String, // "virtuelle", "physique"
    @Json(name = "currency") val currency: String,
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class CardDetailsResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "card") val card: CardInfoDto? = null,
    @Json(name = "pan") val pan: String? = null,
    @Json(name = "cvv") val cvv: String? = null,
    @Json(name = "expiry") val expiry: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class FundingConfigRequest(
    @Json(name = "type") val type: String, // "wallet", "cpk"
    @Json(name = "cpkSignature") val cpkSignature: String? = null,
    @Json(name = "cpkPin") val cpkPin: String? = null
)

@JsonClass(generateAdapter = true)
data class CardFeeResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "price") val price: Double? = null,
    @Json(name = "fee") val fee: Double? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "cost") val cost: Double? = null,
    @Json(name = "total") val total: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "fee_code") val feeCode: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class CardActionResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "card") val card: CardInfoDto? = null
)
