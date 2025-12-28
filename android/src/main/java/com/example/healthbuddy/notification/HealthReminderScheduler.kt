package com.example.healthbuddy.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit

object HealthReminderScheduler {

    fun scheduleWeeklyReminder(context: Context) {

        val delay = calculateInitialDelayToSunday20h()

        val request = PeriodicWorkRequestBuilder<HealthInfoReminderWorker>(
            7, TimeUnit.DAYS
        )
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            //.setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "weekly_health_reminder",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }
    fun calculateInitialDelayToSunday20h(): Long {
        val now = LocalDateTime.now()
        var nextSunday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            .withHour(20)
            .withMinute(0)
            .withSecond(0)

        if (now.isAfter(nextSunday)) {
            nextSunday = nextSunday.plusWeeks(1)
        }

        return Duration.between(now, nextSunday).toMillis()
    }

}

