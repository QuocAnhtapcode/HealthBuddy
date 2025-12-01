package com.example.healthbuddy.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.data.model.QuizResponse
import com.example.healthbuddy.data.model.QuizSubmitRequest
import com.example.healthbuddy.data.model.UserAnswerOption
import com.example.healthbuddy.data.model.UserAnswerQuestion
import com.example.healthbuddy.data.repo.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val isLoading: Boolean = false,
    val quiz: QuizResponse? = null,
    val error: String? = null,

    val selectedOptions: Map<Long, Long> = emptyMap(), // questionId -> optionId

    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repo: QuizRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(QuizUiState())
    val ui = _ui.asStateFlow()

    fun loadQuiz() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)

            repo.getQuiz()
                .onSuccess { quiz ->
                    _ui.value = _ui.value.copy(
                        isLoading = false,
                        quiz = quiz,
                        error = null
                    )
                }
                .onFailure { e ->
                    _ui.value = _ui.value.copy(
                        isLoading = false,
                        error = e.message ?: "Lỗi khi tải quiz"
                    )
                }
        }
    }

    fun selectAnswer(questionId: Long, optionId: Long) {
        _ui.value = _ui.value.copy(
            selectedOptions = _ui.value.selectedOptions.toMutableMap().apply {
                this[questionId] = optionId
            }
        )
    }

    fun submitQuiz() {
        val quiz = _ui.value.quiz ?: return

        viewModelScope.launch {
            // build body theo selectedOptions
            val answerMap = _ui.value.selectedOptions

            val userAnswerQuestions = quiz.questions.mapNotNull { q ->
                val chosenOptionId = answerMap[q.id] ?: return@mapNotNull null

                UserAnswerQuestion(
                    questionId = q.id,
                    userAnswerOption = listOf(
                        UserAnswerOption(optionId = chosenOptionId)
                    )
                )
            }

            if (userAnswerQuestions.isEmpty()) {
                _ui.value = _ui.value.copy(
                    submitError = "Bạn chưa chọn câu trả lời nào"
                )
                return@launch
            }

            val body = QuizSubmitRequest(
                quizId = quiz.id,
                userAnswerQuestions = userAnswerQuestions
            )

            _ui.value = _ui.value.copy(
                isSubmitting = true,
                submitError = null,
                submitSuccess = false
            )

            repo.submitQuiz(body)
                .onSuccess {
                    _ui.value = _ui.value.copy(
                        isSubmitting = false,
                        submitSuccess = true
                    )
                }
                .onFailure { e ->
                    _ui.value = _ui.value.copy(
                        isSubmitting = false,
                        submitError = e.message ?: "Lỗi khi gửi quiz"
                    )
                }
        }
    }
}
