package com.example.waterly

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun WaterlyScreenWrapper(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp) // space for bottom bar
        ) {
            content()
        }

        WaterlyBottomNavBar(
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
