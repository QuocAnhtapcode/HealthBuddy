package com.example.healthbuddy.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.TextPrimary
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeRunScreen(homeViewModel: HomeViewModel) {
    val ui by homeViewModel.ui.collectAsState()

    // load stats: ví dụ lấy 7 ngày gần nhất
    LaunchedEffect(Unit) {
        homeViewModel.startListening()

        val end = LocalDate.now()
        val start = end.minusDays(6)
        val fmt = DateTimeFormatter.ISO_LOCAL_DATE

        homeViewModel.loadCaloriesStats(
            startDate = start.format(fmt),
            endDate = end.format(fmt)
        )
    }

    DisposableEffect(Unit) {
        onDispose { homeViewModel.stopListening() }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(bottom = 92.dp)
    ) {
        item {
            val ui by homeViewModel.ui.collectAsState()

            CaloriesChartsSection(
                ui = ui,
                onStartDateChange = { homeViewModel.setRange(it, ui.range.endDate) },
                onEndDateChange = { homeViewModel.setRange(ui.range.startDate, it) },
                onApply = { homeViewModel.applyRange() },
                onModeChange = { homeViewModel.setChartMode(it) }
            )

        }

        item {
            Spacer(Modifier.height(12.dp))
            RunFromWatchSection(latest = ui.latestRun)
        }

        item {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Lịch sử vận động từ đồng hồ",
                color = AccentLime,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        items(ui.runHistory, key = { it.timestampMillis }) { run ->
            RunSessionCard(run)
            Spacer(Modifier.height(10.dp))
        }

        item { Spacer(Modifier.height(18.dp)) }
    }
}
