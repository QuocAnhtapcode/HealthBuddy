package com.example.healthbuddy.service

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import androidx.health.services.client.data.ExerciseState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.healthbuddy.data.DataSyncRepository
import com.example.healthbuddy.data.ExerciseClientManager
import com.example.healthbuddy.data.isExerciseInProgress
import com.example.healthbuddy.presentation.summary.SummaryScreenState
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExerciseService : LifecycleService() {
    @Inject
    lateinit var exerciseClientManager: ExerciseClientManager

    @Inject
    lateinit var exerciseNotificationManager: ExerciseNotificationManager

    @Inject
    lateinit var exerciseServiceMonitor: ExerciseServiceMonitor

    private var isBound = false
    private var isStarted = false
    private val localBinder = LocalBinder()

    private val serviceRunningInForeground: Boolean
        get() = this.foregroundServiceType != 0

    private suspend fun isExerciseInProgress() =
        exerciseClientManager.exerciseClient.isExerciseInProgress()

    suspend fun prepareExercise() {
        exerciseClientManager.prepareExercise()
    }
    suspend fun startExercise() {
        postOngoingActivityNotification()
        exerciseClientManager.startExercise()
    }
    suspend fun pauseExercise() {
        exerciseClientManager.pauseExercise()
    }
    suspend fun resumeExercise() {
        exerciseClientManager.resumeExercise()
    }
    suspend fun endExercise(){
        exerciseClientManager.endExercise()
        removeOngoingActivityNotification()
    }

    fun markLap() {
        lifecycleScope.launch {
            exerciseClientManager.markLap()
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        super.onStartCommand(intent, flags, startId)

        Log.d(TAG, "onStartCommand")

        if (!isStarted) {
            isStarted = true

            if (!isBound) {
                stopSelfIfNotRunning()
            }
            // Start collecting exercise information
            lifecycleScope.launch(Dispatchers.Default) {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    exerciseServiceMonitor.monitor()
                }
            }
        }
        // If our process is stopped, we might have an active exercise. We want the system to
        // recreate our service so that we can present the ongoing notification in that case.
        return START_STICKY
    }

    private fun stopSelfIfNotRunning() {
        lifecycleScope.launch {
            // We may have been restarted by the system. Check for an ongoing exercise.
            if (!isExerciseInProgress()) {
                // Need to cancel [prepareExercise()] to prevent battery drain.
                if (exerciseServiceMonitor.exerciseServiceState.value.exerciseState ==
                    ExerciseState.PREPARING
                ) {
                    lifecycleScope.launch {
                        endExercise()
                    }
                }
                stopSelf()
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)

        handleBind()

        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)

        handleBind()
    }

    private fun handleBind() {
        if (!isBound) {
            isBound = true
            startService(Intent(this, this::class.java))
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBound = false
        lifecycleScope.launch {
            delay(UNBIND_DELAY)
            if (!isBound) {
                stopSelfIfNotRunning()
            }
        }
        return true
    }

    fun removeOngoingActivityNotification() {
        if (serviceRunningInForeground) {
            Log.d(TAG, "Removing ongoing activity notification")
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    private fun postOngoingActivityNotification() {
        if (!serviceRunningInForeground) {
            Log.d(TAG, "Posting ongoing activity notification")

            exerciseNotificationManager.createNotificationChannel()
            val serviceState = exerciseServiceMonitor.exerciseServiceState.value
            ServiceCompat.startForeground(
                this,
                ExerciseNotificationManager.NOTIFICATION_ID,
                exerciseNotificationManager.buildNotification(
                    serviceState.activeDurationCheckpoint?.activeDuration
                        ?: Duration.ZERO
                ),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION or
                    if
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
                    } else {
                        0
                    }
            )
        }
    }
    inner class LocalBinder : Binder() {
        fun getService() = this@ExerciseService

        val exerciseServiceState: Flow<ExerciseServiceState>
            get() = this@ExerciseService.exerciseServiceMonitor.exerciseServiceState
    }

    companion object {
        private val UNBIND_DELAY = 3.seconds
    }
}
