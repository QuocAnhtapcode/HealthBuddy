package com.example.healthbuddy.data.api

import com.example.healthbuddy.data.model.QuizResponse
import com.example.healthbuddy.data.model.QuizSubmitRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface QuizApi {

    @GET("v1/quiz")
    suspend fun getQuiz(
    ): QuizResponse

    @POST("v1/quiz/submit")
    suspend fun submitQuiz(
        @Body body: QuizSubmitRequest
    )
}
