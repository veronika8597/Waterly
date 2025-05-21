package com.example.waterly

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.waterly.home.HomeScreen
import com.example.waterly.settings.SettingsScreen
import com.example.waterly.statistics.StatisticsScreen


@Composable
fun RootNavigationScreen() {

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val selectedIndex = when (currentRoute) {
        "statistics" -> 0
        "home" -> 1
        "settings" -> 2
        else -> 1
    }

    val onItemSelected: (Int) -> Unit = { index ->
        val route = when (index) {
            0 -> "statistics"
            1 -> "home"
            2 -> "settings"
            else -> error("Invalid index")
        }

        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Your screen content (NavHost)
        NavHost(navController, startDestination = "splash") {

            composable("splash") {
                SplashScreen(navController)
            }


            composable("home") {
                HomeScreen(
                    navController = navController,
                    selectedIndex = selectedIndex,
                    onItemSelected = onItemSelected
                )
            }

            composable("statistics") {
                StatisticsScreen(
                    navController = navController,
                    selectedIndex = selectedIndex,
                    onItemSelected = onItemSelected
                )
            }

            composable("settings") {
                SettingsScreen(
                    navController = navController,
                    selectedIndex = selectedIndex,
                    onItemSelected = onItemSelected
                )
            }
        }

        // 🔥 Show bottom bar ONCE outside all screens
        if (currentRoute != "splash") {
            WaterlyBottomNavBar(
                selectedIndex = selectedIndex,
                onItemSelected = onItemSelected,
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}