package com.example.waterly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import com.example.waterly.ui.theme.WaterlyTypography
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.text.SimpleDateFormat
import java.util.Locale


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

@Composable
fun MonthlyBarChartFromHistory(waterHistory: Map<String, Int>, dates: List<String>) {

    val barData = dates.map { date -> waterHistory[date]?.toFloat() ?: 0f }

    val highlightDays = setOf(1, 5, 10, 15, 20)
    val lastDayOfMonth = dates.last().takeLast(2).toIntOrNull() ?: 0

    val xAxisLabels = dates.mapIndexed { index, date ->
        val day = date.takeLast(2).toIntOrNull() ?: return@mapIndexed ""
        val isLast = index == dates.lastIndex

        if (day in highlightDays || (day == 25 && lastDayOfMonth >= 30) || isLast) {
            day.toString()
        } else {
            ""
        }
    }

    // Tooltip labels in DD/MM/YYYY format
    val dateLabels = dates.map { raw ->
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val output = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            output.format(input.parse(raw)!!)
        } catch (e: Exception) {
            raw // fallback if parse fails
        }
    }

    // Pass to the chart
    MonthlyBarChart(
        data = barData,
        xAxisLabels = xAxisLabels,
        tooltipLabels = dateLabels
    )
}


@Composable
fun MonthlyBarChart(
    data: List<Float>,
    xAxisLabels: List<String>,
    tooltipLabels: List<String>
) {
    val selectedBar = remember { mutableStateOf<Pair<Int, String>?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            factory = { ctx ->
                BarChart(ctx).apply {
                    description.isEnabled = false
                    setDrawGridBackground(false)
                    setDrawBarShadow(false)
                    setDrawValueAboveBar(false) // HIDE default values
                    axisRight.isEnabled = false
                    axisLeft.granularity = 1f
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        granularity = 1f
                        isGranularityEnabled = true
                        setAvoidFirstLastClipping(false)
                        setDrawLabels(true)
                    }
                    legend.isEnabled = false
                    setTouchEnabled(true)
                    isHighlightPerTapEnabled = true

                    setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                        override fun onValueSelected(e: Entry?, h: Highlight?) {
                            val index = e?.x?.toInt() ?: return
                            val date = tooltipLabels.getOrNull(index) ?: return
                            selectedBar.value = index to date
                        }

                        override fun onNothingSelected() {
                            selectedBar.value = null
                        }
                    })
                }
            },
            update = { chart ->
                val entries = data.mapIndexed { index, value ->
                    BarEntry(index.toFloat(), value).also {
                        it.data = tooltipLabels.getOrNull(index) ?: ""
                    }
                }

                val dataSet = BarDataSet(entries, "Water (ml)").apply {
                    color = "#00B4FC".toColorInt()
                    valueTextColor = android.graphics.Color.DKGRAY
                    valueTextSize = 10f
                    setDrawValues(true) // Hide chart value labels

                    valueFormatter = object : ValueFormatter() {
                        override fun getBarLabel(barEntry: BarEntry?): String {
                            val value = barEntry?.y ?: return ""
                            return if (value == 0f) "" else value.toInt().toString()
                        }
                    }

                }

                chart.data = BarData(dataSet).apply { barWidth = 0.4f }
                chart.invalidate()
            }
        )

        selectedBar.value?.let { (index, dateText) ->
            val spacing = 300f / tooltipLabels.size
            val xOffset = (index * spacing).dp

            Text(
                text = dateText,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = xOffset, top = 8.dp)
                    .background(Color.LightGray)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                color = Color.Black,
                style = WaterlyTypography.labelSmall
            )
        }
    }
}



