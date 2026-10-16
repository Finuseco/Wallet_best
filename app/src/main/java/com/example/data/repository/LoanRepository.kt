package com.example.data.repository

import com.example.data.remote.ApiClient
import com.example.models.LoanOffer
import com.example.models.LoanRequest
import com.example.models.LoanHistory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface LoanService {
    @GET("api/v1/loans/offer")
    suspend fun getOffer(@Query("currency") currency: String): LoanOffer

    @POST("api/v1/loans/request")
    suspend fun requestLoan(@Body request: LoanRequest): Map<String, Any>

    @GET("api/v1/loans/history")
    suspend fun getHistory(@Query("currency") currency: String): LoanHistory

    @POST("api/v1/loans/repay")
    suspend fun repayLoan(@Body body: Map<String, Any>): Map<String, Any>

    @POST("api/v1/loans/repay-installment")
    suspend fun repayInstallment(@Body body: Map<String, Any>): Map<String, Any>
}

class LoanRepository {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val service = Retrofit.Builder()
        .baseUrl("https://app.cashpay-all.com/")
        .client(ApiClient.okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(LoanService::class.java)

    suspend fun getOffer(currency: String) = service.getOffer(currency)
    suspend fun requestLoan(request: LoanRequest) = service.requestLoan(request)
    suspend fun getHistory(currency: String) = service.getHistory(currency)
    suspend fun repayLoan(loanId: Int, amount: Double) = service.repayLoan(mapOf("loanId" to loanId, "amount" to amount))
    suspend fun repayInstallment(loanId: Int, installmentId: Int) = service.repayInstallment(mapOf("loanId" to loanId, "installmentId" to installmentId))
}
