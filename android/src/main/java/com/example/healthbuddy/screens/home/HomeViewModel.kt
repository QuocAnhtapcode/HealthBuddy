package com.example.healthbuddy.screens.home

import androidx.lifecycle.ViewModel
import com.example.healthbuddy.common.DataPaths
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RunStatsUi(
    val averageHeartRate: Int? = null,
    val totalDistanceMeters: Int? = null,
    val totalCalories: Int? = null,
    val durationMillis: Long? = null,
    val timestampMillis: Long? = null,
    val rawMessage: String = "Waiting..."
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataClient: DataClient,
    private val messageClient: MessageClient,
    private val capabilityClient: CapabilityClient
) : ViewModel(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    private val _ui = MutableStateFlow(RunStatsUi())
    val ui = _ui.asStateFlow()

    fun start() {
        messageClient.addListener(this)
        dataClient.addListener(this)
    }

    fun stop() {
        messageClient.removeListener(this)
        dataClient.removeListener(this)
    }

    override fun onMessageReceived(event: MessageEvent) {
        when (event.path) {
            DataPaths.EXERCISE_PATH -> {
                val text = event.data.decodeToString()
                _ui.update { it.copy(rawMessage = text) }
            }
        }
    }

    override fun onDataChanged(buffer: DataEventBuffer) {
        buffer.forEach { e ->
            if (e.type == DataEvent.TYPE_CHANGED &&
                e.dataItem.uri.path == DataPaths.EXERCISE_PATH
            ) {
                val map = DataMapItem.fromDataItem(e.dataItem).dataMap
                val hr = map.getInt("averageHeartRate")
                val dist = map.getInt("totalDistance")
                val cal = map.getInt("totalCalories")
                val dur = map.getLong("duration")
                val ts  = map.getLong("timestamp")

                _ui.value = RunStatsUi(
                    averageHeartRate = hr.takeIf { it > 0 },
                    totalDistanceMeters = dist.takeIf { it >= 0 },
                    totalCalories = cal.takeIf { it >= 0 },
                    durationMillis = dur.takeIf { it >= 0 },
                    timestampMillis = ts.takeIf { it > 0 },
                    rawMessage = "hr=$hr, dist=$dist, cal=$cal, dur=$dur, ts=$ts"
                )
            }
        }
    }

    override fun onCapabilityChanged(info: CapabilityInfo) {
        // Optional: update connected state later
    }

    override fun onCleared() {
        stop()
        super.onCleared()
    }
}
