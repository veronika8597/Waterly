package com.example.waterly

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getCurrentWeekDates(startDate: Date): List<String> {
    val calendar = Calendar.getInstance()
    calendar.time = startDate
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

fun getTodayDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(Date())
}

@SuppressLint("SimpleDateFormat")
fun isFutureDate(date: String): Boolean {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd")
    val today = formatter.parse(getTodayDate())
    val selected = formatter.parse(date)

    return selected?.after(today) ?: false
}


fun getDayLabel(date: String): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = formatter.parse(getTodayDate())
    val selected = formatter.parse(date)
    val diffDays = ((selected.time - today.time) / (1000 * 60 * 60 * 24)).toInt()

    return when (diffDays) {
        0 -> "Today"
        1 -> "Tomorrow"
        -1 -> "Yesterday"
        else -> date
    }
}