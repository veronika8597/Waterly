package com.example.waterly.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.waterly.settings.viewModel.SettingsViewModel
import com.example.waterly.ui.theme.WaterlyTheme
import com.example.waterly.ui.theme.WaterlyTypography
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val hourOptions = (0..23).map { String.format("%02d:00", it) }
    val intervalOptions = listOf(1, 2, 3, 4)

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Settings", Modifier.padding(start = 10.dp), style = WaterlyTypography.displayLarge, color = Color(0xFF00B4FC))

        SectionLabel("Daily Goal (ml)")
        OutlinedTextField(
            value = state.goalInput,
            onValueChange = viewModel::onGoalChanged,
            singleLine = true,
            modifier = Modifier.padding(horizontal = 10.dp),
            colors = goalInputColors()
        )

        Spacer(modifier = Modifier.height(3.dp))
        SaveButton("Save Goal") { viewModel.saveGoal(context) }

        Spacer(modifier = Modifier.height(10.dp))

        SectionLabel("Notifications")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Reminders", style = WaterlyTypography.bodyLarge)
            Switch(
                checked = state.notificationsEnabled,
                onCheckedChange = { viewModel.toggleNotifications(context, it) },
                colors = switchColors()
            )
        }

        if (state.notificationsEnabled && state.isInitialized) {
            IntervalSelection(state.selectedInterval, intervalOptions) {
                viewModel.setReminderInterval(context, it)
            }

            Spacer(modifier = Modifier.height(10.dp))

            SectionLabel("Quiet Hours")

            QuietHoursDropdown(
                label = "From",
                value = state.quietStart,
                expanded = state.expandedStart,
                onToggle = { viewModel.toggleStartDropdown() },
                onSelect = { viewModel.setQuietStart(context, it) },
                options = hourOptions
            )
            Spacer(modifier = Modifier.height(7.dp))

            QuietHoursDropdown(
                label = "Until",
                value = state.quietEnd,
                expanded = state.expandedEnd,
                onToggle = { viewModel.toggleEndDropdown() },
                onSelect = { viewModel.setQuietEnd(context, it) },
                options = hourOptions
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = { viewModel.setWipeConfirm(true) },
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text("Wipe All Data", color = Color(0xFFE91E63), style = WaterlyTypography.titleSmall)
        }

        //Set new goal
        if (state.showGoalSavedDialog) {
            LaunchedEffect(Unit) {
                delay(1000) // dismiss after 1 second
                viewModel.dismissGoalSavedDialog()
            }

            AlertDialog(
                onDismissRequest = { viewModel.dismissGoalSavedDialog() },
                confirmButton = {},
                title = {
                    Text(
                        text = "🎉 New goal set!",
                        style = WaterlyTypography.titleMedium,
                        color = Color(0xFF00B4FC)
                    )
                },
                containerColor = Color.White,
                textContentColor = Color.Black
            )
        }

        //Wipe data
        if (state.showWipeConfirm) {
            AlertDialog(
                onDismissRequest = { viewModel.setWipeConfirm(false) },
                title = { Text("Confirm Reset") },
                text = { Text("Are you sure you want to wipe all your water tracking data?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.wipeAllData(context)
                        Toast.makeText(context, "Data wiped", Toast.LENGTH_SHORT).show()
                    }) { Text("Wipe", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.setWipeConfirm(false) }) {
                        Text("Cancel")
                    }
                }
            )
        }




    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    WaterlyTheme {
        SettingsScreen(
            navController = rememberNavController(),
            selectedIndex = 2,
            onItemSelected = {}
        )
    }
}
