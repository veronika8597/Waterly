package com.example.waterly.settings.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterly.ReminderScheduler
import com.example.waterly.WaterDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    fun initialize(context: Context) {
        viewModelScope.launch {
            val goal = WaterDataStore.loadDailyGoal(context)
            val enabled = WaterDataStore.loadReminderEnabled(context)
            val interval = WaterDataStore.loadReminderInterval(context)
            val (start, end) = WaterDataStore.loadQuietHours(context)

            _uiState.value = _uiState.value.copy(
                currentGoal = goal,
                goalInput = goal.toString(),
                notificationsEnabled = enabled,
                selectedInterval = interval,
                quietStart = String.format("%02d:00", start),
                quietEnd = String.format("%02d:00", end),
                isInitialized = true
            )
        }
    }

    fun onGoalChanged(newValue: String) {
        if (newValue.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(goalInput = newValue)
        }
    }

    fun saveGoal(context: Context) {
        val parsed = _uiState.value.goalInput.toIntOrNull()
        if (parsed != null && parsed in 500..5000) {
            WaterDataStore.saveDailyGoal(context, parsed)
            _uiState.value = _uiState.value.copy(currentGoal = parsed)
            showGoalSavedPopup()
        }
    }

    fun dismissGoalSavedDialog(){
        _uiState.value = _uiState.value.copy(showGoalSavedDialog = false)
    }

    fun toggleNotifications(context: Context, enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        WaterDataStore.saveReminderEnabled(context, enabled)

        if (enabled) ReminderScheduler.scheduleRepeatingReminder(context, _uiState.value.selectedInterval)
        else ReminderScheduler.cancelReminder(context)
    }

    fun setReminderInterval(context: Context, hours: Int) {
        _uiState.value = _uiState.value.copy(selectedInterval = hours)
        WaterDataStore.saveReminderInterval(context, hours)
    }

    fun setQuietStart(context: Context, hour: String) {
        _uiState.value = _uiState.value.copy(quietStart = hour)
        WaterDataStore.saveQuietHours(context, hour.take(2).toInt(), _uiState.value.quietEnd.take(2).toInt())
    }

    fun setQuietEnd(context: Context, hour: String) {
        _uiState.value = _uiState.value.copy(quietEnd = hour)
        WaterDataStore.saveQuietHours(context, _uiState.value.quietStart.take(2).toInt(), hour.take(2).toInt())
    }

    fun toggleStartDropdown() {
        _uiState.value = _uiState.value.copy(
            expandedStart = !_uiState.value.expandedStart
        )
    }

    fun toggleEndDropdown() {
        _uiState.value = _uiState.value.copy(
            expandedEnd = !_uiState.value.expandedEnd
        )
    }

    fun setWipeConfirm(show: Boolean) {
        _uiState.value = _uiState.value.copy(showWipeConfirm = show)
    }


    fun toggleWipeDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showWipeConfirm = show)
    }

    fun wipeAllData(context: Context) {
        WaterDataStore.clearAllData(context)
        toggleWipeDialog(false)
    }

    fun showGoalSavedPopup() {
        _uiState.value = _uiState.value.copy(showGoalSavedDialog = true)
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(showGoalSavedDialog = false)
        }
    }

}
