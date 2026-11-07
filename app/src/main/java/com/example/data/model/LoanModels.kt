package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoanDto(
    @Json(name = "id") val id: Long,
    @Json(name = "amount") val amount: Double,
    @Json(name = "initialAmount") val initialAmount: Double = 0.0,
    @Json(name = "principal_amount") val principalAmount: Double? = null,
    @Json(name = "total_repayment") val totalRepayment: Double? = null,
    @Json(name = "repaid_amount") val repaidAmount: Double = 0.0,
    @Json(name = "remaining_balance") val remainingBalance: Double,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "status") val status: String? = null,
    @Json(name = "start_date") val startDate: String? = null,
    @Json(name = "end_date") val endDate: String? = null,
    @Json(name = "isFinished") val isFinished: Boolean = false,
    @Json(name = "schedule") val schedule: List<LoanInstallmentDto> = emptyList(),
    @Json(name = "installments") val installments: List<LoanInstallmentDto> = emptyList(),
    @Json(name = "repayments") val repayments: List<LoanRepaymentDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class LoanInstallmentDto(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "installment_number") val number: Int = 1,
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "status") val status: String? = "PENDING",
    @Json(name = "due_date") val dueDate: String? = null,
    @Json(name = "penalty") val penalty: Double = 0.0,
    @Json(name = "paid_amount") val paidAmount: Double = 0.0,
    @Json(name = "remaining_amount") val remainingAmount: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class LoanRepaymentDto(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "payment_date") val date: String? = null,
    @Json(name = "type") val type: String = "repayment",
    @Json(name = "debt_remaining") val debtRemaining: Double? = null
)

@JsonClass(generateAdapter = true)
data class LoanOfferDto(
    @Json(name = "eligible") val eligible: Boolean = false,
    @Json(name = "reason") val reason: String? = null,
    @Json(name = "interestRate") val interestRate: Double? = 0.0,
    @Json(name = "guaranteeRate") val guaranteeRate: Double? = 0.0,
    @Json(name = "balance") val balance: Double = 0.0,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "durations") val durations: List<Int> = emptyList(),
    @Json(name="interestRates") val interestRates: Map<String,Double> = emptyMap()
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
    @Json(name = "installmentCount") val installmentCount: Int,
    @Json(name="acceptTerms") val acceptTerms: Boolean = true,
    @Json(name="acceptedTerms") val acceptedTerms: LoanApplicationQuote? = null
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
    @Json(name = "amount") val amount: Double,
    @Json(name="acceptedPayment") val acceptedPayment: LoanPaymentQuote? = null
)

@JsonClass(generateAdapter = true)
data class LoanRepayResponse(
    @Json(name="currency") val currency:String?=null,
    @Json(name="fee") val fee:Double?=null,
    @Json(name="reference") val reference:String?=null,
    @Json(name = "paid") val paid: Double? = null,
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "debtBefore") val debtBefore: Double? = null,
    @Json(name = "remaining") val debtAfter: Double? = null,
    @Json(name = "remainingBalance") val remainingBalance: Double? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanRepayInstallmentRequest(
    @Json(name = "loanId") val loanId: Long,
    @Json(name = "installmentId") val installmentId: Long,
    @Json(name="acceptedPayment") val acceptedPayment: LoanPaymentQuote? = null
)

@JsonClass(generateAdapter = true)
data class LoanRepayInstallmentResponse(
    @Json(name="paid") val paid:Double?=null,
    @Json(name="remaining") val remaining:Double?=null,
    @Json(name="currency") val currency:String?=null,
    @Json(name="fee") val fee:Double?=null,
    @Json(name="reference") val reference:String?=null,
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

@JsonClass(generateAdapter=true)
data class LoanApplicationQuote(val principal:Double,val interestRate:Double,val totalRepayment:Double,val repaymentFrequency:String,val installmentCount:Int,val installmentAmount:Double,val lastInstallmentAmount:Double,val guaranteeAmount:Double,val currency:String,val durationMonths:Int,val penaltyIntervalDays:Int,val penaltyExample:Double)
@JsonClass(generateAdapter=true)
data class LoanApplicationQuoteResponse(val success:Boolean,val quote:LoanApplicationQuote?=null,val error:String?=null)
@JsonClass(generateAdapter=true)
data class LoanPaymentQuote(val loanId:Long,val installmentId:Long?=null,val amount:Double,val currency:String,val fee:Double,val totalDebit:Double,val remaining:Double,val quoteToken:String?=null)
@JsonClass(generateAdapter=true)
data class LoanPaymentQuoteResponse(val success:Boolean,val quote:LoanPaymentQuote?=null,val error:String?=null)
