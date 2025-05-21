package com.example.waterly.settings.viewModel

data class SettingsUiState(
    val goalInput: String = "",
    val currentGoal: Int = 2000,
    val notificationsEnabled: Boolean = false,
    val selectedInterval: Int = 2,
    val quietStart: String = "22:00",
    val quietEnd: String = "07:00",
    val showWipeConfirm: Boolean = false,
    val isInitialized: Boolean = false,
    val expandedStart: Boolean = false,
    val expandedEnd: Boolean = false,
    val expandedInterval: Boolean = false,
    val showGoalSavedDialog: Boolean = false
)