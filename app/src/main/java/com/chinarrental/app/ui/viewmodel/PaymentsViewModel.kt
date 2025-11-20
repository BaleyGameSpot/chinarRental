package com.chinarrental.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinarrental.app.data.model.Payment
import com.chinarrental.app.data.model.PaymentMethod
import com.chinarrental.app.data.model.PaymentType
import com.chinarrental.app.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentsUiState(
    val payments: List<Payment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalAmount: Double = 0.0,
    val todayTotal: Double = 0.0
)

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentsUiState())
    val uiState: StateFlow<PaymentsUiState> = _uiState.asStateFlow()

    init {
        loadPayments()
    }

    fun loadPayments() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                launch {
                    paymentRepository.getAllPayments().collect { payments ->
                        _uiState.value = _uiState.value.copy(
                            payments = payments,
                            isLoading = false
                        )
                    }
                }

                launch {
                    paymentRepository.getTotalPaymentsAmount().collect { total ->
                        _uiState.value = _uiState.value.copy(totalAmount = total ?: 0.0)
                    }
                }

                launch {
                    paymentRepository.getTodayTotalAmount().collect { total ->
                        _uiState.value = _uiState.value.copy(todayTotal = total ?: 0.0)
                    }
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load payments"
                )
            }
        }
    }

    fun deletePayment(payment: Payment) {
        viewModelScope.launch {
            paymentRepository.deletePayment(payment)
            loadPayments()
        }
    }
}
