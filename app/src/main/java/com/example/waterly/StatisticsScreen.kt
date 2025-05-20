package com.example.waterly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterly.ui.theme.WaterlyTheme
import com.example.waterly.ui.theme.WaterlyTypography

@Composable
fun StatisticsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Drinking Statistics",
            style = WaterlyTypography.displayLarge,
            color = Color(0xFF00B4FC)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Placeholder for Week Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFE0F7FA)),
            contentAlignment = Alignment.Center
        ) {
            Text("Weekly Chart (Coming Soon)", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Placeholder for Month Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFF1F8E9)),
            contentAlignment = Alignment.Center
        ) {
            Text("Monthly Chart (Coming Soon)", color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    WaterlyTheme {
        StatisticsScreen()
    }
}