package com.example.healthbuddy.presentation.home

import androidx.lifecycle.ViewModel
import com.example.healthbuddy.common.DataPaths
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@JsonClass(generateAdapter = true)
data class WatchCaloriesStat(
    val date: String,    // MM-dd
    val burned: Float,
    val eaten: Float,
    val net: Float
)

@HiltViewModel
class WatchHomeViewModel @Inject
constructor(
    private val dataClient: DataClient
) : ViewModel(), DataClient.OnDataChangedListener {

    private val _stats = MutableStateFlow<List<WatchCaloriesStat>>(emptyList())
    val stats = _stats.asStateFlow()

    private val moshi = Moshi.Builder().build()
    private val adapter = moshi.adapter<List<WatchCaloriesStat>>(
        Types.newParameterizedType(List::class.java, WatchCaloriesStat::class.java)
    )

    fun startListening() {
        dataClient.addListener(this)
    }

    fun stopListening() {
        dataClient.removeListener(this)
    }

    override fun onDataChanged(buffer: DataEventBuffer) {
        buffer.forEach { event ->
            if (event.type != DataEvent.TYPE_CHANGED) return@forEach
            if (event.dataItem.uri.path != DataPaths.CALORIES_7DAYS) return@forEach

            val map = DataMapItem.fromDataItem(event.dataItem).dataMap
            val json = map.getString("payload") ?: return@forEach

            runCatching {
                adapter.fromJson(json)
            }.onSuccess { list ->
                if (!list.isNullOrEmpty()) {
                    _stats.value = list
                }
            }
        }
    }
}

