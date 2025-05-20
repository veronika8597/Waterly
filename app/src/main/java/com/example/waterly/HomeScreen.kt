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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.waterly.ui.theme.WaterlyTheme
import com.example.waterly.ui.theme.WaterlyTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@SuppressLint("MutableCollectionMutableState")
@Composable
fun HomeScreen(navController: NavHostController, selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var waterIntake by rememberSaveable { mutableIntStateOf(0) }
    val selectedDate = rememberSaveable { mutableStateOf(getTodayDate()) }
    //val waterGoal = 2000 // in ml
    val waterGoal = remember {WaterDataStore.loadDailyGoal(context)}
    val scale = remember { Animatable(1f) }
    var showBottleDialog by rememberSaveable { mutableStateOf(false) }
    var selectedBottleSize by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedItem by rememberSaveable { mutableStateOf<String?>(null) }

    val waterHistory = remember { mutableStateOf(WaterDataStore.loadWaterHistory(context)) }
    var showFutureOverlay by rememberSaveable { mutableStateOf(false) }
    //var selectedIndex by rememberSaveable { mutableIntStateOf(1) }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var currentWeekStart by rememberSaveable(
        stateSaver = Saver(
            save = { dateFormat.format(it) },
            restore = { dateFormat.parse(it)!! }
        )) { mutableStateOf(dateFormat.parse(getTodayDate())!!) }

    val currentWeek by remember(currentWeekStart) {
        mutableStateOf(getCurrentWeekDates(currentWeekStart))
    }


    LaunchedEffect(selectedDate.value) {
        waterIntake = waterHistory.value[selectedDate.value] ?: 0
        selectedItem = null
        selectedBottleSize = null
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Drink Water",
                style = WaterlyTypography.displayLarge,
                color = Color(0xFF00B4FC)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = getDayLabel(selectedDate.value),
            style = WaterlyTypography.titleMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(35.dp))

        // Water circle + selection UI
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
                            style = WaterlyTypography.bodyLarge,
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
                                if (!isFutureDate(selectedDate.value)) {
                                    selectedItem = "glass"
                                    waterIntake += 250
                                    waterHistory.value =
                                        waterHistory.value.toMutableMap().apply {
                                            put(selectedDate.value, waterIntake)
                                        }
                                    WaterDataStore.saveWaterHistory(context, waterHistory.value)
                                    coroutineScope.launch {
                                        scale.animateTo(1.2f, tween(100))
                                        scale.animateTo(1f, tween(100))
                                    }
                                } else {
                                    showFutureOverlay = true
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
                                if (!isFutureDate(selectedDate.value)) {
                                    showBottleDialog = true
                                    selectedItem = "bottle"
                                    coroutineScope.launch {
                                        scale.animateTo(1.2f, tween(100))
                                        scale.animateTo(1f, tween(100))
                                    }
                                } else {
                                    showFutureOverlay = true
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
                            waterHistory.value = waterHistory.value.toMutableMap().apply {
                                put(selectedDate.value, waterIntake)
                            }
                            WaterDataStore.saveWaterHistory(context, waterHistory.value)

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

        // Week view
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
                    .background(Color(0xFF00B4FC))
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIos,
                    contentDescription = "Previous Week",
                    tint = Color.LightGray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            val cal = Calendar.getInstance().apply {
                                time = currentWeekStart
                                add(Calendar.WEEK_OF_YEAR, -1)
                            }
                            currentWeekStart = cal.time
                        }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = getWeekRangeTitle(currentWeek),
                    style = WaterlyTypography.labelMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Next Week",
                    tint = Color.LightGray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            val cal = Calendar.getInstance().apply {
                                time = currentWeekStart
                                add(Calendar.WEEK_OF_YEAR, 1)
                            }
                            currentWeekStart = cal.time
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Day list
        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        Row(
            modifier = Modifier.fillMaxWidth(),
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
                    Text(
                        text = dayNames[index % 7],
                        style = WaterlyTypography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(
                                color = if (isSelected) Color(0xFF00B4FC) else Color(
                                    0xFFE0E0E0
                                ),
                                shape = MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.takeLast(2),
                            style = WaterlyTypography.labelMedium,
                            color = if (isSelected) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }

    // Overlay for future date warning
    if (showFutureOverlay) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Can't log future data",
                style = WaterlyTypography.bodyLarge,
                color = Color.White
            )

            LaunchedEffect(Unit) {
                delay(1500)
                showFutureOverlay = false
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WaterlyTheme {
        HomeScreen(
            navController = rememberNavController(),
            selectedIndex = 1, // pretend Home is selected
            onItemSelected = {}// no-op for preview
        )
    }
}