package com.example.waterly

import WaterFillCircle
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var waterIntake by rememberSaveable { mutableStateOf(0) }
    val waterGoal = 2000 // in ml
    val scale = remember { Animatable(1f) }
    var showBottleDialog by rememberSaveable { mutableStateOf(false) }
    var selectedBottleSize by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedItem by rememberSaveable { mutableStateOf<String?>(null) }


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
                text = "Drink Today",
                style = MaterialTheme.typography.headlineMedium
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
                            tint = Color(0xFF2196F3), // blue
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = if (selectedItem == "glass") "250 ml" else "${selectedBottleSize ?: "?"} ml",
                            style = MaterialTheme.typography.bodyMedium,
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
                            modifier = Modifier.size(56.dp),
                            colorFilter = if (selectedItem == "glass") null else ColorFilter.tint(Color.Gray)
                        )
                        Text(
                            text = "+250 ml",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp),
                            color = if (selectedItem == "glass") Color.Unspecified else Color.Gray
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
                                )}
                            .clickable {
                                showBottleDialog = true
                                selectedItem = "bottle"
                                coroutineScope.launch {
                                    scale.animateTo(1.2f, tween(100))
                                    scale.animateTo(1f, tween(100))
                                }}
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.water_bottle),
                            contentDescription = "Water Bottle",
                            modifier = Modifier.size(55.dp),
                            colorFilter = if (selectedItem == "bottle") null else ColorFilter.tint(Color.Gray)
                        )
                        Text(
                            text = "Bottle",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp),
                            color = if (selectedItem == "bottle") Color.Unspecified else Color.Gray
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
    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WaterlyTheme {
        HomeScreen(navController = rememberNavController())
    }
}
