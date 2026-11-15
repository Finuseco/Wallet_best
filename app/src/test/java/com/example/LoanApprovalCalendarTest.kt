package com.example

import com.example.util.formatLoanCalendarDateTime
import java.util.TimeZone
import org.junit.Test
import org.junit.Assert.*

class LoanApprovalCalendarTest {
    @Test fun approvalAndWeeklyDueDateKeepTheExactClockAndWeekday() {
        val zone = TimeZone.getTimeZone("UTC")
        val approved = formatLoanCalendarDateTime("2026-11-16T11:12:13.456Z", zone)
        val due = formatLoanCalendarDateTime("2026-11-23T11:12:13.456Z", zone)
        assertTrue(approved.startsWith("lundi 16 novembre 2026"))
        assertTrue(due.startsWith("lundi 23 novembre 2026"))
        assertTrue(approved.contains("11:12:13"))
        assertTrue(due.contains("11:12:13"))
    }
    @Test fun timezoneIsExplicitAndMissingApprovalDoesNotInventADate() {
        assertEquals("Non renseignée", formatLoanCalendarDateTime(null))
        assertFalse(formatLoanCalendarDateTime("2026-11-16").contains("00:00"))
        assertTrue(formatLoanCalendarDateTime("2026-11-16T11:12:13.456Z", TimeZone.getTimeZone("GMT+02:00")).contains("13:12:13"))
        assertTrue(formatLoanCalendarDateTime("2026-11-15T23:12:13.456Z", TimeZone.getTimeZone("GMT+02:00")).startsWith("lundi 16 novembre"))
    }
}
