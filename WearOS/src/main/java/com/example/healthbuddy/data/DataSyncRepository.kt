package com.example.healthbuddy.data

import android.content.Context
import com.example.healthbuddy.di.bindService
import com.example.healthbuddy.presentation.summary.SummaryScreenState
import com.example.healthbuddy.service.SyncService
import dagger.hilt.android.ActivityRetainedLifecycle
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ActivityRetainedScoped
class DataSyncRepository
@Inject
constructor(
    @ApplicationContext private val applicationContext: Context,
    val coroutineScope: CoroutineScope,
    val lifecycle: ActivityRetainedLifecycle
){
    private val binderConnection =
        lifecycle.bindService<SyncService.LocalBinder, SyncService>(applicationContext)

    private fun serviceCall(function: suspend SyncService.() -> Unit) =
        coroutineScope.launch {
            binderConnection.runWhenConnected {
                function(it.getService())
            }
        }
    fun sendExerciseSummary(summaryScreenState: SummaryScreenState) =
        serviceCall { sendExerciseSummary(summaryScreenState) }
}
