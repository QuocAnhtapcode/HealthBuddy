package com.example.healthbuddy.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object HealthReminderNotification {

    const val CHANNEL_ID = "health_info_reminder"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Health Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Nhắc cập nhật thông tin sức khỏe hàng tuần"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}

