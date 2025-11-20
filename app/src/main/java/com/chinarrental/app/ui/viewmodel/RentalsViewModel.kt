package com.chinarrental.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinarrental.app.data.model.Rental
import com.chinarrental.app.data.model.RentalStatus
import com.chinarrental.app.data.repository.RentalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RentalsUiState(
    val rentals: List<Rental> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedStatus: RentalStatus? = null
)

@HiltViewModel
class RentalsViewModel @Inject constructor(
    private val rentalRepository: RentalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RentalsUiState())
    val uiState: StateFlow<RentalsUiState> = _uiState.asStateFlow()

    init {
        loadRentals()
    }

    fun loadRentals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val status = _uiState.value.selectedStatus

                val rentalsFlow = if (status != null) {
                    rentalRepository.getRentalsByStatus(status)
                } else {
                    rentalRepository.getAllRentals()
                }

                rentalsFlow.collect { rentals ->
                    _uiState.value = _uiState.value.copy(
                        rentals = rentals,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load rentals"
                )
            }
        }
    }

    fun filterByStatus(status: RentalStatus?) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
        loadRentals()
    }

    fun returnRental(rentalId: Long, returnDate: Long, damageCharges: Double = 0.0) {
        viewModelScope.launch {
            val result = rentalRepository.returnRental(rentalId, returnDate, damageCharges)
            result.onSuccess {
                loadRentals()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteRental(rental: Rental) {
        viewModelScope.launch {
            rentalRepository.deleteRental(rental)
            loadRentals()
        }
    }
}
