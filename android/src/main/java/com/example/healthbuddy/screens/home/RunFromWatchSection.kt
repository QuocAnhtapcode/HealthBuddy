package com.example.healthbuddy.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary
import java.util.concurrent.TimeUnit

@Composable
fun RunFromWatchSection(latest: RunSession?) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(LavenderBand)
            .padding(16.dp)
    ) {
        Text(
            text = "Dữ liệu từ đồng hồ thông minh",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        if (latest == null) {
            Text(
                text = "Chờ dữ liệu từ đồng hồ.....",
                color = TextPrimary.copy(alpha = 0.8f),
                fontSize = 13.sp
            )
        } else {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(latest.durationMillis).toInt()
            val km = latest.totalDistanceMeters / 1000f

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Nhịp tim trung bình", color = TextPrimary.copy(alpha = 0.8f), fontSize = 12.sp)
                Text("${latest.averageHeartRate} bpm", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Quãng đường", color = TextPrimary.copy(alpha = 0.8f), fontSize = 12.sp)
                Text(String.format("%.2f km", km), color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Calo tiêu thụ", color = TextPrimary.copy(alpha = 0.8f), fontSize = 12.sp)
                Text("${latest.totalCalories} kcal", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Thời gian chạy", color = TextPrimary.copy(alpha = 0.8f), fontSize = 12.sp)
                Text("$minutes phúc", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RunSessionCard(run: RunSession) {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(run.durationMillis).toInt()
    val km = run.totalDistanceMeters / 1000f

    val runTimeText = remember(run.timestampMillis) {
        formatTimestamp(run.timestampMillis)
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .padding(14.dp)
    ) {
        Text(
            text = "Run",
            color = AccentLime,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Thời gian", color = TextSecondary, fontSize = 12.sp)
            Text(runTimeText, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Quãng đường", color = TextSecondary, fontSize = 12.sp)
            Text(String.format("%.2f km", km), color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Nhịp tim trung bình", color = TextSecondary, fontSize = 12.sp)
            Text("${run.averageHeartRate} bpm", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Calo tiêu thụ", color = TextSecondary, fontSize = 12.sp)
            Text("${run.totalCalories} kcal", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Thời gian chạy", color = TextSecondary, fontSize = 12.sp)
            Text("$minutes phút", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun formatTimestamp(timestampMillis: Long): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return java.time.Instant
        .ofEpochMilli(timestampMillis)
        .atZone(java.time.ZoneId.systemDefault())
        .format(formatter)
}

