package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRequest(
    @Json(name = "amount") val amount: Double,
    @Json(name = "fromCurrency") val fromCurrency: String,
    @Json(name = "toCurrency") val toCurrency: String,
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class ExchangeResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "transactionId") val transactionId: Long? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "fromCurrency") val fromCurrency: String? = null,
    @Json(name = "toCurrency") val toCurrency: String? = null,
    @Json(name = "rate") val rate: Double? = null,
    @Json(name = "toAmount") val toAmount: Double? = null,
    @Json(name = "fee") val fee: Double? = 0.0,
    @Json(name = "senderNewBalance") val senderNewBalance: Double? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ExchangeRateItemDto(
    @Json(name = "from") val from: String? = null,
    @Json(name = "to") val to: String? = null,
    @Json(name = "rate") val rate: Double? = null,
    @Json(name = "pair") val pair: String? = null
)

@JsonClass(generateAdapter = true)
data class ExchangeRatesResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "countryCode") val countryCode: String? = null,
    @Json(name = "nationalCurrency") val nationalCurrency: String? = null,
    @Json(name = "rates") val rates: Map<String, Double>? = null,
    @Json(name = "pairs") val pairs: List<ExchangeRateItemDto>? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class RealtimeQuoteDto(
    @Json(name = "fromAmount") val fromAmount: Double? = null,
    @Json(name = "fromCurrency") val fromCurrency: String? = null,
    @Json(name = "toAmount") val toAmount: Double? = null,
    @Json(name = "toCurrency") val toCurrency: String? = null,
    @Json(name = "rate") val rate: Double? = null,
    @Json(name = "quoteId") val quoteId: String? = null
)

@JsonClass(generateAdapter = true)
data class RealtimeQuoteResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "quote") val quote: RealtimeQuoteDto? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ServerActionRateDbItem(
    @Json(name = "from_currency") val fromCurrency: String? = null,
    @Json(name = "to_currency") val toCurrency: String? = null,
    @Json(name = "rate") val rate: Double? = null
)

@JsonClass(generateAdapter = true)
data class CryptoExchangeRequest(
    @Json(name = "direction") val direction: String,
    @Json(name = "currency") val currency: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "pin") val pin: String,
    @Json(name = "country") val country: String? = null
)

@JsonClass(generateAdapter = true)
data class CryptoExchangeDataDto(
    @Json(name = "direction") val direction: String? = null,
    @Json(name = "fiatCurrency") val fiatCurrency: String? = null,
    @Json(name = "fiatAmount") val fiatAmount: Double? = null,
    @Json(name = "cryptoCurrency") val cryptoCurrency: String? = null,
    @Json(name = "cryptoAmount") val cryptoAmount: Double? = null,
    @Json(name = "exchangeRate") val exchangeRate: Double? = null,
    @Json(name = "fee") val fee: Double? = null,
    @Json(name = "status") val status: String? = null
)

@JsonClass(generateAdapter = true)
data class CryptoExchangeResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "data") val data: CryptoExchangeDataDto? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "rate") val rate: Double? = null,
    @Json(name = "toAmount") val toAmount: Double? = null,
    @Json(name = "fee") val feeFlat: Double? = null
)
