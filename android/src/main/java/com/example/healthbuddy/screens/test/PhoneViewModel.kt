package com.example.healthbuddy.screens.test

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.healthbuddy.common.DataPaths
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PhoneViewModel(application: Application):
    AndroidViewModel(application),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener
{
    private val dataClient by lazy { Wearable.getDataClient(application) }
    private val messageClient by lazy { Wearable.getMessageClient(application) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(application) }

    private val _message = MutableStateFlow("Waiting...")
    val message = _message.asStateFlow()
    fun start(){
        messageClient.addListener(this)
        dataClient.addListener(this)
    }
    fun stop(){
        messageClient.removeListener(this)
        dataClient.removeListener(this)
    }
    override fun onMessageReceived(p0: MessageEvent) {
        Log.d("PhoneVM", "onMessageReceived path=${p0.path} bytes=${p0.data.size}")
        when(p0.path){
            DataPaths.EXERCISE_PATH -> {
                _message.value = p0.data.decodeToString()
            }
        }
    }

    override fun onDataChanged(p0: DataEventBuffer) {
        p0.forEach { event ->
            if(event.type == DataEvent.TYPE_CHANGED &&
                event.dataItem.uri.path == DataPaths.EXERCISE_PATH
            ){
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val avgHr = dataMap.getInt("averageHeartRate")
                val distance = dataMap.getInt("totalDistance")
                val calories = dataMap.getInt("totalCalories")
                val duration = dataMap.getLong("duration")
                val timeStamp = dataMap.getLong("timestamp")
                _message.value = _message.value + dataMap.toString()
            }
        }
    }

    override fun onCapabilityChanged(p0: CapabilityInfo) {
        TODO("Not yet implemented")
    }
}
