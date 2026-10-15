package com.example.data.model

import com.example.data.local.CpkKeyEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CpkIssueRequest(
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "securityLevel") val securityLevel: String = "none", // "none" or "pin_protected"
    @Json(name = "keyPin") val keyPin: String? = null,
    @Json(name = "userPin") val userPin: String
)

@JsonClass(generateAdapter = true)
data class CpkIssueResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "signature") val signature: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "securityLevel") val securityLevel: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "fileContent") val fileContent: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class CpkKeyDto(
    @Json(name = "signature") val signature: String,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "securityLevel") val securityLevel: String = "none",
    @Json(name = "status") val status: String = "active",
    @Json(name = "remainingAmount") val remainingAmount: Double? = null,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "issuerId") val issuerId: Long? = null
)

@JsonClass(generateAdapter = true)
data class CpkRecoverRequest(
    @Json(name = "signature") val signature: String,
    @Json(name = "keyPin") val keyPin: String? = null,
    @Json(name = "partialAmount") val partialAmount: Double? = null
)

@JsonClass(generateAdapter = true)
data class CpkRecoverResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "message") val message: String? = null,
    @Json(name = "transactionId") val transactionId: String? = null,
    @Json(name = "amountRecovered") val amountRecovered: Double? = null,
    @Json(name = "remainingAmount") val remainingAmount: Double? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class CpkCancelRequest(
    @Json(name = "signature") val signature: String,
    @Json(name = "userPin") val userPin: String
)

@JsonClass(generateAdapter = true)
data class CpkCancelResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "message") val message: String? = null,
    @Json(name = "refundedAmount") val refundedAmount: Double? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class CpkStatusResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "signature") val signature: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "remainingAmount") val remainingAmount: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "securityLevel") val securityLevel: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "data") val data: CpkKeyDto? = null
)

@JsonClass(generateAdapter = true)
data class CpkFilePayload(
    @Json(name = "format") val format: String = "FINUSECO_CPK_V1",
    @Json(name = "signature") val signature: String,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String,
    @Json(name = "securityLevel") val securityLevel: String = "none",
    @Json(name = "createdAt") val createdAt: String,
    @Json(name = "issuerPhone") val issuerPhone: String? = null,
    @Json(name = "checksum") val checksum: String? = null
)

@JsonClass(generateAdapter = true)
data class CpkCloudDriveBackup(
    @Json(name = "vaultVersion") val vaultVersion: String = "1.0",
    @Json(name = "exportedAt") val exportedAt: String,
    @Json(name = "userPhone") val userPhone: String? = null,
    @Json(name = "keysCount") val keysCount: Int,
    @Json(name = "keys") val keys: List<CpkKeyEntity> = emptyList()
)
