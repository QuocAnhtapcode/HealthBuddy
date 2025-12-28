package com.example.healthbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.healthbuddy.notification.HealthReminderScheduler
import com.example.healthbuddy.screens.MainApp
import com.example.healthbuddy.ui.theme.HealthBuddyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HealthReminderScheduler.scheduleWeeklyReminder(this)
        enableEdgeToEdge()
        setContent {
            HealthBuddyTheme {
                MainApp()
            }
        }
    }
}
