package com.example.waterly

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getCurrentWeekDates(): List<String> {
    val calendar = Calendar.getInstance()

    // Go to start of the week (Sunday)
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val week = mutableListOf<String>()

    repeat(7) {
        week.add(dateFormat.format(calendar.time))
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    return week
}

fun getWeekRangeTitle(week: List<String>): String {
    if (week.size < 2) return ""
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    val start = outputFormat.format(inputFormat.parse(week.first())!!)
    val end = outputFormat.format(inputFormat.parse(week.last())!!)
    return "$start – $end"
}

