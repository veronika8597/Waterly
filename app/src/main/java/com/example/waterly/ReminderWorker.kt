package com.example.waterly

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        showReminderNotification()
        return Result.success()
    }

    private fun showReminderNotification() {
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
}