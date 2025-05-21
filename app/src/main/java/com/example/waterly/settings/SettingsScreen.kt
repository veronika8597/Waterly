package com.example.waterly.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.waterly.ReminderScheduler
import com.example.waterly.WaterDataStore
import com.example.waterly.ui.theme.WaterlyTheme
import com.example.waterly.ui.theme.WaterlyTypography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
)
{

    val context = LocalContext.current
    val intervalOptions = listOf(1, 2, 3, 4) // in hours

    var goalInput by remember { mutableStateOf("") }
    val currentGoal = remember { mutableIntStateOf(WaterDataStore.loadDailyGoal(context)) }

    var notificationsEnabled by remember { mutableStateOf(false) }
    var selectedInterval by remember { mutableStateOf(2) }
    var isInitialized by remember { mutableStateOf(false) }

    var showWipeConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        notificationsEnabled = WaterDataStore.loadReminderEnabled(context)
        selectedInterval = WaterDataStore.loadReminderInterval(context)
        isInitialized = true
    }

    LaunchedEffect(currentGoal.intValue) {
        goalInput = currentGoal.intValue.toString()
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = "Settings",
            style = WaterlyTypography.displayLarge,
            color = Color(0xFF00B4FC)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = "Daily Goal (ml)",
            style = WaterlyTypography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = goalInput,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    goalInput = newValue
                }
            },
            singleLine = true,
            modifier = Modifier
                .padding(horizontal = 10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0x2700B4FC),
                unfocusedContainerColor = Color(0x2700B4FC),
                disabledContainerColor = Color(0x2700B4FC),
                errorContainerColor = Color(0x2700B4FC),
                focusedIndicatorColor = Color(0xFF00B4FC),
               /* focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,*/
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF00B4FC)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val parsed = goalInput.toIntOrNull()
                if (parsed != null && parsed in 500..5000) {
                    WaterDataStore.saveDailyGoal(context, parsed)
                    currentGoal.intValue = parsed
                }
            },
            modifier = Modifier.padding(start = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00B6FF),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Save Goal",
                style = WaterlyTypography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = "Notifications",
            style = WaterlyTypography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Reminders", style = WaterlyTypography.bodyLarge)
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = {
                    notificationsEnabled = it
                    WaterDataStore.saveReminderEnabled(context, it)

                    if (it) {
                        ReminderScheduler.scheduleRepeatingReminder(context, selectedInterval)
                    } else {
                        ReminderScheduler.cancelReminder(context)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF00B4FC),
                    checkedTrackColor = Color(0x2700B4FC),
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.Gray)
            )
        }

        if (isInitialized && notificationsEnabled) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = "Reminder Interval",
                style = WaterlyTypography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.padding(start = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                intervalOptions.forEach { hours ->
                    TextButton(onClick = {
                        selectedInterval = hours
                        WaterDataStore.saveReminderInterval(context, hours)
                    }) {
                        Text(
                            text = "$hours hr",
                            color = if (selectedInterval == hours) Color(0xFF00B4FC) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = "Quiet Hours",
                style = WaterlyTypography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            val hourOptions = (0..23).map { String.format("%02d:00", it) }
            var quietStart by remember { mutableStateOf("22:00") }
            var quietEnd by remember { mutableStateOf("07:00") }
            var expandedStart by remember { mutableStateOf(false) }
            var expandedEnd by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                val (start, end) = WaterDataStore.loadQuietHours(context)
                quietStart = String.format("%02d:00", start)
                quietEnd = String.format("%02d:00", end)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.padding(bottom = 10.dp),
                        text = "From",
                        style = WaterlyTypography.bodyLarge
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedStart,
                        onExpandedChange = { expandedStart = !expandedStart }
                    ) {
                        TextField(
                            value = quietStart,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStart) },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF00B4FC),
                                unfocusedIndicatorColor = Color.Gray,
                                cursorColor = Color(0xFF00B4FC),
                                focusedLabelColor = Color(0xFF00B4FC),
                                focusedContainerColor = Color(0x2700B4FC),
                                unfocusedContainerColor = Color(0x2700B4FC),
                                disabledContainerColor = Color(0x2700B4FC),
                            )
                        )
                        DropdownMenu(
                            expanded = expandedStart,
                            onDismissRequest = { expandedStart = false }
                        ) {
                            hourOptions.forEach { hour ->
                                DropdownMenuItem(
                                    text = { Text(hour) },
                                    onClick = {
                                        quietStart = hour
                                        expandedStart = false
                                        WaterDataStore.saveQuietHours(
                                            context,
                                            quietStart.take(2).toInt(),
                                            quietEnd.take(2).toInt()
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.padding(bottom = 10.dp),
                        text = "Until",
                        style = WaterlyTypography.bodyLarge
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedEnd,
                        onExpandedChange = { expandedEnd = !expandedEnd }
                    ) {
                        TextField(
                            value = quietEnd,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEnd) },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF00B4FC),
                                unfocusedIndicatorColor = Color.Gray,
                                cursorColor = Color(0xFF00B4FC),
                                focusedLabelColor = Color(0xFF00B4FC),
                                focusedContainerColor = Color(0x2700B4FC),
                                unfocusedContainerColor = Color(0x2700B4FC),
                                disabledContainerColor = Color(0x2700B4FC),
                            )
                        )
                        DropdownMenu(
                            expanded = expandedEnd,
                            onDismissRequest = { expandedEnd = false }
                        ) {
                            hourOptions.forEach { hour ->
                                DropdownMenuItem(
                                    text = { Text(hour) },
                                    onClick = {
                                        quietEnd = hour
                                        expandedEnd = false
                                        WaterDataStore.saveQuietHours(
                                            context,
                                            quietStart.take(2).toInt(),
                                            quietEnd.take(2).toInt()
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(
            onClick = { showWipeConfirm = true },
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text(
                text = "Wipe All Data",
                color = Color(0xFFE91E63),
                style = WaterlyTypography.titleSmall)
        }

        if (showWipeConfirm) {
            AlertDialog(
                onDismissRequest = { showWipeConfirm = false },
                title = { Text("Confirm Reset") },
                text = { Text("Are you sure you want to wipe all your water tracking data?") },
                confirmButton = {
                    TextButton(onClick = {
                        WaterDataStore.clearAllData(context)
                        Toast.makeText(context, "Data wiped", Toast.LENGTH_SHORT).show()
                        showWipeConfirm = false
                    }) {
                        Text("Wipe", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showWipeConfirm = false }) {
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