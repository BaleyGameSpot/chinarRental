package com.chinarrental.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinarrental.app.data.model.RentalStatus
import com.chinarrental.app.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val activeRentals: Int = 0,
    val totalCustomers: Int = 0,
    val totalItems: Int = 0,
    val todayIncome: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val overdueRentals: Int = 0,
    val pendingPayments: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val rentalRepository: RentalRepository,
    private val customerRepository: CustomerRepository,
    private val itemRepository: ItemRepository,
    private val transactionRepository: TransactionRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Load all dashboard metrics in parallel
                launch {
                    rentalRepository.getRentalsByStatus(RentalStatus.ACTIVE).collect { rentals ->
                        _uiState.value = _uiState.value.copy(activeRentals = rentals.size)
                    }
                }

                launch {
                    customerRepository.getTotalCustomersCount().collect { count ->
                        _uiState.value = _uiState.value.copy(totalCustomers = count)
                    }
                }

                launch {
                    itemRepository.getTotalItemsCount().collect { count ->
                        _uiState.value = _uiState.value.copy(totalItems = count)
                    }
                }

                launch {
                    paymentRepository.getTodayTotalAmount().collect { amount ->
                        _uiState.value = _uiState.value.copy(todayIncome = amount ?: 0.0)
                    }
                }

                launch {
                    transactionRepository.getTotalIncome().collect { income ->
                        _uiState.value = _uiState.value.copy(totalIncome = income ?: 0.0)
                    }
                }

                launch {
                    transactionRepository.getTotalExpense().collect { expense ->
                        _uiState.value = _uiState.value.copy(totalExpense = expense ?: 0.0)
                    }
                }

                launch {
                    transactionRepository.getBalance().collect { balance ->
                        _uiState.value = _uiState.value.copy(balance = balance ?: 0.0)
                    }
                }

                launch {
                    rentalRepository.getOverdueRentals().collect { rentals ->
                        _uiState.value = _uiState.value.copy(overdueRentals = rentals.size)
                    }
                }

                launch {
                    rentalRepository.getRentalsWithPendingPayment().collect { rentals ->
                        _uiState.value = _uiState.value.copy(pendingPayments = rentals.size)
                    }
                }

                _uiState.value = _uiState.value.copy(isLoading = false)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard data"
                )
            }
        }
    }

    fun refresh() {
        loadDashboardData()
    }
}
