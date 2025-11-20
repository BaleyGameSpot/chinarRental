package com.chinarrental.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinarrental.app.data.model.Transaction
import com.chinarrental.app.data.model.TransactionCategory
import com.chinarrental.app.data.model.TransactionType
import com.chinarrental.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoznamchaUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val todayIncome: Double = 0.0,
    val todayExpense: Double = 0.0,
    val selectedType: TransactionType? = null,
    val showAddDialog: Boolean = false,
    // Add transaction form fields
    val newAmount: String = "",
    val newDescription: String = "",
    val newCategory: TransactionCategory = TransactionCategory.RENTAL_INCOME,
    val newType: TransactionType = TransactionType.INCOME
)

@HiltViewModel
class RoznamchaViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoznamchaUiState())
    val uiState: StateFlow<RoznamchaUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val selectedType = _uiState.value.selectedType

                launch {
                    val transactionsFlow = if (selectedType != null) {
                        transactionRepository.getTransactionsByType(selectedType)
                    } else {
                        transactionRepository.getAllTransactions()
                    }

                    transactionsFlow.collect { transactions ->
                        _uiState.value = _uiState.value.copy(
                            transactions = transactions,
                            isLoading = false
                        )
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
                    transactionRepository.getTodayIncome().collect { income ->
                        _uiState.value = _uiState.value.copy(todayIncome = income ?: 0.0)
                    }
                }

                launch {
                    transactionRepository.getTodayExpense().collect { expense ->
                        _uiState.value = _uiState.value.copy(todayExpense = expense ?: 0.0)
                    }
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load transactions"
                )
            }
        }
    }

    fun filterByType(type: TransactionType?) {
        _uiState.value = _uiState.value.copy(selectedType = type)
        loadTransactions()
    }

    fun showAddDialog(show: Boolean = true) {
        _uiState.value = _uiState.value.copy(
            showAddDialog = show,
            newAmount = "",
            newDescription = "",
            error = null
        )
    }

    fun updateNewAmount(amount: String) {
        _uiState.value = _uiState.value.copy(newAmount = amount, error = null)
    }

    fun updateNewDescription(description: String) {
        _uiState.value = _uiState.value.copy(newDescription = description)
    }

    fun updateNewCategory(category: TransactionCategory) {
        _uiState.value = _uiState.value.copy(newCategory = category)
    }

    fun updateNewType(type: TransactionType) {
        _uiState.value = _uiState.value.copy(newType = type)
    }

    fun addTransaction() {
        val state = _uiState.value

        val amount = state.newAmount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = state.copy(error = "Valid amount is required")
            return
        }

        if (state.newDescription.isBlank()) {
            _uiState.value = state.copy(error = "Description is required")
            return
        }

        viewModelScope.launch {
            val transaction = Transaction(
                type = state.newType,
                category = state.newCategory,
                amount = amount,
                description = state.newDescription,
                date = System.currentTimeMillis()
            )

            val result = transactionRepository.insertTransaction(transaction)

            result.onSuccess {
                _uiState.value = state.copy(showAddDialog = false)
                loadTransactions()
            }.onFailure { e ->
                _uiState.value = state.copy(error = e.message ?: "Failed to add transaction")
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
            loadTransactions()
        }
    }
}
