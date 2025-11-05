package com.example.healthbuddy.di

import android.util.Log
import com.example.healthbuddy.data.local.TokenStore
import com.example.healthbuddy.screens.auth.AuthApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.net.InetAddress
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Connection
import okhttp3.EventListener
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideLogging(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }.apply { level = HttpLoggingInterceptor.Level.BODY }
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenStore: TokenStore): Interceptor =
        Interceptor{ chain ->
            val token = runBlocking {
                tokenStore.tokenFlow.firstOrNull()
            }
            val req = if(!token.isNullOrBlank()){
                chain.request().newBuilder()
                    .addHeader("Authorization","Bearer $token")
                    .build()
            } else chain.request()
            chain.proceed(req)
        }
    @Provides
    @Singleton
    fun provideOkHttp(
        logger: HttpLoggingInterceptor,
        auth: Interceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .eventListenerFactory { object : EventListener() {
            override fun dnsStart(call: Call, domainName: String) {
                Log.d("OkHttpEvt", "dnsStart $domainName")
            }
            override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
                Log.d("OkHttpEvt", "dnsEnd $domainName -> $inetAddressList")
            }
            override fun connectionAcquired(call: Call, connection: Connection) {
                Log.d("OkHttpEvt", "connAcquired ${connection.route()}")
            }
            override fun requestHeadersEnd(call: Call, request: Request) {
                Log.d("OkHttpEvt", "request -> ${request.method} ${request.url}")
            }
            override fun responseHeadersEnd(call: Call, response: Response) {
                Log.d("OkHttpEvt", "response <- ${response.code} ${response.request.url}")
            }
            override fun connectionReleased(call: Call, connection: Connection) {
                Log.d("OkHttpEvt", "connReleased ${connection.route()}")
            }
        } }
        .addInterceptor(auth)     // bearer-token interceptor
        .addInterceptor(logger)   // body logger last
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val moshi = Moshi.Builder().build()
    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/v1/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)
}
