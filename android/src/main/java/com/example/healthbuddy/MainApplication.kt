package com.example.healthbuddy

import android.app.Application
import com.example.healthbuddy.notification.HealthReminderNotification
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        HealthReminderNotification.createChannel(this)
    }
}

