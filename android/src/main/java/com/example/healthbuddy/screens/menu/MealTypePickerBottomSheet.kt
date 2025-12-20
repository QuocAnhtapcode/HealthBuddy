package com.example.healthbuddy.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary


enum class MealType {
    BREAKFAST, LUNCH, DINNER, SNACK
}

@Composable
fun MealTypePickerBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSelect: (MealType) -> Unit
) {
    if (!visible) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onDismiss()
            }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(SurfaceDark)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Gray.copy(alpha = 0.5f))
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Chọn bữa ăn",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            val options = listOf(
                MealType.BREAKFAST to "Bữa sáng",
                MealType.LUNCH     to "Bữa trưa",
                MealType.DINNER    to "Bữa tối",
                MealType.SNACK     to "Ăn vặt"
            )

            options.forEachIndexed { index, (type, title) ->
                MealTypeRow(
                    title = title,
                    type = type,
                    onClick = {
                        onSelect(type)
                        onDismiss()
                    }
                )

                if (index != options.lastIndex) {
                    Spacer(Modifier.height(10.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Hủy",
                color = AccentLime,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onDismiss() }
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun MealTypeRow(
    title: String,
    type: MealType,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundDark)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon tròn nhỏ
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(LavenderBand.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (type) {
                    MealType.BREAKFAST -> "S"
                    MealType.LUNCH     -> "Tr"
                    MealType.DINNER    -> "T"
                    MealType.SNACK     -> "V"
                },
                color = AccentLime,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = when (type) {
                    MealType.BREAKFAST -> "Bắt đầu ngày mới"
                    MealType.LUNCH     -> "Nạp năng lượng buổi trưa"
                    MealType.DINNER    -> "Kết thúc ngày"
                    MealType.SNACK     -> "Một chút bổ sung"
                },
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

