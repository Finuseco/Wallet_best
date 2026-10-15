package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InternationalTransferClaimDto(
    @Json(name = "id") val rawId: Any? = null,
    @Json(name = "provider") val provider: String? = "Transfert",
    @Json(name = "trackingNumber") val trackingNumber: String? = "",
    @Json(name = "expectedAmount") val rawExpectedAmount: Double? = 0.0,
    @Json(name = "expectedCurrency") val expectedCurrency: String? = "USD",
    @Json(name = "senderCountry") val senderCountry: String? = "",
    @Json(name = "receiveInCurrency") val receiveInCurrency: String? = "USD",
    @Json(name = "status") val status: String? = "pending",
    @Json(name = "userDocumentId") val userDocumentId: Long? = null,
    @Json(name = "rejectionReason") val rejectionReason: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "updatedAt") val updatedAt: String? = null
) {
    val id: Long
        get() = when (val v = rawId) {
            is Number -> v.toLong()
            is String -> v.toLongOrNull() ?: 0L
            else -> 0L
        }

    val expectedAmount: Double
        get() = rawExpectedAmount ?: 0.0

    val providerDisplayName: String
        get() = when (provider?.lowercase()?.trim().orEmpty()) {
            "western_union", "westernunion", "wu" -> "Western Union"
            "moneygram", "money_gram", "mg" -> "MoneyGram"
            "ria" -> "Ria"
            else -> provider?.replace("_", " ")?.replaceFirstChar { it.uppercase() } ?: "Transfert International"
        }

    val statusDisplayName: String
        get() = when (status?.lowercase()?.trim().orEmpty()) {
            "pending" -> "En attente"
            "processing" -> "En cours de traitement"
            "approved" -> "Approuvée"
            "processing_payment" -> "Paiement en cours"
            "completed" -> "Terminée"
            "rejected" -> "Rejetée"
            "failed" -> "Échec"
            else -> status?.replace("_", " ")?.replaceFirstChar { it.uppercase() } ?: "En attente"
        }

    val isPendingOrProcessing: Boolean
        get() = (status?.lowercase()?.trim().orEmpty()) in listOf("pending", "processing", "approved", "processing_payment")

    val isTerminal: Boolean
        get() = (status?.lowercase()?.trim().orEmpty()) in listOf("completed", "rejected", "failed")
}

@JsonClass(generateAdapter = true)
data class CreateClaimRequest(
    @Json(name = "provider") val provider: String,
    @Json(name = "trackingNumber") val trackingNumber: String,
    @Json(name = "expectedAmount") val expectedAmount: Double,
    @Json(name = "expectedCurrency") val expectedCurrency: String,
    @Json(name = "senderCountry") val senderCountry: String,
    @Json(name = "receiveInCurrency") val receiveInCurrency: String
)

@JsonClass(generateAdapter = true)
data class CreateClaimResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "claim") val claimItem: InternationalTransferClaimDto? = null,
    @Json(name = "data") val dataItem: InternationalTransferClaimDto? = null,
    @Json(name = "error") val error: String? = null
) {
    val claim: InternationalTransferClaimDto?
        get() = claimItem ?: dataItem
}

@JsonClass(generateAdapter = true)
data class ClaimsListResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "count") val count: Int? = null,
    @Json(name = "claims") val claimsList: List<InternationalTransferClaimDto>? = null,
    @Json(name = "data") val dataList: List<InternationalTransferClaimDto>? = null,
    @Json(name = "error") val error: String? = null
) {
    val claims: List<InternationalTransferClaimDto>
        get() = claimsList ?: dataList ?: emptyList()
}

@JsonClass(generateAdapter = true)
data class ClaimDetailResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "claim") val claimItem: InternationalTransferClaimDto? = null,
    @Json(name = "data") val dataItem: InternationalTransferClaimDto? = null,
    @Json(name = "error") val error: String? = null
) {
    val claim: InternationalTransferClaimDto?
        get() = claimItem ?: dataItem
}
