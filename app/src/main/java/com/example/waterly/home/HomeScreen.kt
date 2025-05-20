package com.example.waterly.home

import WaterFillCircle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.waterly.BottleSizeDialog
import com.example.waterly.R
import com.example.waterly.getCurrentWeekDates
import com.example.waterly.getDayLabel
import com.example.waterly.getTodayDate
import com.example.waterly.getWeekRangeTitle
import com.example.waterly.home.viewmodel.HomeViewModel
import com.example.waterly.ui.theme.WaterlyTheme
import com.example.waterly.ui.theme.WaterlyTypography
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    navController: NavHostController,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var currentWeekStart by rememberSaveable(
        stateSaver = Saver(
            save = { dateFormat.format(it) },
            restore = { dateFormat.parse(it)!! }
        )
    ) { mutableStateOf(dateFormat.parse(getTodayDate())!!) }

    val currentWeek by remember(currentWeekStart) {
        mutableStateOf(getCurrentWeekDates(currentWeekStart))
    }

    LaunchedEffect(uiState.selectedDate) {
        viewModel.loadWaterData(context)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Drink Water",
            style = WaterlyTypography.displayLarge,
            color = Color(0xFF00B4FC),
            modifier = Modifier.padding(start = 10.dp)
        )

        Text(
            text = getDayLabel(uiState.selectedDate),
            style = WaterlyTypography.titleMedium,
            color = Color.Gray,
            modifier = Modifier.padding(start = 10.dp)
        )

        Spacer(modifier = Modifier.height(35.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WaterFillCircle(waterIntake = uiState.waterIntake, waterGoal = uiState.waterGoal)
                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.selectedItem != null) {
                    Icon(
                        painter = painterResource(id = if (uiState.selectedItem == "glass") R.drawable.water_glass else R.drawable.water_bottle),
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = uiState.selectedSizeLabel,
                        style = WaterlyTypography.bodyLarge,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    // Glass
                    Image(
                        painter = painterResource(id = R.drawable.water_glass),
                        contentDescription = null,
                        modifier = Modifier
                            .size(55.dp)
                            .offset { IntOffset(0, if (uiState.selectedItem == "glass") (-8).dp.roundToPx() else 0) }
                            .clickable @androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS) { viewModel.onGlassClicked(context) },
                        colorFilter = if (uiState.selectedItem != "glass") ColorFilter.tint(Color.Gray) else null
                    )

                    // Bottle
                    Image(
                        painter = painterResource(id = R.drawable.water_bottle),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .offset { IntOffset(0, if (uiState.selectedItem == "bottle") (-8).dp.roundToPx() else 0) }
                            .clickable { viewModel.onBottleClicked(context) },
                        colorFilter = if (uiState.selectedItem != "bottle") ColorFilter.tint(Color.Gray) else null
                    )
                }

                if (uiState.showBottleDialog) {
                    BottleSizeDialog(
                        onSelect = @androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS) { size -> viewModel.onBottleSelected(context, size) },
                        onDismiss = { viewModel.onBottleDialogDismiss() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            "Week",
            style = WaterlyTypography.labelLarge,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(60.dp)
                .height(2.dp)
                .background(Color(0xFF00B4FC))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Icon(
                Icons.Default.ArrowBackIos,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(24.dp).clickable {
                    val cal = Calendar.getInstance().apply {
                        time = currentWeekStart
                        add(Calendar.WEEK_OF_YEAR, -1)
                    }
                    currentWeekStart = cal.time
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(getWeekRangeTitle(currentWeek), style = WaterlyTypography.labelMedium, color = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(24.dp).clickable {
                    val cal = Calendar.getInstance().apply {
                        time = currentWeekStart
                        add(Calendar.WEEK_OF_YEAR, 1)
                    }
                    currentWeekStart = cal.time
                }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            currentWeek.forEachIndexed { index, date ->
                val isSelected = date == uiState.selectedDate
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.onDateSelected(date, context) }
                ) {
                    Text(dayNames[index % 7], style = WaterlyTypography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(
                                color = if (isSelected) Color(0xFF00B4FC) else Color(0xFFE0E0E0),
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

    if (uiState.showFutureOverlay) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Text("Can't log future data", style = WaterlyTypography.bodyLarge, color = Color.White)
            LaunchedEffect(Unit) {
                delay(1500)
                viewModel.dismissOverlay()
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
            selectedIndex = 1,
            onItemSelected = {}
        )
    }
}
