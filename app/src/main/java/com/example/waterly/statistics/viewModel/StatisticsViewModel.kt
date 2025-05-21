package com.example.waterly.statistics.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterly.WaterDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatisticsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState

    fun loadHistory(context: Context) {
        viewModelScope.launch {
            val history = WaterDataStore.loadWaterHistory(context)
            _uiState.value = _uiState.value.copy(waterHistory = history)
        }
    }

    fun previousWeek() {
        _uiState.value = _uiState.value.copy(weekOffset = _uiState.value.weekOffset - 1)
    }

    fun nextWeek() {
        if (_uiState.value.weekOffset < 0) {
            _uiState.value = _uiState.value.copy(weekOffset = _uiState.value.weekOffset + 1)
        }
    }

    fun previousMonth() {
        _uiState.value = _uiState.value.copy(monthOffset = _uiState.value.monthOffset - 1)
    }

    fun nextMonth() {
        if (_uiState.value.monthOffset < 0) {
            _uiState.value = _uiState.value.copy(monthOffset = _uiState.value.monthOffset + 1)
        }
    }
}
