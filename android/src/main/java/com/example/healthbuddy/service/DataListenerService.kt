package com.example.healthbuddy.service

import android.util.Log
import com.example.healthbuddy.common.DataPaths
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DataListenerService: WearableListenerService() {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )
    override fun onCreate() {
        super.onCreate()
    }

    override fun onMessageReceived(p0: MessageEvent) {
        super.onMessageReceived(p0)
        when(p0.path){
            DataPaths.EXERCISE_PATH -> {
                val message = p0.data.toString()
                scope.launch {
                    //Do something with the data here ( Push to server )
                }
                Log.d("Exercise Listener",message)
            }
        }
    }

    override fun onDataChanged(p0: DataEventBuffer) {
        super.onDataChanged(p0)
        p0.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED &&
                event.dataItem.uri.path == DataPaths.EXERCISE_PATH
            ) {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                dataMap.getInt("averageHeartRate")
                dataMap.getInt("totalDistance")
                dataMap.getInt("totalCalories")
                dataMap.getLong("duration")
                dataMap.getLong("timestamp")

                //Do something with the data here ( Push to server )
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}
