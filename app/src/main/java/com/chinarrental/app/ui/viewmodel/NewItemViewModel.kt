package com.chinarrental.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinarrental.app.data.model.Item
import com.chinarrental.app.data.model.ItemCategory
import com.chinarrental.app.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewItemUiState(
    val name: String = "",
    val description: String = "",
    val category: ItemCategory = ItemCategory.TENT,
    val rentPerDay: String = "",
    val totalQuantity: String = "",
    val availableQuantity: String = "",
    val imageUri: String? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class NewItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewItemUiState())
    val uiState: StateFlow<NewItemUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, error = null)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateCategory(category: ItemCategory) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateRentPerDay(rent: String) {
        _uiState.value = _uiState.value.copy(rentPerDay = rent, error = null)
    }

    fun updateTotalQuantity(quantity: String) {
        val availableQty = _uiState.value.availableQuantity.toIntOrNull() ?: 0
        val totalQty = quantity.toIntOrNull() ?: 0

        _uiState.value = _uiState.value.copy(
            totalQuantity = quantity,
            availableQuantity = if (availableQty > totalQty) totalQty.toString() else _uiState.value.availableQuantity,
            error = null
        )
    }

    fun updateAvailableQuantity(quantity: String) {
        _uiState.value = _uiState.value.copy(availableQuantity = quantity, error = null)
    }

    fun updateImageUri(uri: String?) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
    }

    fun saveItem() {
        val state = _uiState.value

        // Validation
        if (state.name.isBlank()) {
            _uiState.value = state.copy(error = "Item name is required")
            return
        }

        val rentPerDay = state.rentPerDay.toDoubleOrNull()
        if (rentPerDay == null || rentPerDay <= 0) {
            _uiState.value = state.copy(error = "Valid rent per day is required")
            return
        }

        val totalQuantity = state.totalQuantity.toIntOrNull()
        if (totalQuantity == null || totalQuantity <= 0) {
            _uiState.value = state.copy(error = "Valid total quantity is required")
            return
        }

        val availableQuantity = state.availableQuantity.toIntOrNull() ?: totalQuantity
        if (availableQuantity > totalQuantity) {
            _uiState.value = state.copy(error = "Available quantity cannot exceed total quantity")
            return
        }

        _uiState.value = state.copy(isSaving = true, error = null)

        viewModelScope.launch {
            val item = Item(
                name = state.name,
                description = state.description,
                category = state.category,
                rentPerDay = rentPerDay,
                totalQuantity = totalQuantity,
                availableQuantity = availableQuantity,
                imageUri = state.imageUri
            )

            val result = itemRepository.insertItem(item)

            result.onSuccess {
                _uiState.value = NewItemUiState(saveSuccess = true)
            }.onFailure { e ->
                _uiState.value = state.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save item"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = NewItemUiState()
    }
}
