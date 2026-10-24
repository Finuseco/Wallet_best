package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsdtAddressResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "currency") val currency: String? = "USDT",
    @Json(name = "network") val network: String? = "TRC20",
    @Json(name = "address") val address: String? = null,
    @Json(name = "existing") val existing: Boolean? = false,
    @Json(name = "error") val error: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class UsdtBalanceResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "currency") val currency: String? = "USDT_TRC20",
    @Json(name = "network") val network: String? = "TRC20",
    @Json(name = "address") val address: String? = null,
    @Json(name = "balance") val balance: Double = 0.0,
    @Json(name = "onChainBalance") val onChainBalance: Double? = null,
    @Json(name = "source") val source: String? = "CASHPAY_LEDGER",
    @Json(name = "error") val error: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class UsdtDepositItem(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "amount") val amount: String? = "0.000000",
    @Json(name = "tx_hash") val txHash: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "confirmations") val confirmations: Int? = 0,
    @Json(name = "required_confirmations") val requiredConfirmations: Int? = 1,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class UsdtDepositsResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "currency") val currency: String? = "USDT_TRC20",
    @Json(name = "deposits") val deposits: List<UsdtDepositItem> = emptyList(),
    @Json(name = "error") val error: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class UsdtTransferRequest(
    @Json(name = "amount") val amount: String,
    @Json(name = "currency") val currency: String = "USDT",
    @Json(name = "recipientAddress") val recipientAddress: String,
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class UsdtTransferResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "status") val status: String? = null,
    @Json(name = "transferId") val transferId: String? = null,
    @Json(name = "txHash") val txHash: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)
