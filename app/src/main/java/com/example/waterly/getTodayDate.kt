package com.example.waterly

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getTodayDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(Date())
}
