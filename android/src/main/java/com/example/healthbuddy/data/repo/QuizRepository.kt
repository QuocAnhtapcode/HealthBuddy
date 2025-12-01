package com.example.healthbuddy.data.repo

import com.example.healthbuddy.data.api.QuizApi
import com.example.healthbuddy.data.model.QuizResponse
import com.example.healthbuddy.data.model.QuizSubmitRequest
import javax.inject.Inject

class QuizRepository @Inject constructor(
    private val api: QuizApi
) {
    suspend fun getQuiz(): Result<QuizResponse> =
        runCatching { api.getQuiz() }

    suspend fun submitQuiz(body: QuizSubmitRequest): Result<Unit> =
        runCatching { api.submitQuiz(body) }
}

