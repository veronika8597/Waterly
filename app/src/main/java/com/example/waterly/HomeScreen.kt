package com.example.waterly

import WaterFillCircle
import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.waterly.ui.theme.WaterlyTheme
import com.example.waterly.ui.theme.WaterlyTypography
import kotlinx.coroutines.launch


@SuppressLint("MutableCollectionMutableState")
@Composable
fun HomeScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var waterIntake by rememberSaveable { mutableIntStateOf(0) }
    val selectedDate = rememberSaveable { mutableStateOf(getTodayDate()) }
    val waterHistory = rememberSaveable { mutableStateOf(mutableMapOf<String, Int>()) }
    val waterGoal = 2000 // in ml
    val scale = remember { Animatable(1f) }
    var showBottleDialog by rememberSaveable { mutableStateOf(false) }
    var selectedBottleSize by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedItem by rememberSaveable { mutableStateOf<String?>(null) }
    val currentWeek = remember { getCurrentWeekDates() }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Drink Water",
                style = WaterlyTypography.displayLarge,
                color = Color(0xFF00B4FC)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Middle area - Centered Water Circle + Glass
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WaterFillCircle(waterIntake = waterIntake, waterGoal = waterGoal)

                Spacer(modifier = Modifier.height(10.dp))

                if (selectedItem != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(
                                id = if (selectedItem == "glass") R.drawable.water_glass
                                else R.drawable.water_bottle
                            ),
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = if (selectedItem == "glass") "250 ml" else "${selectedBottleSize ?: "?"} ml",
                            style = WaterlyTypography.bodyLarge, // << use correct size
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {

                    // Glass (clickable)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    0,
                                    if (selectedItem == "glass") (-8).dp.roundToPx() else 0
                                )
                            }
                            .clickable {
                                selectedItem = "glass"
                                waterIntake += 250
                                coroutineScope.launch {
                                    scale.animateTo(1.2f, tween(100))
                                    scale.animateTo(1f, tween(100))
                                }
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.water_glass),
                            contentDescription = "Glass of Water",
                            modifier = Modifier.size(55.dp),
                            colorFilter = if (selectedItem == "glass") null else ColorFilter.tint(
                                Color.Gray
                            )
                        )
                    }

                    // Bottle (clickable)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    0,
                                    if (selectedItem == "bottle") (-8).dp.roundToPx() else 0
                                )
                            }
                            .clickable {
                                showBottleDialog = true
                                selectedItem = "bottle"
                                coroutineScope.launch {
                                    scale.animateTo(1.2f, tween(100))
                                    scale.animateTo(1f, tween(100))
                                }
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.water_bottle),
                            contentDescription = "Water Bottle",
                            modifier = Modifier.size(60.dp),
                            colorFilter = if (selectedItem == "bottle") null else ColorFilter.tint(
                                Color.Gray
                            )
                        )
                    }
                }

                if (showBottleDialog) {
                    BottleSizeDialog(
                        onSelect = { size ->
                            waterIntake += size
                            selectedBottleSize = size
                            selectedItem = "bottle"
                            showBottleDialog = false
                        },
                        onDismiss = { showBottleDialog = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Week",
                style = WaterlyTypography.labelLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(Color(0xFF00B4FC)) // blue underline
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Week range (Nov 15 – Nov 21)
            Text(
                text = getWeekRangeTitle(currentWeek),
                style = WaterlyTypography.labelMedium,
                color = Color.Gray
            )

        }

        Spacer(modifier = Modifier.height(30.dp))

        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            currentWeek.forEachIndexed { index, date ->
                val isSelected = date == selectedDate.value

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedDate.value = date
                            waterIntake = waterHistory.value[date] ?: 0
                        }
                ) {
                    // Day name
                    Text(
                        text = dayNames[index % 7],
                        style = WaterlyTypography.labelSmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Date box
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (isSelected) Color(0xFF00B4FC) else Color(0xFFE0E0E0),
                                shape = MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.takeLast(2), // Show day number
                            style = WaterlyTypography.labelMedium,
                            color = if (isSelected) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WaterlyTheme {
        HomeScreen(navController = rememberNavController())
    }
}