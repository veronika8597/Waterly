package com.example.waterly

import android.annotation.SuppressLint

@SuppressLint("SimpleDateFormat")
fun isFutureDate(date: String): Boolean {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd")
    val today = formatter.parse(getTodayDate())
    val selected = formatter.parse(date)

    return selected?.after(today) ?: false
}
