package com.example.waterly

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
//import com.example.waterly.ReminderWorker.Companion.testReminder
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private const val WORK_NAME = "water_reminder_work"

    fun scheduleRepeatingReminder(context: Context, intervalHours: Int) {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            intervalHours.toLong(), TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

/*    fun testReminderNow(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(2, TimeUnit.SECONDS) // runs almost immediately
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }*/

/*
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun testReminderNow(context: Context) {
        val waterHistory = WaterDataStore.loadWaterHistory(context)
        val goal = WaterDataStore.loadDailyGoal(context)
        val today = getTodayDate()
        val todayIntake = waterHistory[today] ?: 0

        if (todayIntake >= goal) {
            Log.d("ReminderTest", "Skipped: goal already met")
        } else {
            Log.d("ReminderTest", "Showing reminder")
            testReminder(context)

        }
    }
*/


}
