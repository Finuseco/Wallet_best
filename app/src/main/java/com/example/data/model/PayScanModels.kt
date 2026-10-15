package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScanPayInterpretRequest(
    @Json(name = "value") val value: String? = null,
    @Json(name = "data") val data: String? = null
)

@JsonClass(generateAdapter = true)
data class ScanPayInterpretResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "scan") val scan: ScanResultData? = null
)

@JsonClass(generateAdapter = true)
data class ScanResultData(
    @Json(name = "type") val type: String? = null, // "POS_SALE", "PAYMENT_REQUEST", "PRODUCT", "WALLET", "BARCODE", "UNKNOWN"
    @Json(name = "walletId") val walletId: String? = null,
    @Json(name = "value") val value: String? = null,
    @Json(name = "productId") val productId: String? = null,
    @Json(name = "barcode") val barcode: String? = null,
    @Json(name = "recipient") val recipient: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "paymentCurrency") val paymentCurrency: String? = null,
    @Json(name = "saleReference") val saleReference: String? = null,
    @Json(name = "boutiqueName") val boutiqueName: String? = null,
    @Json(name = "sellerName") val sellerName: String? = null,
    @Json(name = "sellerWalletId") val sellerWalletId: String? = null,
    @Json(name = "items") val items: List<PosSaleItemDto>? = null,
    @Json(name = "data") val nestedData: ScanResultData? = null
)

@JsonClass(generateAdapter = true)
data class PosSaleItemDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String = "",
    @Json(name = "quantity") val quantity: Int = 1,
    @Json(name = "sellingPrice") val sellingPrice: Double = 0.0,
    @Json(name = "currency") val currency: String? = "USD",
    @Json(name = "barcode") val barcode: String? = null
)

@JsonClass(generateAdapter = true)
data class ScanProductLookupRequest(
    @Json(name = "productId") val productId: String? = null,
    @Json(name = "barcode") val barcode: String? = null
)

@JsonClass(generateAdapter = true)
data class ScanProductLookupResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "lookup") val lookup: ProductLookupData? = null
)

@JsonClass(generateAdapter = true)
data class ProductLookupData(
    @Json(name = "productId") val productId: String? = null,
    @Json(name = "barcode") val barcode: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "sellingPrice") val sellingPrice: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "stock") val stock: Int? = null,
    @Json(name = "boutiqueName") val boutiqueName: String? = null
)

@JsonClass(generateAdapter = true)
data class PosSalePaymentRequest(
    @Json(name = "pin") val pin: String,
    @Json(name = "scanData") val scanData: PosSaleData
)

@JsonClass(generateAdapter = true)
data class PosSaleData(
    @Json(name = "type") val type: String = "POS_SALE",
    @Json(name = "sellerWalletId") val sellerWalletId: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "paymentCurrency") val paymentCurrency: String = "USD",
    @Json(name = "saleReference") val saleReference: String? = null,
    @Json(name = "boutiqueName") val boutiqueName: String? = null,
    @Json(name = "sellerName") val sellerName: String? = null,
    @Json(name = "items") val items: List<PosSaleItemDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PosSalePaymentResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "transaction") val transaction: PosTransactionDetails? = null
)

@JsonClass(generateAdapter = true)
data class PosTransactionDetails(
    @Json(name = "type") val type: String? = null,
    @Json(name = "saleReference") val saleReference: String? = null,
    @Json(name = "sellerWalletId") val sellerWalletId: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "paymentCurrency") val paymentCurrency: String? = null,
    @Json(name = "boutiqueName") val boutiqueName: String? = null,
    @Json(name = "sellerName") val sellerName: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null
)
