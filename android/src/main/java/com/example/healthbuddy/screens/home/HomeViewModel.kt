package com.example.healthbuddy.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.common.DataPaths
import com.example.healthbuddy.data.model.CaloriesStat
import com.example.healthbuddy.data.model.CreateRunSessionRequest
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
    val startDate: String,
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

    // watch -> server
    val latestRun: RunSession? = null,
    val runHistory: List<RunSession> = emptyList(),
    val loadingRunHistory: Boolean = false,
    val uploadingRun: Boolean = false,
    val runError: String? = null
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

    // chống gửi trùng (DataItem đôi khi bắn lại)
    private val sentTimestamps = mutableSetOf<Long>()

    fun startListening() {
        messageClient.addListener(this)
        dataClient.addListener(this)
    }

    fun stopListening() {
        messageClient.removeListener(this)
        dataClient.removeListener(this)
    }

    /** gọi khi vào Home để có lịch sử từ server */
    fun loadRunHistory() {
        viewModelScope.launch {
            _ui.update { it.copy(loadingRunHistory = true, runError = null) }

            repository.getRunSessions()
                .onSuccess { list ->
                    // sort mới nhất lên đầu (nếu server chưa sort)
                    val sorted = list.sortedByDescending { it.timestampMillis }
                    sorted.forEach { sentTimestamps.add(it.timestampMillis) }

                    _ui.update {
                        it.copy(
                            loadingRunHistory = false,
                            runHistory = sorted,
                            latestRun = sorted.firstOrNull(),
                            runError = null
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingRunHistory = false,
                            runError = e.message ?: "Load run sessions failed"
                        )
                    }
                }
        }
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
        // Nếu sau này bạn dùng MessageClient thay DataItem thì parse ở đây
        // Hiện tại bạn đang dùng DataItem là hợp lý hơn.
    }

    override fun onDataChanged(buffer: DataEventBuffer) {
        buffer.forEach { e ->
            if (e.type != DataEvent.TYPE_CHANGED) return@forEach
            if (e.dataItem.uri.path != DataPaths.EXERCISE_PATH) return@forEach

            val map = DataMapItem.fromDataItem(e.dataItem).dataMap

            val avgHr = map.getInt("averageHeartRate")
            val dist = map.getInt("totalDistance")
            val cal = map.getInt("totalCalories")
            val dur = map.getLong("duration")
            val ts = map.getLong("timestamp")

            // validate cơ bản
            if (ts <= 0L || dur <= 0L) return@forEach

            // chặn trùng ngay tại đây
            if (sentTimestamps.contains(ts)) return@forEach
            sentTimestamps.add(ts)

            val req = CreateRunSessionRequest(
                averageHeartRate = avgHr,
                totalDistanceMeters = dist,
                totalCalories = cal,
                durationMillis = dur,
                timestampMillis = ts
            )

            createRunSessionToServer(req)
        }
    }

    private fun createRunSessionToServer(req: CreateRunSessionRequest) {
        viewModelScope.launch {
            _ui.update { it.copy(uploadingRun = true, runError = null) }

            repository.createRunSession(req)
                .onSuccess { created ->
                    _ui.update { old ->
                        val newHistory = (listOf(created) + old.runHistory)
                            .distinctBy { it.timestampMillis }
                            .sortedByDescending { it.timestampMillis }
                            .take(50)

                        old.copy(
                            uploadingRun = false,
                            latestRun = created,
                            runHistory = newHistory,
                            runError = null
                        )
                    }
                }
                .onFailure { e ->
                    // nếu fail thì cho phép gửi lại lần sau bằng cách remove khỏi sentTimestamps
                    sentTimestamps.remove(req.timestampMillis)

                    _ui.update {
                        it.copy(
                            uploadingRun = false,
                            runError = e.message ?: "Upload run session failed"
                        )
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

    fun clearRunError() {
        _ui.update { it.copy(runError = null) }
    }
}
