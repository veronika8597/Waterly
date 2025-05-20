package com.example.waterly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.waterly.ui.theme.WaterlyTheme
import com.example.waterly.ui.theme.WaterlyTypography

@Composable
fun StatisticsScreen(navController: NavHostController,
                     selectedIndex: Int,
                     onItemSelected: (Int) -> Unit) {

    //var selectedIndex by rememberSaveable { mutableIntStateOf(1) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

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
        StatisticsScreen(navController = rememberNavController(),
            selectedIndex = 0, // pretend Statistics is selected
            onItemSelected = {} // no-op for preview
        )
    }
}
