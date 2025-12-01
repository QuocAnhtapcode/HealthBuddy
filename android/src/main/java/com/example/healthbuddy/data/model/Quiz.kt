package com.example.healthbuddy.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuizResponse(
    val id: Long,
    val title: String,
    val questions: List<QuizQuestion>
)

@JsonClass(generateAdapter = true)
data class QuizQuestion(
    val id: Long,
    val title: String,
    val options: List<QuizOption>
)

@JsonClass(generateAdapter = true)
data class QuizOption(
    val id: Long,
    val text: String,
    val point: Int
)

@JsonClass(generateAdapter = true)
data class QuizSubmitRequest(
    val quizId: Long,
    val userAnswerQuestions: List<UserAnswerQuestion>
)

@JsonClass(generateAdapter = true)
data class UserAnswerQuestion(
    val questionId: Long,
    val userAnswerOption: List<UserAnswerOption>
)

@JsonClass(generateAdapter = true)
data class UserAnswerOption(
    val optionId: Long
)
