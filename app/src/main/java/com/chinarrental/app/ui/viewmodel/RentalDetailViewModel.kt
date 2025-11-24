package com.chinarrental.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinarrental.app.data.model.*
import com.chinarrental.app.data.repository.RentalRepository
import com.chinarrental.app.data.dao.CustomerDao
import com.chinarrental.app.data.dao.ItemDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RentalDetailUiState(
    val rental: Rental? = null,
    val customer: Customer? = null,
    val item: Item? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showReturnDialog: Boolean = false,
    val showPaymentDialog: Boolean = false,
    val returnSuccess: Boolean = false,
    val paymentSuccess: Boolean = false
)

@HiltViewModel
class RentalDetailViewModel @Inject constructor(
    private val rentalRepository: RentalRepository,
    private val customerDao: CustomerDao,
    private val itemDao: ItemDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(RentalDetailUiState())
    val uiState: StateFlow<RentalDetailUiState> = _uiState.asStateFlow()

    fun loadRentalDetails(rentalId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                rentalRepository.getRentalById(rentalId).collect { rental ->
                    if (rental != null) {
                        // Load customer details
                        customerDao.getCustomerById(rental.customerId).collect { customer ->
                            // Load item details
                            itemDao.getItemById(rental.itemId).collect { item ->
                                _uiState.update {
                                    it.copy(
                                        rental = rental,
                                        customer = customer,
                                        item = item,
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Rental not found"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load rental details"
                    )
                }
            }
        }
    }

    fun showReturnDialog() {
        _uiState.update { it.copy(showReturnDialog = true) }
    }

    fun hideReturnDialog() {
        _uiState.update { it.copy(showReturnDialog = false) }
    }

    fun showPaymentDialog() {
        _uiState.update { it.copy(showPaymentDialog = true) }
    }

    fun hidePaymentDialog() {
        _uiState.update { it.copy(showPaymentDialog = false) }
    }

    fun returnRental(damageCharges: Double = 0.0) {
        viewModelScope.launch {
            val rental = _uiState.value.rental ?: return@launch
            val result = rentalRepository.returnRental(
                rentalId = rental.id,
                returnDate = System.currentTimeMillis(),
                damageCharges = damageCharges
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        showReturnDialog = false,
                        returnSuccess = true,
                        showPaymentDialog = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        showReturnDialog = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to return rental"
                    )
                }
            }
        }
    }

    fun addPayment(amount: Double) {
        viewModelScope.launch {
            val rental = _uiState.value.rental ?: return@launch
            val result = rentalRepository.addPayment(rental.id, amount)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        showPaymentDialog = false,
                        paymentSuccess = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        showPaymentDialog = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to add payment"
                    )
                }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update {
            it.copy(
                returnSuccess = false,
                paymentSuccess = false
            )
        }
    }
}
