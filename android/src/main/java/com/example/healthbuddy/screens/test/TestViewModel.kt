package com.example.healthbuddy.screens.test

import android.util.Log
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

@HiltViewModel
class TestViewModel @Inject constructor(
    private val dataClient: DataClient,
    private val messageClient: MessageClient,
    private val capabilityClient: CapabilityClient
) : ViewModel(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    private val _message = MutableStateFlow("Waiting...")
    val message = _message.asStateFlow()

    fun start() {
        messageClient.addListener(this)
        dataClient.addListener(this)
        // capabilityClient.addListener(this, YOUR_CAPABILITY)
    }

    fun stop() {
        messageClient.removeListener(this)
        dataClient.removeListener(this)
        // capabilityClient.removeListener(this)
    }

    override fun onMessageReceived(event: MessageEvent) {
        Log.d("PhoneVM", "onMessageReceived path=${event.path} bytes=${event.data.size}")
        when (event.path) {
            DataPaths.EXERCISE_PATH -> _message.value = event.data.decodeToString()
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
                _message.value = "hr=$hr, dist=$dist, cal=$cal, dur=$dur, ts=$ts"
            }
        }
    }

    override fun onCapabilityChanged(info: CapabilityInfo) {
        Log.d("PhoneVM", "onCapabilityChanged: ${info.name} nodes=${info.nodes}")
    }
}

