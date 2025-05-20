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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.waterly.ui.theme.WaterlyTheme
import com.example.waterly.ui.theme.WaterlyTypography
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.core.graphics.toColorInt


@Composable
fun StatisticsScreen(navController: NavHostController,
                     selectedIndex: Int,
                     onItemSelected: (Int) -> Unit) {

    val context = LocalContext.current
    val waterHistory = WaterDataStore.loadWaterHistory(context)

    val weekOffset = remember { mutableStateOf(0) }

    val currentWeek = remember(weekOffset.value) {
        Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, weekOffset.value) }.time
    }.let { getCurrentWeekDates(it) }

    val weekLabel = getWeekRangeTitle(currentWeek)

    val today = getTodayDate()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Week dates
    val calendar = Calendar.getInstance()
    //val currentWeek = getCurrentWeekDates(calendar.time)

/*    // Entries for the chart
    val entries = currentWeek.mapIndexed { index, date ->
        FloatEntry(index.toFloat(), waterHistory[date]?.toFloat() ?: 0f)
    }*/

    val labels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

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
            IconButton(onClick = { weekOffset.value-- }) {
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
                onClick = { if (weekOffset.value < 0) weekOffset.value++ },
                enabled = weekOffset.value < 0
            ) {
                Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = "Next Week")
            }
        }

        //Week Chart
        WeeklyBarChartFromHistory(waterHistory, currentWeek)

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

@Composable
fun WeeklyBarChartFromHistory(waterHistory: Map<String, Int>, weekDates: List<String>) {

    val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val barData = weekDates.map { date -> waterHistory[date]?.toFloat() ?: 0f }

    WeeklyBarChart(data = barData, labels = dayLabels)
}

@Composable
fun WeeklyBarChart(data: List<Float>, labels: List<String>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setDrawValueAboveBar(true)
                axisRight.isEnabled = false
                axisLeft.granularity = 1f
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                legend.isEnabled = false
            }
        },
        update = { chart ->
            val entries = data.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }
            val dataSet = BarDataSet(entries, "Water (ml)").apply {
                color = "#00B4FC".toColorInt()
                valueTextColor = android.graphics.Color.BLACK
                valueTextSize = 12f
            }
            chart.data = BarData(dataSet)
            chart.invalidate()
        }
    )
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
