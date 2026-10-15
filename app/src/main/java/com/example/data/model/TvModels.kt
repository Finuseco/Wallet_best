package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class TvProviderInfo(
    val code: String,
    val name: String,
    val description: String,
    val smartcardLabel: String,
    val smartcardPlaceholder: String,
    val isSmartcardRequired: Boolean = true
) {
    companion object {
        val ALL_PROVIDERS = listOf(
            TvProviderInfo(
                code = "CPLUS",
                name = "Canal+ RDC",
                description = "Forfaits ACCESS, EVASION, ACCESS+, TOUT CANAL+",
                smartcardLabel = "Numéro de carte d'abonné Canal+",
                smartcardPlaceholder = "Ex: 14 chiffres de la carte à puce"
            ),
            TvProviderInfo(
                code = "EASYTV",
                name = "EasyTV",
                description = "Bouquet TNT EasyTV RDC",
                smartcardLabel = "Numéro de décodeur EasyTV",
                smartcardPlaceholder = "Ex: Numéro inscrit sous le décodeur"
            ),
            TvProviderInfo(
                code = "BLUS",
                name = "BLUS",
                description = "Bouquet numérique BLUS",
                smartcardLabel = "Numéro d'abonné ou décodeur BLUS",
                smartcardPlaceholder = "Ex: Identifiant client BLUS"
            )
        )
    }
}

@JsonClass(generateAdapter = true)
data class TvPackageDto(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "provider_code") val providerCode: String? = null,
    @Json(name = "provider") val provider: String? = null,
    @Json(name = "provider_name") val providerName: String? = null,
    @Json(name = "package_name") val packageName: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "display_price") val displayPrice: Double? = null,
    @Json(name = "price") val price: Double? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "display_currency") val displayCurrency: String? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "channels") val channels: String? = null
) {
    val effectiveName: String
        get() = packageName?.takeIf { it.isNotBlank() }
            ?: name?.takeIf { it.isNotBlank() }
            ?: "Forfait Standard"

    val effectivePrice: Double
        get() = displayPrice ?: price ?: amount ?: 0.0

    val effectiveCurrency: String
        get() = displayCurrency?.takeIf { it.isNotBlank() }
            ?: currency?.takeIf { it.isNotBlank() }
            ?: "CDF"
}

@JsonClass(generateAdapter = true)
data class TvPackagesResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "provider") val provider: String? = null,
    @Json(name = "packages") val packages: List<TvPackageDto> = emptyList(),
    @Json(name = "error") val error: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class TvSubscriptionRequest(
    @Json(name = "provider") val provider: String,
    @Json(name = "packageId") val packageId: Long,
    @Json(name = "smartcardNumber") val smartcardNumber: String,
    @Json(name = "currency") val currency: String = "CDF",
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class TvSubscriptionResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "message") val message: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "transactionId") val transactionId: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "provider") val provider: String? = null,
    @Json(name = "packageId") val packageId: Long? = null,
    @Json(name = "packageName") val packageName: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "rate") val rate: Double? = null,
    @Json(name = "debitedAmount") val debitedAmount: Double? = null,
    @Json(name = "debitedCurrency") val debitedCurrency: String? = null,
    @Json(name = "smartcardNumber") val smartcardNumber: String? = null,
    @Json(name = "date") val date: String? = null,
    @Json(name = "error") val error: String? = null
)
