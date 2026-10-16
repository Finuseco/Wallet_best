package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.LoanRepository
import com.example.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoanUiState {
    object Idle : LoanUiState()
    object Loading : LoanUiState()
    data class OfferLoaded(val offer: LoanOffer) : LoanUiState()
    data class HistoryLoaded(val history: LoanHistory) : LoanUiState()
    data class Error(val message: String) : LoanUiState()
}

class LoanViewModel(private val repository: LoanRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<LoanUiState>(LoanUiState.Idle)
    val uiState: StateFlow<LoanUiState> = _uiState

    fun fetchOffer(currency: String) {
        viewModelScope.launch {
            _uiState.value = LoanUiState.Loading
            try {
                val offer = repository.getOffer(currency)
                _uiState.value = LoanUiState.OfferLoaded(offer)
            } catch (e: Exception) {
                _uiState.value = LoanUiState.Error(e.message ?: "Erreur lors de la récupération de l'offre")
            }
        }
    }

    fun fetchHistory(currency: String) {
        viewModelScope.launch {
            _uiState.value = LoanUiState.Loading
            try {
                val history = repository.getHistory(currency)
                _uiState.value = LoanUiState.HistoryLoaded(history)
            } catch (e: Exception) {
                _uiState.value = LoanUiState.Error(e.message ?: "Erreur lors de la récupération de l'historique")
            }
        }
    }
}
