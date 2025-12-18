package com.example.healthbuddy.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary

@Composable
fun HomeRunScreen(
    homeViewModel: HomeViewModel
) {
    val ui by homeViewModel.ui.collectAsState()

    LaunchedEffect(Unit) { homeViewModel.start() }
    DisposableEffect(Unit) { onDispose { homeViewModel.stop() } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Home",
                    color = AccentLime,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Running overview",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            LiveBadge(
                isLive = ui.timestampMillis != null
            )
        }

        Spacer(Modifier.height(14.dp))

        // Purple hero card (giống style summary card)
        RunningHeroCard(ui)

        Spacer(Modifier.height(12.dp))

        // Detail cards
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                title = "Heart rate",
                value = ui.averageHeartRate?.let { "$it" } ?: "--",
                unit = "bpm",
                subtitle = "Avg"
            )
            StatTile(
                title = "Calories",
                value = ui.totalCalories?.let { "$it" } ?: "--",
                unit = "kcal",
                subtitle = "Total"
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                title = "Distance",
                value = ui.totalDistanceMeters?.let { formatKm(it) } ?: "--",
                unit = "km",
                subtitle = "Total"
            )
            StatTile(
                title = "Duration",
                value = ui.durationMillis?.let { formatDuration(it) } ?: "--:--",
                unit = "",
                subtitle = "Time"
            )
        }

        Spacer(Modifier.height(14.dp))

        // Raw debug (collapsible feel)
        DebugCard(raw = ui.rawMessage, ts = ui.timestampMillis)
    }
}
@Composable
private fun LiveBadge(isLive: Boolean) {
    val dotColor = if (isLive) AccentLime else TextSecondary

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(SurfaceDark)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isLive) "LIVE" else "WAITING",
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RunningHeroCard(ui: RunStatsUi) {
    val distanceKm = ui.totalDistanceMeters?.let { it / 1000f } ?: 0f
    val title = if (ui.timestampMillis != null) "Workout from watch" else "No data yet"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(LavenderBand.copy(alpha = 0.35f))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))

        // Big highlight value
        Text(
            text = if (ui.totalDistanceMeters != null) "${"%.2f".format(distanceKm)} km" else "-- km",
            color = AccentLime,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(8.dp))

        // small row chips
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoPill(label = "Avg HR", value = ui.averageHeartRate?.let { "$it bpm" } ?: "--")
            InfoPill(label = "Calories", value = ui.totalCalories?.let { "$it kcal" } ?: "--")
            InfoPill(label = "Duration", value = ui.durationMillis?.let { formatDuration(it) } ?: "--:--")
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = ui.timestampMillis?.let { "Last sync: ${formatTime(it)}" } ?: "Waiting for watch…",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun InfoPill(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundDark.copy(alpha = 0.55f))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(label, color = TextSecondary, fontSize = 10.sp)
        Spacer(Modifier.height(2.dp))
        Text(value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    unit: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .padding(14.dp)
    ) {
        Text(title, color = TextSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            if (unit.isNotBlank()) {
                Spacer(Modifier.width(6.dp))
                Text(
                    text = unit,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(subtitle, color = AccentLime, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DebugCard(raw: String, ts: Long?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .padding(14.dp)
    ) {
        Text(
            text = "Debug payload",
            color = TextSecondary,
            fontSize = 12.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = raw,
            color = TextPrimary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = ts?.let { "timestamp=$it" } ?: "timestamp=--",
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}
private fun formatKm(meters: Int): String {
    val km = meters / 1000f
    return "%.2f".format(km)
}

private fun formatDuration(ms: Long): String {
    val totalSec = ms / 1000
    val m = (totalSec / 60).toInt()
    val s = (totalSec % 60).toInt()
    return "%02d:%02d".format(m, s)
}

private fun formatTime(timestampMs: Long): String {
    val instant = java.time.Instant.ofEpochMilli(timestampMs)
    val local = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
    return local.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm, dd MMM"))
}
