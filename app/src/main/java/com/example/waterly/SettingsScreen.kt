package com.example.waterly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.waterly.ui.theme.WaterlyTheme
import com.example.waterly.ui.theme.WaterlyTypography

@Composable
fun SettingsScreen(
    navController: NavHostController,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
)
{

    val context = LocalContext.current
    var goalInput by remember { mutableStateOf("") }
    val currentGoal = remember { mutableIntStateOf(WaterDataStore.loadDailyGoal(context)) }

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
                .padding(horizontal = 10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                val parsed = goalInput.toIntOrNull()
                if (parsed != null && parsed in 500..5000) {
                    WaterDataStore.saveDailyGoal(context, parsed)
                    currentGoal.intValue = parsed
                }
            },
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text("Save Goal")
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