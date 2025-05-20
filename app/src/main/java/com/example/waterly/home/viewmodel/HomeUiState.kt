package com.example.waterly.home.viewmodel


data class HomeUiState(
    val waterIntake: Int = 0,
    val selectedDate: String = "",
    val waterGoal: Int = 2000,
    val selectedItem: String? = null,
    val selectedBottleSize: Int? = null,
    val showBottleDialog: Boolean = false,
    val showFutureOverlay: Boolean = false,
    val waterHistory: Map<String, Int> = emptyMap()
) {
    val selectedSizeLabel: String
        get() = when (selectedItem) {
            "glass" -> "250 ml"
            "bottle" -> "${selectedBottleSize ?: "?"} ml"
            else -> ""
        }
}
