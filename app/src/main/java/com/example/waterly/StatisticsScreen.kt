package com.example.waterly

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.waterly.ui.theme.WaterlyTheme
import com.example.waterly.ui.theme.WaterlyTypography
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun StatisticsScreen(navController: NavHostController,
                     selectedIndex: Int,
                     onItemSelected: (Int) -> Unit) {

    val context = LocalContext.current
    val waterHistory = WaterDataStore.loadWaterHistory(context)

    val weekOffset = remember { mutableIntStateOf(0) }

    val currentWeek = remember(weekOffset.intValue) {
        Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, weekOffset.intValue) }.time
    }.let { getCurrentWeekDates(it) }

    val weekLabel = getWeekRangeTitle(currentWeek)

    val monthOffset = remember { mutableIntStateOf(0) }

    val currentMonth = remember(monthOffset.intValue) {
        Calendar.getInstance().apply { add(Calendar.MONTH, monthOffset.intValue) }.time
    }

    val monthLabel = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth)

    val monthDates = remember(monthOffset.intValue) {
        val cal = Calendar.getInstance().apply { time = currentMonth }
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val dates = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        while (cal.get(Calendar.MONTH) == currentMonth.month) {
            dates.add(dateFormat.format(cal.time))
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { weekOffset.intValue-- }) {
                Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Previous Week")
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text(
                    text = weekLabel,
                    style = WaterlyTypography.titleMedium)

                Spacer(modifier = Modifier.height(4.dp))

                val animatedLineWidth by animateDpAsState(
                    targetValue = 100.dp + (weekLabel.length.dp * 2),
                    label = "LineWidthAnimation"
                )

                Box(
                    modifier = Modifier
                        .width(animatedLineWidth)
                        .height(2.dp)
                        .background(Color(0xFF00B4FC))
                )

            }

            IconButton(
                onClick = { if (weekOffset.intValue < 0) weekOffset.intValue++ },
                enabled = weekOffset.intValue < 0
            ) {
                Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = "Next Week")
            }
        }

        //Week Chart
        WeeklyBarChartFromHistory(waterHistory, currentWeek)

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            IconButton(
                onClick = { monthOffset.intValue-- })
            {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Previous Month"
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text(
                    text = monthLabel,
                    style = WaterlyTypography.titleMedium)

                Spacer(modifier = Modifier.height(4.dp))

                val animatedLineWidth by animateDpAsState(
                    targetValue = 60.dp + (monthLabel.length.dp * 4),
                    label = "LineWidthAnimation"
                )

                Box(
                    modifier = Modifier
                        .width(animatedLineWidth)
                        .height(2.dp)
                        .background(Color(0xFF00B4FC))
                )

            }

            IconButton(
                onClick = { if (monthOffset.intValue < 0) monthOffset.intValue++ },
                enabled = monthOffset.intValue < 0
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Next Month")
            }
        }

        //Month Chart
        MonthlyBarChartFromHistory(waterHistory, monthDates)

    }
}


@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    WaterlyTheme {
        StatisticsScreen(navController = rememberNavController(),
            selectedIndex = 0,
            onItemSelected = {}
        )
    }
}
