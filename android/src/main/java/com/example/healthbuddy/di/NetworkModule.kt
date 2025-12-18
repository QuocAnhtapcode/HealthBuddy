package com.example.healthbuddy.di

import android.util.Log
import com.example.healthbuddy.data.local.TokenStore
import com.example.healthbuddy.data.api.AuthApi
import com.example.healthbuddy.data.api.HomeApi
import com.example.healthbuddy.data.api.WorkOutApi
import com.example.healthbuddy.data.api.MenuApi
import com.example.healthbuddy.data.api.QuizApi
import com.example.healthbuddy.data.api.UserInfoApi
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
        Interceptor { chain ->

            val request = chain.request()
            val path = request.url.encodedPath

            // Skip token for auth endpoints
            val shouldSkipAuth =
                path.endsWith("/auth/login") ||
                    path.endsWith("/auth/register") ||
                    path.endsWith("/auth/refresh")

            if (shouldSkipAuth) {
                Log.d("AuthInterceptor", "Skip Authorization header for $path")
                return@Interceptor chain.proceed(request)
            }

            // Attach token for all other API
            val token = runBlocking { tokenStore.tokenFlow.firstOrNull() }

            val authedRequest =
                if (!token.isNullOrBlank()) {
                    Log.d("AuthInterceptor", "Attach Authorization Bearer token")
                    request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else request

            chain.proceed(authedRequest)
        }

    @Provides
    @Singleton
    fun provideOkHttp(
        logger: HttpLoggingInterceptor,
        auth: Interceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .eventListenerFactory {
                object : EventListener() {
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
                }
            }
            .addInterceptor(auth)      // Bearer Token Interceptor
            .addInterceptor(logger)    // Log body
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    private val moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            //.baseUrl("http://10.0.2.2:8000/api/") //Emulator -> Laptop
            .baseUrl("http://192.168.53.102:8000/api/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideUserProfileApi(retrofit: Retrofit): UserInfoApi =
        retrofit.create(UserInfoApi::class.java)

    @Provides
    @Singleton
    fun provideQuizApi(retrofit: Retrofit): QuizApi =
        retrofit.create(QuizApi::class.java)

    @Provides
    @Singleton
    fun provideGoalApi(retrofit: Retrofit): WorkOutApi =
        retrofit.create(WorkOutApi::class.java)

    @Provides
    @Singleton
    fun provideMenuApi(retrofit: Retrofit): MenuApi =
        retrofit.create(MenuApi::class.java)

    @Provides
    @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApi =
        retrofit.create(HomeApi::class.java)

}
