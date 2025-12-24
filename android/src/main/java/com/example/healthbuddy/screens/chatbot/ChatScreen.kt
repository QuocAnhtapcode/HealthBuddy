package com.example.healthbuddy.screens.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.R
import com.example.healthbuddy.data.model.ChatHistoryItem
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.ButtonBg
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatBotViewModel,
    onBack: () -> Unit,
    onOpenMenuDetail: (chatId: Long) -> Unit
) {
    val ui by viewModel.ui.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    // Auto scroll xuống cuối khi có tin nhắn mới / hoặc đang gửi
    LaunchedEffect(ui.history.size, ui.sending) {
        val lastIndex = (ui.history.size * 2 - 1).coerceAtLeast(0)
        if (lastIndex > 0) {
            listState.animateScrollToItem(lastIndex)
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ChatBot gợi ý menu",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Quay lại",
                            tint = AccentLime
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        bottomBar = {
            ChatInputBar(
                value = ui.input,
                enabled = !ui.sending,
                onValueChange = viewModel::setInput,
                onSend = { viewModel.sendMessage() }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            if (ui.error != null) {
                ErrorPill(
                    text = ui.error ?: "",
                    onDismiss = viewModel::clearError
                )
                Spacer(Modifier.height(8.dp))
            }

            if (ui.loadingHistory) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentLime)
                }
                return@Column
            }

            if (ui.history.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có lịch sử. Hãy chat để tạo menu!", color = TextSecondary)
                }
                return@Column
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Render mỗi item thành 2 bubble (User -> Bot)
                ui.history.forEach { item ->
                    item(key = "u_${item.id}") {
                        UserBubble(
                            text = extractUserPrompt(item.userPrompt),
                        )
                    }

                    item(key = "b_${item.id}") {
                        BotBubble(
                            item = item,
                            sending = ui.sending,
                            onOpenMenu = { onOpenMenuDetail(item.id) }
                        )
                    }
                }

                // typing indicator khi đang gửi
                if (ui.sending) {
                    item(key = "typing") {
                        BotTypingBubble()
                    }
                }
            }
        }
    }
}

/** Bubble của user (bên phải) */
@Composable
private fun UserBubble(
    text: String
) {
    ChatRow(isUser = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 6.dp // góc “đuôi” bên phải
                    )
                )
                .background(AccentLime.copy(alpha = 0.16f))
                .border(
                    width = 1.dp,
                    color = AccentLime.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 6.dp
                    )
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Bạn",
                color = AccentLime,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

/** Bubble của bot (bên trái) */
@Composable
private fun BotBubble(
    item: ChatHistoryItem,
    sending: Boolean,
    onOpenMenu: () -> Unit
) {
    val preview = extractNotesOrShortSummary(item.aiResponse)

    ChatRow(isUser = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.86f)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = 6.dp,  // “đuôi” bên trái
                        bottomEnd = 18.dp
                    )
                )
                .background(SurfaceDark)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(LavenderBand)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "AI",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                // chip nhỏ để người dùng hiểu đây là menu gợi ý
                Text(
                    text = "MENU #${item.id}",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = preview,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(10.dp))

            // CTA gọn đẹp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onOpenMenu,
                    enabled = !sending,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBg),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Xem menu", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun BotTypingBubble() {
    ChatRow(isUser = false) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = AccentLime,
                strokeWidth = 2.dp
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "AI đang soạn menu…",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ChatRow(
    isUser: Boolean,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        content()
    }
}

/** Backend trả userPrompt là JSON string -> extract ra câu người dùng */
private fun extractUserPrompt(raw: String): String {
    // Ví dụ raw: "{\r\n  \"userPrompt\": \"Hôm nay...\"\r\n}"
    val cleaned = raw.replace("\r", "").trim()

    // Regex bắt nội dung "userPrompt": "...."
    val regex = Regex("\"userPrompt\"\\s*:\\s*\"(.*?)\"", RegexOption.DOT_MATCHES_ALL)
    val match = regex.find(cleaned)?.groupValues?.getOrNull(1)

    val text = match ?: cleaned
    return text
        .replace("\\n", "\n")
        .replace("\\t", " ")
        .replace("\\\"", "\"")
        .trim()
        .ifBlank { "..." }
}

/** AI response đang là JSON trong ``` ``` -> chỉ lấy notes hoặc tóm tắt nhẹ */
private fun extractNotesOrShortSummary(aiResponse: String): String {
    // Ưu tiên tìm "notes": "..."
    val notesRegex = Regex("\"notes\"\\s*:\\s*\"(.*?)\"", RegexOption.DOT_MATCHES_ALL)
    val notes = notesRegex.find(aiResponse)?.groupValues?.getOrNull(1)

    val text = (notes ?: "Mình đã tạo một thực đơn phù hợp. Bạn có thể bấm “Xem menu” để xem chi tiết.")
    return text
        .replace("\\n", "\n")
        .replace("\\\"", "\"")
        .trim()
}


@Composable
private fun ChatInputBar(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(color = SurfaceDark) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                placeholder = { Text("Nhập gợi ý…", color = TextSecondary) },
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = BackgroundDark,
                    unfocusedContainerColor = BackgroundDark,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = AccentLime,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 52.dp)
            )

            Spacer(Modifier.width(10.dp))

            IconButton(
                onClick = onSend,
                enabled = enabled && value.trim().isNotEmpty(),
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(AccentLime)
            ) {
                if (!enabled) {
                    CircularProgressIndicator(
                        color = BackgroundDark,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(Icons.Default.Send, null, tint = BackgroundDark)
                }
            }
        }
    }
}

@Composable
fun ErrorPill(text: String, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Red.copy(alpha = 0.18f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, color = Color(0xFFFFC9C9), fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(
            text = "Ẩn",
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(onClick = onDismiss)
        )
    }
}
