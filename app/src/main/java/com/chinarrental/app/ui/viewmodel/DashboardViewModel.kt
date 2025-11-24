package com.chinarrental.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinarrental.app.data.model.RentalStatus
import com.chinarrental.app.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
                // Load all dashboard metrics using first() to get a snapshot
                val activeRentals = rentalRepository.getRentalsByStatus(RentalStatus.ACTIVE).first()
                val totalCustomers = customerRepository.getTotalCustomersCount().first()
                val totalItems = itemRepository.getTotalItemsCount().first()
                val todayIncome = paymentRepository.getTodayTotalAmount().first() ?: 0.0
                val totalIncome = transactionRepository.getTotalIncome().first() ?: 0.0
                val totalExpense = transactionRepository.getTotalExpense().first() ?: 0.0
                val balance = transactionRepository.getBalance().first() ?: 0.0
                val overdueRentals = rentalRepository.getOverdueRentals().first()
                val pendingPayments = rentalRepository.getRentalsWithPendingPayment().first()

                _uiState.value = _uiState.value.copy(
                    activeRentals = activeRentals.size,
                    totalCustomers = totalCustomers,
                    totalItems = totalItems,
                    todayIncome = todayIncome,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    balance = balance,
                    overdueRentals = overdueRentals.size,
                    pendingPayments = pendingPayments.size,
                    isLoading = false
                )

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
