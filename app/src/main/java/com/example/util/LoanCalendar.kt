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
