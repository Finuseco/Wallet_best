package com.example

import com.example.data.model.CountryDto
import com.example.ui.viewmodel.AuthUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CashPayUnitTest {

    @Test
    fun testCountryFlagEmojiGeneration() {
        val rdc = CountryDto(code = "CD", name = "RDC", dialCode = "+243")
        val france = CountryDto(code = "FR", name = "France", dialCode = "+33")
        val usa = CountryDto(code = "US", name = "USA", dialCode = "+1")

        assertEquals("🇨🇩", rdc.flagEmoji)
        assertEquals("🇫🇷", france.flagEmoji)
        assertEquals("🇺🇸", usa.flagEmoji)
    }

    @Test
    fun testNineDigitPhoneConstraint() {
        val inputWithTenDigits = "8123456789"
        val filtered = inputWithTenDigits.filter { it.isDigit() }.take(9)

        // Strict 9-digit restriction
        assertEquals(9, filtered.length)
        assertEquals("812345678", filtered)
    }

    @Test
    fun testFullPhoneConstruction() {
        val state = AuthUiState(
            selectedCountry = CountryDto("CD", "RDC", "+243"),
            localPhone = "800001234"
        )
        assertEquals("+243800001234", state.fullPhone)
        assertTrue(state.isPhoneValid)
        assertTrue(state.isIdentificationReady)
    }

    @Test
    fun testPhoneExtractionLastNineDigits() {
        val fullPhone = "+243800001234"
        val digitsOnly = fullPhone.filter { it.isDigit() }
        val last9 = digitsOnly.takeLast(9)

        assertEquals("800001234", last9)
    }

    @Test
    fun testOtpSixDigitDetection() {
        val testOtp = "123456"
        val isComplete = testOtp.length == 6
        assertTrue(isComplete)
    }

    @Test
    fun testPinFourDigitDetection() {
        val pin = "1234"
        val isComplete = pin.length == 4
        assertTrue(isComplete)
    }
}
