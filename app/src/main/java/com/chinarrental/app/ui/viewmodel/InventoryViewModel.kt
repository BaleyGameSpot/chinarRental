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

data class InventoryUiState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: ItemCategory? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val searchQuery = _uiState.value.searchQuery
                val category = _uiState.value.selectedCategory

                val itemsFlow = when {
                    searchQuery.isNotBlank() -> itemRepository.searchItems(searchQuery)
                    category != null -> itemRepository.getItemsByCategory(category)
                    else -> itemRepository.getAllItems()
                }

                itemsFlow.collect { items ->
                    _uiState.value = _uiState.value.copy(
                        items = items,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load items"
                )
            }
        }
    }

    fun filterByCategory(category: ItemCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        loadItems()
    }

    fun searchItems(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadItems()
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemRepository.deleteItem(item)
            loadItems()
        }
    }
}
