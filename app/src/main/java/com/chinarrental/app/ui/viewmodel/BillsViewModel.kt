package com.chinarrental.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinarrental.app.data.model.Bill
import com.chinarrental.app.data.model.BillStatus
import com.chinarrental.app.data.repository.BillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BillsUiState(
    val bills: List<Bill> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedStatus: BillStatus? = null,
    val totalAmount: Double = 0.0,
    val pendingAmount: Double = 0.0
)

@HiltViewModel
class BillsViewModel @Inject constructor(
    private val billRepository: BillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BillsUiState())
    val uiState: StateFlow<BillsUiState> = _uiState.asStateFlow()

    init {
        loadBills()
    }

    fun loadBills() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val searchQuery = _uiState.value.searchQuery
                val status = _uiState.value.selectedStatus

                launch {
                    val billsFlow = when {
                        searchQuery.isNotBlank() -> billRepository.searchBills(searchQuery)
                        status != null -> billRepository.getBillsByStatus(status)
                        else -> billRepository.getAllBills()
                    }

                    billsFlow.collect { bills ->
                        _uiState.value = _uiState.value.copy(
                            bills = bills,
                            isLoading = false
                        )
                    }
                }

                launch {
                    billRepository.getTotalBillsAmount().collect { total ->
                        _uiState.value = _uiState.value.copy(totalAmount = total ?: 0.0)
                    }
                }

                launch {
                    billRepository.getPendingBillsAmount().collect { pending ->
                        _uiState.value = _uiState.value.copy(pendingAmount = pending ?: 0.0)
                    }
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load bills"
                )
            }
        }
    }

    fun searchBills(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadBills()
    }

    fun filterByStatus(status: BillStatus?) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
        loadBills()
    }

    fun deleteBill(bill: Bill) {
        viewModelScope.launch {
            billRepository.deleteBill(bill)
            loadBills()
        }
    }
}
