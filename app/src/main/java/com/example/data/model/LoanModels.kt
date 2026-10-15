package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoanDto(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "initialAmount") val initialAmount: Double = 0.0,
    @Json(name = "principalAmount") val principalAmount: Double? = null,
    @Json(name = "totalRepayment") val totalRepayment: Double? = null,
    @Json(name = "repaidAmount") val repaidAmount: Double = 0.0,
    @Json(name = "remainingBalance") val remainingBalance: Double = 0.0,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "status") val status: String? = "APPROVED",
    @Json(name = "startDate") val startDate: String? = null,
    @Json(name = "endDate") val endDate: String? = null,
    @Json(name = "isFinished") val isFinished: Boolean = false,
    @Json(name = "schedule") val schedule: List<LoanInstallmentDto> = emptyList(),
    @Json(name = "installments") val installments: List<LoanInstallmentDto> = emptyList(),
    @Json(name = "repayments") val repayments: List<LoanRepaymentDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class LoanInstallmentDto(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "number") val number: Int = 1,
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "status") val status: String? = "PENDING",
    @Json(name = "dueDate") val dueDate: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanRepaymentDto(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "date") val date: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanOfferDto(
    @Json(name = "eligible") val eligible: Boolean = true,
    @Json(name = "reason") val reason: String? = null,
    @Json(name = "interestRate") val interestRate: Double? = 0.0,
    @Json(name = "guaranteeRate") val guaranteeRate: Double? = 0.0,
    @Json(name = "balance") val balance: Double = 0.0,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "durations") val durations: List<Int> = listOf(1, 3, 6, 12)
)

@JsonClass(generateAdapter = true)
data class LoanOfferResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "offer") val offer: LoanOfferDto? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanCurrentResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "loan") val loan: LoanDto? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanHistoryResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "loans") val loans: List<LoanDto> = emptyList(),
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanRequest(
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String,
    @Json(name = "durationMonths") val durationMonths: Int,
    @Json(name = "repaymentFrequency") val repaymentFrequency: String = "monthly",
    @Json(name = "installmentCount") val installmentCount: Int
)

@JsonClass(generateAdapter = true)
data class LoanRequestResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "loan") val loan: LoanDto? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanRepayRequest(
    @Json(name = "loanId") val loanId: Long,
    @Json(name = "amount") val amount: Double
)

@JsonClass(generateAdapter = true)
data class LoanRepayResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "debtBefore") val debtBefore: Double? = null,
    @Json(name = "debtAfter") val debtAfter: Double? = null,
    @Json(name = "remainingBalance") val remainingBalance: Double? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanRepayInstallmentRequest(
    @Json(name = "loanId") val loanId: Long,
    @Json(name = "installmentId") val installmentId: Long
)

@JsonClass(generateAdapter = true)
data class LoanRepayInstallmentResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

// Aliases for compatibility
typealias Loan = LoanDto
typealias Installment = LoanInstallmentDto
typealias Repayment = LoanRepaymentDto
typealias RepayRequest = LoanRepayRequest
typealias RepayInstallmentRequest = LoanRepayInstallmentRequest

@JsonClass(generateAdapter = true)
data class ClientInfo(
    @Json(name = "userId") val userId: String = "",
    @Json(name = "walletId") val walletId: String = "",
    @Json(name = "fullName") val fullName: String = "",
    @Json(name = "avatar") val avatar: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanInfo(
    @Json(name = "id") val id: String = "",
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "remainingBalance") val remainingBalance: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class AgentRepaymentRequest(
    @Json(name = "action") val action: String,
    @Json(name = "operationId") val operationId: String? = null,
    @Json(name = "clientRef") val clientRef: String? = null,
    @Json(name = "clientWalletId") val clientWalletId: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "pin") val pin: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentRepaymentResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "operationId") val operationId: String? = null,
    @Json(name = "step") val step: String? = null,
    @Json(name = "client") val client: ClientInfo? = null,
    @Json(name = "loan") val loan: LoanInfo? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "debtBefore") val debtBefore: Double? = null,
    @Json(name = "debtAfter") val debtAfter: Double? = null,
    @Json(name = "debtRemaining") val debtRemaining: Double? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "error") val error: String? = null
)
