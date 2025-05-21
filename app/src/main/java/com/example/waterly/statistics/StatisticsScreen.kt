package com.example.waterly.statistics

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.waterly.MonthlyBarChartFromHistory
import com.example.waterly.WeeklyBarChartFromHistory
import com.example.waterly.getCurrentWeekDates
import com.example.waterly.getWeekRangeTitle
import com.example.waterly.statistics.viewModel.StatisticsViewModel
import com.example.waterly.ui.theme.WaterlyTheme
import com.example.waterly.ui.theme.WaterlyTypography
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatisticsScreen(
    navController: NavHostController,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    viewModel: StatisticsViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHistory(context)
    }

    val currentWeek = remember(uiState.weekOffset) {
        Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, uiState.weekOffset)
        }.time
    }.let { getCurrentWeekDates(it) }

    val weekLabel = getWeekRangeTitle(currentWeek)

    val currentMonth = remember(uiState.monthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, uiState.monthOffset)
        }.time
    }

    val monthLabel = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth)

    val monthDates = remember(uiState.monthOffset) {
        val cal = Calendar.getInstance().apply { time = currentMonth }
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val dates = mutableListOf<String>()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        while (cal.get(Calendar.MONTH) == currentMonth.month) {
            dates.add(format.format(cal.time))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        dates
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Drinking Statistics",
            style = WaterlyTypography.displayLarge,
            color = Color(0xFF00B4FC)
        )

        // WEEK SECTION
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.previousWeek() }) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Previous Week")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(weekLabel, style = WaterlyTypography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                val animatedWidth by animateDpAsState(
                    targetValue = 100.dp + (weekLabel.length.dp * 2),
                    label = "LineWidthAnimation"
                )
                Box(
                    modifier = Modifier
                        .width(animatedWidth)
                        .height(2.dp)
                        .background(Color(0xFF00B4FC))
                )
            }

            IconButton(
                onClick = { viewModel.nextWeek() },
                enabled = uiState.weekOffset < 0
            ) {
                Icon(Icons.Default.ArrowForwardIos, contentDescription = "Next Week")
            }
        }

        WeeklyBarChartFromHistory(uiState.waterHistory, currentWeek)

        Spacer(modifier = Modifier.height(32.dp))

        // MONTH SECTION
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.previousMonth() }) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Previous Month")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(monthLabel, style = WaterlyTypography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                val animatedWidth by animateDpAsState(
                    targetValue = 60.dp + (monthLabel.length.dp * 4),
                    label = "LineWidthAnimation"
                )
                Box(
                    modifier = Modifier
                        .width(animatedWidth)
                        .height(2.dp)
                        .background(Color(0xFF00B4FC))
                )
            }

            IconButton(
                onClick = { viewModel.nextMonth() },
                enabled = uiState.monthOffset < 0
            ) {
                Icon(Icons.Default.ArrowForwardIos, contentDescription = "Next Month")
            }
        }

        MonthlyBarChartFromHistory(uiState.waterHistory, monthDates)
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    WaterlyTheme {
        StatisticsScreen(
            navController = rememberNavController(),
            selectedIndex = 0,
            onItemSelected = {}
        )
    }
}
