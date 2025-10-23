package com.example.healthbuddy.data

import android.content.Context
import com.example.healthbuddy.common.DataPaths
import com.example.healthbuddy.presentation.summary.SummaryScreenState
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class DataLayerClientManager
@Inject
constructor(
    @ApplicationContext context: Context
){
    private val nodeClient: NodeClient = Wearable.getNodeClient(context)
    private val messageClient: MessageClient = Wearable.getMessageClient(context)
    private val dataClient: DataClient = Wearable.getDataClient(context)
    private val capabilityClient: CapabilityClient = Wearable.getCapabilityClient(context)

    suspend fun sendMessageToAll(path: String, payload: ByteArray){
        val nodes = nodeClient.connectedNodes.await()
        if(nodes.isEmpty()) return
        nodes.map { node ->
            messageClient.sendMessage(node.id,path,payload)
        }.let {
            Tasks.whenAllComplete(it).await()
        }
    }

    /** Put a DataItem that will sync reliably (auto-retry) when peer reconnects. */
    suspend fun sendSummaryDataItem(
        summaryScreenState: SummaryScreenState,
        sessionId: String = UUID.randomUUID().toString()
    ) {
        val request = PutDataMapRequest.create(DataPaths.EXERCISE_PATH).apply {
            dataMap.putInt("averageHeartRate", summaryScreenState.averageHeartRate.toInt())
            dataMap.putInt("totalDistance",summaryScreenState.totalDistance.toInt())
            dataMap.putInt("totalCalories",summaryScreenState.totalCalories.toInt())
            dataMap.putLong("duration",summaryScreenState.elapsedTime.toMillis())
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }.asPutDataRequest().setUrgent()

        dataClient.putDataItem(request).await()
    }

    suspend fun isCapabilityReachable(capability: String): Boolean{
        val info = capabilityClient.getCapability(
            capability,
            CapabilityClient.FILTER_REACHABLE
        ).await()
        return info.nodes.isNotEmpty()
    }

    /** Flow of capability changes (e.g., phone connects/disconnects). */
    fun capabilityFlow(capability: String): Flow<CapabilityInfo> = callbackFlow{
        val listener = CapabilityClient.OnCapabilityChangedListener{info->
            trySendBlocking(info)
        }
        capabilityClient.addListener(listener,capability)
        trySendBlocking(
            capabilityClient.getCapability(
                capability,
                CapabilityClient.FILTER_REACHABLE
            ).await()
        )
        awaitClose { capabilityClient.removeListener(listener) }
    }

    /** All incoming MessageEvents as a cold Flow. */
    fun messagesFlow(): Flow<MessageEvent> = callbackFlow {
        val listener = MessageClient.OnMessageReceivedListener { event ->
            trySendBlocking(event)
        }
        messageClient.addListener(listener)
        awaitClose { messageClient.removeListener(listener) }
    }

    /** All incoming DataEvents as a cold Flow. */
    fun dataEventsFlow(): Flow<DataEvent> = callbackFlow {
        val listener = DataClient.OnDataChangedListener { buffer ->
            buffer.forEach { trySendBlocking(it) }
        }
        dataClient.addListener(listener)
        awaitClose { dataClient.removeListener(listener) }
    }
}
