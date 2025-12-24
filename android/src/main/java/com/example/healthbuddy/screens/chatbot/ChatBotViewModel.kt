package com.example.healthbuddy.screens.chatbot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.data.model.ChatHistoryItem
import com.example.healthbuddy.data.model.ChatMenuPreview
import com.example.healthbuddy.data.model.Menu
import com.example.healthbuddy.data.repo.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatBotUiState(
    val loadingHistory: Boolean = false,
    val history: List<ChatHistoryItem> = emptyList(),

    val sending: Boolean = false,
    val input: String = "",

    val selectedChatId: Long? = null,
    val loadingMenuPreview: Boolean = false,
    val menuPreview: ChatMenuPreview? = null,

    val savingMenu: Boolean = false,

    val error: String? = null
)

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val repo: MenuRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ChatBotUiState())
    val ui = _ui.asStateFlow()

    // ---------- Input ----------
    fun setInput(text: String) {
        _ui.update { it.copy(input = text) }
    }

    fun clearError() {
        _ui.update { it.copy(error = null) }
    }

    fun loadHistory(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _ui.update { it.copy(loadingHistory = true, error = null) }
            } else {
                _ui.update { it.copy(error = null) }
            }

            repo.getChatHistory()
                .onSuccess { res ->
                    _ui.update {
                        it.copy(
                            loadingHistory = false,
                            history = res.content.reversed(),
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingHistory = false,
                            error = e.message ?: "Load chat history failed"
                        )
                    }
                }
        }
    }

    fun sendMessage() {
        val msg = _ui.value.input.trim()
        if (msg.isBlank()) return

        viewModelScope.launch {
            _ui.update { it.copy(sending = true, error = null) }

            repo.sendMessageToChatBot(msg)
                .onSuccess { item ->
                    val chatId = item.id

                    _ui.update {
                        it.copy(
                            sending = false,
                            input = "",
                            selectedChatId = chatId,
                            error = null
                        )
                    }

                    // refresh history mượt (không bật loadingHistory)
                    loadHistory(showLoading = false)
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            sending = false,
                            error = e.message ?: "Send message failed"
                        )
                    }
                }
        }
    }


    // ---------- 3) Click 1 chat -> load menu preview ----------
    fun selectChat(chatId: Long) {
        _ui.update { it.copy(selectedChatId = chatId) }
        loadMenuFromChat(chatId)
    }

    fun loadMenuFromChat(chatId: Long) {
        viewModelScope.launch {
            _ui.update {
                it.copy(
                    selectedChatId = chatId,
                    loadingMenuPreview = true,
                    menuPreview = null,
                    error = null
                )
            }

            repo.getMenuFromChat(chatId)
                .onSuccess { menu ->
                    _ui.update {
                        it.copy(
                            loadingMenuPreview = false,
                            menuPreview = menu,
                            error = null
                        )
                    }
                    Log.d("ChatBotViewModel",menu.toString())
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingMenuPreview = false,
                            error = e.message ?: "Load chat menu failed"
                        )
                    }
                    Log.d("ChatBotViewModel","Fail")
                }
        }
    }

    // ---------- 4) Save menu preview -> menu hôm nay ----------
    fun saveSelectedMenu(onDone: () -> Unit = {}) {
        val chatId = _ui.value.selectedChatId ?: return

        viewModelScope.launch {
            _ui.update { it.copy(savingMenu = true, error = null) }

            repo.saveChatMenu(chatId)
                .onSuccess {
                    _ui.update { it.copy(savingMenu = false, error = null) }
                    onDone()
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            savingMenu = false,
                            error = e.message ?: "Save menu failed"
                        )
                    }
                }
        }
    }

    fun clearPreview() {
        _ui.update { it.copy(selectedChatId = null, menuPreview = null) }
    }
}
