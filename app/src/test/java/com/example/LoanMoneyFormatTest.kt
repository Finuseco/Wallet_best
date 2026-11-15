package com.example

import com.example.util.formatLoanMoney
import com.example.util.formatLoanPercent
import com.example.util.parseLoanAmount
import org.junit.Test
import org.junit.Assert.*

class LoanMoneyFormatTest {
    private fun spaces(value: String) = value.replace('\u202f', ' ').replace('\u00a0', ' ')
    @Test fun cdfKeepsTheActualAmountAndFractionalInstallments() {
        assertEquals("50 000 CDF", spaces(formatLoanMoney(50000.0, "CDF")))
        assertEquals("50 CDF", formatLoanMoney(50.0, "CDF"))
        assertEquals("0 CDF", formatLoanMoney(0.0, "CDF"))
        assertEquals("15,5 CDF", formatLoanMoney(15.5, "CDF"))
        assertEquals("50,00 USD", formatLoanMoney(50.0, "USD"))
        assertEquals("62,00 EUR", formatLoanMoney(62.0, "EUR"))
        assertEquals("24 %", formatLoanPercent(24.0))
    }
    @Test fun frenchInputParsesWithoutScalingOrGuessingCurrency() {
        assertEquals(50000.0, parseLoanAmount("50 000")!!, 0.0)
        assertEquals(50000.5, parseLoanAmount("50\u202f000,50")!!, 0.0)
        assertEquals(50.25, parseLoanAmount("50,25")!!, 0.0)
        for (value in listOf("NaN", "Infinity", "-5", "0", "1,2,3", "CDF 50")) assertNull(parseLoanAmount(value))
    }
}
