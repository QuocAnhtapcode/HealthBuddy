package com.example.healthbuddy.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.R
import com.example.healthbuddy.data.model.RunSession
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary
import java.util.concurrent.TimeUnit

@Composable
fun LatestRunSection(
    latest: RunSession?,
    onOpenHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_run),
                    contentDescription = null,
                    tint = AccentLime,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Lần chạy gần nhất",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            TextButton(onClick = onOpenHistory) {
                Text(
                    text = "Lịch sử",
                    color = AccentLime,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (latest == null) {
            EmptyRunState()
            return
        }

        val km = latest.totalDistanceMeters / 1000f
        val minutes = TimeUnit.MILLISECONDS.toMinutes(latest.durationMillis).toInt()

        Text(
            text = formatTimestamp(latest.timestampMillis),
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(12.dp))

        RunMetricRow(
            distanceKm = km,
            calories = latest.totalCalories,
            avgHr = latest.averageHeartRate,
            durationMin = minutes
        )
    }
}

@Composable
private fun RunMetricRow(
    distanceKm: Float,
    calories: Int,
    avgHr: Int,
    durationMin: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RunMetric("Quãng đường", String.format("%.2f km", distanceKm))
        RunMetric("Calories", "$calories kcal")
        RunMetric("HR TB", "$avgHr bpm")
        RunMetric("Thời gian", "$durationMin phút")
    }
}

@Composable
private fun RunMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun EmptyRunState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_star),
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Chưa có dữ liệu chạy",
            color = TextSecondary,
            fontSize = 13.sp
        )
        Text(
            text = "Hãy bắt đầu chạy với đồng hồ",
            color = TextSecondary.copy(alpha = 0.7f),
            fontSize = 11.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunHistoryScreen(
    homeViewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val ui by homeViewModel.ui.collectAsState()

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lịch sử chạy",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = null,
                            tint = AccentLime
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(BackgroundDark),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(
                items = ui.runHistory,
                key = { it.timestampMillis }
            ) { run ->
                RunHistoryItem(run)
            }

            if (ui.runHistory.isEmpty()) {
                item {
                    Spacer(Modifier.height(40.dp))
                    EmptyRunHistoryState()
                }
            }
        }
    }
}
@Composable
fun RunHistoryItem(run: RunSession) {
    val km = run.totalDistanceMeters / 1000f
    val minutes = TimeUnit.MILLISECONDS.toMinutes(run.durationMillis).toInt()

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_run),
            contentDescription = null,
            tint = AccentLime,
            modifier = Modifier.size(22.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatTimestamp(run.timestampMillis),
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${String.format("%.2f", km)} km · $minutes phút · ${run.totalCalories} kcal",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Text(
            text = "${run.averageHeartRate} bpm",
            color = AccentLime,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun EmptyRunHistoryState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_run),
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(36.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Chưa có buổi chạy nào",
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}


private fun formatTimestamp(timestampMillis: Long): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return java.time.Instant
        .ofEpochMilli(timestampMillis)
        .atZone(java.time.ZoneId.systemDefault())
        .format(formatter)
}

