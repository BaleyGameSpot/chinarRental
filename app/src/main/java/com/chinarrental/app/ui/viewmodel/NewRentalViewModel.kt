package com.chinarrental.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinarrental.app.data.model.*
import com.chinarrental.app.data.repository.CustomerRepository
import com.chinarrental.app.data.repository.ItemRepository
import com.chinarrental.app.data.repository.RentalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewRentalUiState(
    val customers: List<Customer> = emptyList(),
    val items: List<Item> = emptyList(),
    val selectedCustomer: Customer? = null,
    val selectedItem: Item? = null,
    val quantity: String = "1",
    val startDate: Long = System.currentTimeMillis(),
    val expectedReturnDate: Long = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000), // 7 days from now
    val advanceAmount: String = "0",
    val notes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class NewRentalViewModel @Inject constructor(
    private val rentalRepository: RentalRepository,
    private val customerRepository: CustomerRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewRentalUiState())
    val uiState: StateFlow<NewRentalUiState> = _uiState.asStateFlow()

    init {
        loadCustomersAndItems()
    }

    private fun loadCustomersAndItems() {
        viewModelScope.launch {
            launch {
                customerRepository.getAllCustomers().collect { customers ->
                    _uiState.value = _uiState.value.copy(customers = customers)
                }
            }
            launch {
                itemRepository.getAvailableItems().collect { items ->
                    _uiState.value = _uiState.value.copy(items = items)
                }
            }
        }
    }

    fun selectCustomer(customer: Customer) {
        _uiState.value = _uiState.value.copy(selectedCustomer = customer, error = null)
    }

    fun selectItem(item: Item) {
        _uiState.value = _uiState.value.copy(selectedItem = item, error = null)
    }

    fun updateQuantity(quantity: String) {
        _uiState.value = _uiState.value.copy(quantity = quantity, error = null)
    }

    fun updateStartDate(date: Long) {
        _uiState.value = _uiState.value.copy(startDate = date)
    }

    fun updateExpectedReturnDate(date: Long) {
        _uiState.value = _uiState.value.copy(expectedReturnDate = date)
    }

    fun updateAdvanceAmount(amount: String) {
        _uiState.value = _uiState.value.copy(advanceAmount = amount)
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun saveRental() {
        val state = _uiState.value

        // Validation
        if (state.selectedCustomer == null) {
            _uiState.value = state.copy(error = "Please select a customer")
            return
        }

        if (state.selectedItem == null) {
            _uiState.value = state.copy(error = "Please select an item")
            return
        }

        val quantity = state.quantity.toIntOrNull()
        if (quantity == null || quantity <= 0) {
            _uiState.value = state.copy(error = "Valid quantity is required")
            return
        }

        if (quantity > state.selectedItem.availableQuantity) {
            _uiState.value = state.copy(error = "Quantity exceeds available stock (${state.selectedItem.availableQuantity})")
            return
        }

        val advanceAmount = state.advanceAmount.toDoubleOrNull() ?: 0.0
        if (advanceAmount < 0) {
            _uiState.value = state.copy(error = "Advance amount cannot be negative")
            return
        }

        _uiState.value = state.copy(isSaving = true, error = null)

        viewModelScope.launch {
            val rental = Rental(
                customerId = state.selectedCustomer.id,
                itemId = state.selectedItem.id,
                quantity = quantity,
                rentPerDay = state.selectedItem.rentPerDay,
                startDate = state.startDate,
                expectedReturnDate = state.expectedReturnDate,
                advanceAmount = advanceAmount,
                paidAmount = advanceAmount,
                notes = state.notes,
                status = RentalStatus.ACTIVE
            )

            val result = rentalRepository.createRental(rental)

            result.onSuccess {
                _uiState.value = NewRentalUiState(saveSuccess = true)
            }.onFailure { e ->
                _uiState.value = state.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to create rental"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = NewRentalUiState()
        loadCustomersAndItems()
    }
}
