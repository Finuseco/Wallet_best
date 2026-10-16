package com.example.models

data class LoanOffer(
    val success: Boolean,
    val allowedCurrencies: List<String>,
    val offer: OfferDetails
)

data class OfferDetails(
    val eligible: Boolean,
    val reason: String?,
    val currency: String,
    val balance: Double,
    val guaranteeRate: Double,
    val interestRate: Double,
    val durations: List<Int>
)

data class LoanRequest(
    val amount: Double,
    val currency: String,
    val durationMonths: Int,
    val repaymentFrequency: String,
    val installmentCount: Int
)

data class LoanHistory(
    val success: Boolean,
    val currency: String,
    val allowedCurrencies: List<String>,
    val loans: List<Loan>
)

data class Loan(
    val id: Int,
    val principal_amount: Double,
    val total_repayment: Double,
    val currency: String,
    val interest_rate: Double,
    val repaid_amount: Double,
    val remaining_balance: Double,
    val status: String,
    val repayments: List<Map<String, Any>>,
    val schedule: List<Map<String, Any>>
)
