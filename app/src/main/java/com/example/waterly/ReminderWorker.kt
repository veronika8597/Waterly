package com.example.waterly

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Calendar

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val context = applicationContext

        val waterHistory = WaterDataStore.loadWaterHistory(context)
        val goal = WaterDataStore.loadDailyGoal(context)
        val today = getTodayDate()
        val todayIntake = waterHistory[today] ?: 0

        val (quietStart, quietEnd) = WaterDataStore.loadQuietHours(context)
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        if (todayIntake >= goal) {
            Log.d("ReminderWorker", "Skipped: goal met ($todayIntake / $goal)")
            return Result.success()
        }


        val isQuietTime = if (quietStart < quietEnd) {
            currentHour in quietStart until quietEnd
        } else {
            currentHour >= quietStart || currentHour < quietEnd
        }

        if (isQuietTime) {
            Log.d("ReminderWorker", "Skipped: quiet hours ($currentHour)")
            return Result.success()
        }

        showReminderNotification(context)
        return Result.success()
    }

    private fun showReminderNotification(context: Context) {
        val channelId = "water_reminder_channel"
        val notificationId = 1

        // Create notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Water Reminders"
            val descriptionText = "Reminds you to drink water"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build and show the notification
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Change if needed
            .setContentTitle("Time to hydrate 💧")
            .setContentText("Don't forget to drink water!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (NotificationManagerCompat.from(applicationContext)
                .areNotificationsEnabled()
        ) {
            with(NotificationManagerCompat.from(applicationContext)) {
                try {
                    notify(notificationId, builder.build())
                } catch (e: SecurityException) {
                    Log.e("Error showing notification", e.toString())
                }
            }
        }
    }

    companion object {
        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
        fun sendGoalReachedNotification(context: Context) {
            val channelId = "goal_reached_channel"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Goal Achievements",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Celebrate reaching your water goal!"
                }
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("🎉 Goal Reached!")
                .setContentText("You've hit your daily water goal — great job! 💧")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            NotificationManagerCompat.from(context).notify(200, builder.build())
        }
    }
}