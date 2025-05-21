package com.example.waterly.home.viewModel

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterly.ReminderWorker
import com.example.waterly.WaterDataStore
import com.example.waterly.getTodayDate
import com.example.waterly.isFutureDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            selectedDate = getTodayDate(),
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState

    fun loadWaterData(context: Context) {
        viewModelScope.launch {
            val history = WaterDataStore.loadWaterHistory(context)
            val goal = WaterDataStore.loadDailyGoal(context)
            val intake = history[_uiState.value.selectedDate] ?: 0
            _uiState.value = _uiState.value.copy(
                waterHistory = history,
                waterGoal = goal,
                waterIntake = intake
            )
        }
    }

    fun onDateSelected(date: String, context: Context) {
        val intake = _uiState.value.waterHistory[date] ?: 0
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            waterIntake = intake,
            selectedItem = null,
            selectedBottleSize = null
        )
        loadWaterData(context)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun onGlassClicked(context: Context) {
        if (isFutureDate(_uiState.value.selectedDate)) {
            _uiState.value = _uiState.value.copy(showFutureOverlay = true)
            return
        }

        val newIntake = _uiState.value.waterIntake + 250
        val history = _uiState.value.waterHistory.toMutableMap().apply {
            put(_uiState.value.selectedDate, newIntake)
        }

        viewModelScope.launch {
            WaterDataStore.saveWaterHistory(context, history)
        }

        if (_uiState.value.waterIntake < _uiState.value.waterGoal &&
            newIntake >= _uiState.value.waterGoal
        ) {
            ReminderWorker.sendGoalReachedNotification(context)
            onGoalReached()
        }

        _uiState.value = _uiState.value.copy(
            waterIntake = newIntake,
            waterHistory = history,
            selectedItem = "glass"
        )
    }

    fun onBottleClicked(context: Context) {
        if (isFutureDate(_uiState.value.selectedDate)) {
            _uiState.value = _uiState.value.copy(showFutureOverlay = true)
            return
        }

        _uiState.value = _uiState.value.copy(
            selectedItem = "bottle",
            showBottleDialog = true
        )
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun onBottleSelected(context: Context, size: Int) {
        val newIntake = _uiState.value.waterIntake + size
        val history = _uiState.value.waterHistory.toMutableMap().apply {
            put(_uiState.value.selectedDate, newIntake)
        }

        viewModelScope.launch {
            WaterDataStore.saveWaterHistory(context, history)
        }

        if (_uiState.value.waterIntake < _uiState.value.waterGoal &&
            newIntake >= _uiState.value.waterGoal
        ) {
            ReminderWorker.sendGoalReachedNotification(context)
            onGoalReached()
        }

        _uiState.value = _uiState.value.copy(
            selectedBottleSize = size,
            selectedItem = "bottle",
            waterIntake = newIntake,
            waterHistory = history,
            showBottleDialog = false
        )
    }

    fun onBottleDialogDismiss() {
        _uiState.value = _uiState.value.copy(showBottleDialog = false)
    }

    fun onGoalReached(){
        _uiState.value = _uiState.value.copy(showConfetti = true)
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            _uiState.value = _uiState.value.copy(showConfetti = false)
        }

    }

    fun dismissOverlay() {
        _uiState.value = _uiState.value.copy(showFutureOverlay = false)
    }


}
