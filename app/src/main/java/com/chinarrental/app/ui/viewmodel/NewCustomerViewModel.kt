package com.chinarrental.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinarrental.app.data.model.Customer
import com.chinarrental.app.data.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewCustomerUiState(
    val name: String = "",
    val phone: String = "",
    val cnic: String = "",
    val address: String = "",
    val location: String? = null,
    val cnicFrontImageUri: String? = null,
    val cnicBackImageUri: String? = null,
    val discount: String = "0",
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class NewCustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewCustomerUiState())
    val uiState: StateFlow<NewCustomerUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, error = null)
    }

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone, error = null)
    }

    fun updateCnic(cnic: String) {
        _uiState.value = _uiState.value.copy(cnic = cnic, error = null)
    }

    fun updateAddress(address: String) {
        _uiState.value = _uiState.value.copy(address = address)
    }

    fun updateLocation(location: String?) {
        _uiState.value = _uiState.value.copy(location = location)
    }

    fun updateCnicFrontImage(uri: String?) {
        _uiState.value = _uiState.value.copy(cnicFrontImageUri = uri)
    }

    fun updateCnicBackImage(uri: String?) {
        _uiState.value = _uiState.value.copy(cnicBackImageUri = uri)
    }

    fun updateDiscount(discount: String) {
        _uiState.value = _uiState.value.copy(discount = discount)
    }

    fun saveCustomer() {
        val state = _uiState.value

        // Validation
        if (state.name.isBlank()) {
            _uiState.value = state.copy(error = "Customer name is required")
            return
        }

        if (state.phone.isBlank()) {
            _uiState.value = state.copy(error = "Phone number is required")
            return
        }

        if (state.cnic.isBlank()) {
            _uiState.value = state.copy(error = "CNIC is required")
            return
        }

        val discount = state.discount.toDoubleOrNull() ?: 0.0
        if (discount < 0 || discount > 100) {
            _uiState.value = state.copy(error = "Discount must be between 0 and 100")
            return
        }

        _uiState.value = state.copy(isSaving = true, error = null)

        viewModelScope.launch {
            val customer = Customer(
                name = state.name,
                phone = state.phone,
                cnic = state.cnic,
                address = state.address,
                location = state.location,
                cnicFrontImageUri = state.cnicFrontImageUri,
                cnicBackImageUri = state.cnicBackImageUri,
                discount = discount
            )

            val result = customerRepository.insertCustomer(customer)

            result.onSuccess {
                _uiState.value = NewCustomerUiState(saveSuccess = true)
            }.onFailure { e ->
                _uiState.value = state.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save customer"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = NewCustomerUiState()
    }
}
