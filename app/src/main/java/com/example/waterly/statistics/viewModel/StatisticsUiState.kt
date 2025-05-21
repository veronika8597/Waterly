package com.example.waterly.statistics.viewModel

data class StatisticsUiState(
    val waterHistory: Map<String, Int> = emptyMap(),
    val weekOffset: Int = 0,
    val monthOffset: Int = 0
)