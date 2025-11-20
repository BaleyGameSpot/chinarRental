package com.chinarrental.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinarrental.app.data.model.Reminder
import com.chinarrental.app.data.model.ReminderType
import com.chinarrental.app.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RemindersUiState(
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedType: ReminderType? = null,
    val showPending: Boolean = true
)

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemindersUiState())
    val uiState: StateFlow<RemindersUiState> = _uiState.asStateFlow()

    init {
        loadReminders()
    }

    fun loadReminders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val selectedType = _uiState.value.selectedType
                val showPending = _uiState.value.showPending

                val remindersFlow = when {
                    selectedType != null -> reminderRepository.getRemindersByType(selectedType)
                    showPending -> reminderRepository.getPendingReminders()
                    else -> reminderRepository.getAllReminders()
                }

                remindersFlow.collect { reminders ->
                    _uiState.value = _uiState.value.copy(
                        reminders = reminders,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load reminders"
                )
            }
        }
    }

    fun filterByType(type: ReminderType?) {
        _uiState.value = _uiState.value.copy(selectedType = type)
        loadReminders()
    }

    fun toggleShowPending(showPending: Boolean) {
        _uiState.value = _uiState.value.copy(showPending = showPending)
        loadReminders()
    }

    fun markAsCompleted(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.markAsCompleted(reminder.id)
            loadReminders()
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)
            loadReminders()
        }
    }
}
