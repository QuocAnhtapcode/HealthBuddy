package com.example.healthbuddy.di

import android.content.Context
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object WearClientsModule {

    @Provides
    fun provideDataClient(@ApplicationContext ctx: Context): DataClient =
        Wearable.getDataClient(ctx)

    @Provides
    fun provideMessageClient(@ApplicationContext ctx: Context): MessageClient =
        Wearable.getMessageClient(ctx)

    @Provides
    fun provideCapabilityClient(@ApplicationContext ctx: Context): CapabilityClient =
        Wearable.getCapabilityClient(ctx)
}
