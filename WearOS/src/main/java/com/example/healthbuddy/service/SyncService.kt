package com.example.healthbuddy.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.healthbuddy.common.DataPaths
import com.example.healthbuddy.data.DataLayerClientManager
import com.example.healthbuddy.presentation.summary.SummaryScreenState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SyncService: LifecycleService() {
    @Inject
    lateinit var dataLayerClientManager: DataLayerClientManager
    private var isBound = false
    private val localBinder = LocalBinder()

    suspend fun sendExerciseSummary(summaryScreenState: SummaryScreenState) =
        dataLayerClientManager.sendSummaryDataItem(
            summaryScreenState
        )
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
                stopSelf()
            }
        }
        return true
    }
    inner class LocalBinder : Binder() {
        fun getService() = this@SyncService
    }
    companion object {
        private val UNBIND_DELAY = 3.seconds
    }
}
