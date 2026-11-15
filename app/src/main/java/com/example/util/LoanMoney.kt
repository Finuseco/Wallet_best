package com.example.util

import java.text.NumberFormat
import java.util.Locale

/** Display only: never converts, scales or alters the server's contractual amounts. */
fun formatLoanMoney(amount: Double, currency: String): String {
    val code = currency.trim().uppercase(Locale.ROOT)
    if (!amount.isFinite()) return "— $code"
    val format = NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        isGroupingUsed = true
        minimumFractionDigits = if (code == "CDF") 0 else 2
        maximumFractionDigits = 2
    }
    return "${format.format(amount)} $code"
}
fun formatLoanPercent(amount: Double): String = NumberFormat.getNumberInstance(Locale.FRANCE).apply {
    maximumFractionDigits = 2
}.format(amount) + " %"

fun parseLoanAmount(raw: String): Double? = raw.trim().replace(" ", "")
    .replace("\u00a0", "").replace("\u202f", "").replace(',', '.')
    .toDoubleOrNull()?.takeIf { it.isFinite() && it > 0 }
