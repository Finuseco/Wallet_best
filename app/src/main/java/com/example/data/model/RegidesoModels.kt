package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class RegidesoProvince(
    val code: String,
    val name: String,
    val description: String
) {
    companion object {
        val ALL_PROVINCES = listOf(
            RegidesoProvince("KIN", "Kinshasa", "Kinshasa - Ville province"),
            RegidesoProvince("KC", "Kongo Central", "Matadi, Boma, Muanda"),
            RegidesoProvince("KAT", "Haut-Katanga / Katanga", "Lubumbashi, Likasi, Kolwezi"),
            RegidesoProvince("NK", "Nord-Kivu", "Goma, Beni, Butembo"),
            RegidesoProvince("SK", "Sud-Kivu", "Bukavu, Uvira"),
            RegidesoProvince("MB", "Mbuji-Mayi", "Kasaï-Oriental"),
            RegidesoProvince("KAN", "Kananga", "Kasaï-Central")
        )
    }
}

@JsonClass(generateAdapter = true)
data class RegidesoFactureDto(
    @Json(name = "numero_facture") val numeroFacture: String? = null,
    @Json(name = "nom_client") val nomClient: String? = null,
    @Json(name = "statut_facture") val statutFacture: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "montant") val montant: Double? = null,
    @Json(name = "currency") val currency: String? = "CDF",
    @Json(name = "periode") val periode: String? = null,
    @Json(name = "abonnement_id") val abonnementId: String? = null
) {
    val effectiveAmount: Double
        get() = amount ?: montant ?: 0.0

    val effectiveCurrency: String
        get() = currency?.takeIf { it.isNotBlank() } ?: "CDF"

    val isUnpaid: Boolean
        get() = statutFacture?.contains("impay", ignoreCase = true) == true ||
                statutFacture?.contains("unpaid", ignoreCase = true) == true ||
                statutFacture?.contains("due", ignoreCase = true) == true

    val isPaid: Boolean
        get() = statutFacture?.equals("payée", ignoreCase = true) == true ||
                statutFacture?.equals("payee", ignoreCase = true) == true ||
                statutFacture?.equals("paid", ignoreCase = true) == true
}

@JsonClass(generateAdapter = true)
data class RegidesoInvoicesResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "factures") val factures: List<RegidesoFactureDto> = emptyList(),
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)
