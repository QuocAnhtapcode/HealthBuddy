package com.example.healthbuddy.di

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import com.example.healthbuddy.service.AndroidLogExerciseLogger
import com.example.healthbuddy.service.ExerciseLogger
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

    /*
    @Singleton
    @Provides
    fun provideDataLayerClientManager(
        @ApplicationContext context: Context
    ): DataLayerClientManager = DataLayerClientManager(context)
     */
}
