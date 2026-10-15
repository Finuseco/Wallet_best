package com.example.data.repository

import com.example.data.model.WithdrawActionRequest
import com.example.data.model.WithdrawActionResponse
import com.example.data.remote.CashPayApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WithdrawRepository(
    private val apiService: CashPayApiService
) {
    suspend fun withdrawAction(request: WithdrawActionRequest): Result<WithdrawActionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.withdrawAction(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Une erreur est survenue lors de l'opération."
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
