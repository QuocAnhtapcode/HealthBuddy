package com.example.healthbuddy.di

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import com.example.healthbuddy.data.DataLayerClientManager
import com.example.healthbuddy.service.AndroidLogExerciseLogger
import com.example.healthbuddy.service.ExerciseLogger
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Hilt module that provides singleton (application-scoped) objects.
 */
@Module
@InstallIn(SingletonComponent::class)
class MainModule {
    @Singleton
    @Provides
    fun provideApplicationCoroutineScope(): CoroutineScope =
        CoroutineScope(
            SupervisorJob() + Dispatchers.Default
        )

    @Singleton
    @Provides
    fun provideHealthServicesClient(
        @ApplicationContext context: Context
    ): HealthServicesClient = HealthServices.getClient(context)

    @Singleton
    @Provides
    fun provideLogger(): ExerciseLogger = AndroidLogExerciseLogger()

    @Singleton
    @Provides
    fun provideDataLayerClientManager(
        @ApplicationContext context: Context
    ): DataLayerClientManager = DataLayerClientManager(context)

    @Provides
    @Singleton
    fun provideDataClient(
        @ApplicationContext context: Context
    ): DataClient {
        return Wearable.getDataClient(context)
    }

    @Provides
    @Singleton
    fun provideMessageClient(
        @ApplicationContext context: Context
    ): MessageClient {
        return Wearable.getMessageClient(context)
    }

    @Provides
    @Singleton
    fun provideCapabilityClient(
        @ApplicationContext context: Context
    ): CapabilityClient {
        return Wearable.getCapabilityClient(context)
    }
}
