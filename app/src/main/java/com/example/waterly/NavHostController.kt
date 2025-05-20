package com.example.waterly

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//Sets up all navigation routes/screens
//TrackEatsNavGraph() defines the route "home" → shows HomeScreen
@Composable
fun WaterlyNavGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("statistics") { StatisticsScreen() }
    }
}
