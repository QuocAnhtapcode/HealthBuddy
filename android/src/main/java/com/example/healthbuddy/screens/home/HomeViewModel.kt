package com.example.healthbuddy.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.common.DataPaths
import com.example.healthbuddy.data.model.CaloriesStat
import com.example.healthbuddy.data.model.RunSession
import com.example.healthbuddy.data.repo.HomeRepository
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
import kotlinx.coroutines.launch

enum class CaloriesChartMode { LINE_EATEN_BURNED, BAR_MACROS, BAR_NET }

data class CaloriesRange(
    val startDate: String, // yyyy-MM-dd
    val endDate: String
)

data class HomeUiState(
    val loadingStats: Boolean = false,
    val caloriesStats: List<CaloriesStat> = emptyList(),
    val statsError: String? = null,
    val range: CaloriesRange = CaloriesRange(
        startDate = java.time.LocalDate.now().minusDays(6).toString(),
        endDate = java.time.LocalDate.now().toString()
    ),
    val chartMode: CaloriesChartMode = CaloriesChartMode.LINE_EATEN_BURNED,

    // watch
    val latestRun: RunSession? = null,
    val runHistory: List<RunSession> = emptyList()
)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val dataClient: DataClient,
    private val messageClient: MessageClient,
    private val capabilityClient: CapabilityClient
) : ViewModel(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    private val _ui = MutableStateFlow(HomeUiState())
    val ui = _ui.asStateFlow()

    fun startListening() {
        messageClient.addListener(this)
        dataClient.addListener(this)
    }

    fun stopListening() {
        messageClient.removeListener(this)
        dataClient.removeListener(this)
    }

    fun loadCaloriesStats(startDate: String, endDate: String) {
        viewModelScope.launch {
            _ui.update { it.copy(loadingStats = true, statsError = null) }

            repository.getCaloriesStat(startDate, endDate)
                .onSuccess { list ->
                    _ui.update { it.copy(loadingStats = false, caloriesStats = list, statsError = null) }
                }
                .onFailure { e ->
                    _ui.update { it.copy(loadingStats = false, statsError = e.message ?: "Load stats failed") }
                }
        }
    }

    override fun onMessageReceived(event: MessageEvent) {
        // nếu bạn có path riêng thì xử lý tương tự DataMap (khuyến nghị dùng DataItem)
    }

    override fun onDataChanged(buffer: DataEventBuffer) {
        buffer.forEach { e ->
            if (e.type == DataEvent.TYPE_CHANGED && e.dataItem.uri.path == DataPaths.EXERCISE_PATH) {
                val map = DataMapItem.fromDataItem(e.dataItem).dataMap
                val session = RunSession(
                    averageHeartRate = map.getInt("averageHeartRate"),
                    totalDistanceMeters = map.getInt("totalDistance"),
                    totalCalories = map.getInt("totalCalories"),
                    durationMillis = map.getLong("duration"),
                    timestampMillis = map.getLong("timestamp")
                )

                _ui.update { old ->
                    val newHistory = (listOf(session) + old.runHistory)
                        .distinctBy { it.timestampMillis }   // tránh trùng
                        .take(30)
                    old.copy(latestRun = session, runHistory = newHistory)
                }
            }
        }
    }

    override fun onCapabilityChanged(info: CapabilityInfo) {}

    fun setRange(startDate: String, endDate: String) {
        _ui.update { it.copy(range = CaloriesRange(startDate, endDate)) }
    }

    fun setChartMode(mode: CaloriesChartMode) {
        _ui.update { it.copy(chartMode = mode) }
    }

    fun applyRange() {
        val r = _ui.value.range
        loadCaloriesStats(r.startDate, r.endDate)
    }

}
