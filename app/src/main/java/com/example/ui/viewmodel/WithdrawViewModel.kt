package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.WithdrawActionRequest
import com.example.data.model.WithdrawActionResponse
import com.example.data.repository.WithdrawRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WithdrawViewModel(private val repository: WithdrawRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<WithdrawUiState>(WithdrawUiState.Idle)
    val uiState: StateFlow<WithdrawUiState> = _uiState
    
    private var currentOperationId: String? = null

    fun startWithdraw(operation: String, agentWalletId: String?, clientWalletId: String?) {
        viewModelScope.launch {
            _uiState.value = WithdrawUiState.Loading
            val request = WithdrawActionRequest(
                operation = operation,
                action = "start",
                agentWalletId = agentWalletId,
                clientWalletId = clientWalletId
            )
            handleResponse(repository.withdrawAction(request))
        }
    }

    fun proceedAction(action: String, extraParams: Map<String, String> = emptyMap()) {
        viewModelScope.launch {
            _uiState.value = WithdrawUiState.Loading
            
            val request = WithdrawActionRequest(
                operation = _lastOperationType ?: "",
                action = action,
                operationId = currentOperationId,
                currency = extraParams["currency"],
                amount = extraParams["amount"]?.toDoubleOrNull(),
                pin = extraParams["pin"],
                channel = extraParams["channel"],
                otp = extraParams["otp"]
            )
            handleResponse(repository.withdrawAction(request))
        }
    }

    private var _lastOperationType: String? = null

    private fun handleResponse(result: Result<WithdrawActionResponse>) {
        result.onSuccess { response ->
            if (response.success) {
                currentOperationId = response.operationId
                if (_lastOperationType == null) _lastOperationType = response.operation
                _uiState.value = WithdrawUiState.Success(response)
            } else {
                _uiState.value = WithdrawUiState.Error(response.error ?: "Une erreur est survenue")
            }
        }.onFailure { e ->
            _uiState.value = WithdrawUiState.Error(e.message ?: "Erreur réseau")
        }
    }
}

sealed class WithdrawUiState {
    object Idle : WithdrawUiState()
    object Loading : WithdrawUiState()
    data class Success(val response: WithdrawActionResponse) : WithdrawUiState()
    data class Error(val message: String) : WithdrawUiState()
}
