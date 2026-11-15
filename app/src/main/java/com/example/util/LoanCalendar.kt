package com.example.util

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/** Render the contractual calendar date without shifting it to the phone's timezone. */
fun formatLoanCalendarDate(value: String?): String {
    val raw = value?.trim().orEmpty()
    if (raw.isBlank()) return "Non renseignée"
    val date = raw.take(10)
    if (!Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}").matches(date)) return raw
    val utc = TimeZone.getTimeZone("UTC")
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply {
        isLenient = false
        timeZone = utc
    }
    val position = ParsePosition(0)
    val parsed = parser.parse(date, position) ?: return raw
    if (position.index != date.length) return raw
    return SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH).apply { timeZone = utc }.format(parsed)
}

/** Full approval/due instant in the explicitly displayed device timezone. Legacy date-only data has no invented time. */
fun formatLoanCalendarDateTime(value: String?, zone: TimeZone = TimeZone.getDefault()): String {
    val raw = value?.trim().orEmpty()
    if (raw.isBlank()) return "Non renseignée"
    if (raw.length == 10) return formatLoanCalendarDate(raw)
    for (pattern in listOf("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", "yyyy-MM-dd'T'HH:mm:ssXXX")) {
        val parser = SimpleDateFormat(pattern, Locale.ROOT).apply { isLenient = false }
        val position = ParsePosition(0)
        val parsed = parser.parse(raw, position)
        if (parsed != null && position.index == raw.length) {
            return SimpleDateFormat("EEEE dd MMMM yyyy 'à' HH:mm:ss z", Locale.FRENCH)
                .apply { timeZone = zone }.format(parsed)
        }
    }
    return raw
}
