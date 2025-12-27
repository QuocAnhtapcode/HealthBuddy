package com.example.healthbuddy.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.healthbuddy.ui.theme.BackgroundDark
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onOpenRunHistory:() -> Unit
) {
    val ui by homeViewModel.ui.collectAsState()

    // load stats: lấy 7 ngày gần nhất
    LaunchedEffect(Unit) {
        homeViewModel.startListening()

        val end = LocalDate.now()
        val start = end.minusDays(6)
        val fmt = DateTimeFormatter.ISO_LOCAL_DATE

        homeViewModel.loadCaloriesStats(
            startDate = start.format(fmt),
            endDate = end.format(fmt)
        )

        homeViewModel.loadRunHistory()
        homeViewModel.applyRange()
        homeViewModel.loadHealthInfoHistory()
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
            HealthInfoLineChartCard(
                healthList = ui.healthInfos
            )
        }


        item {
            Spacer(Modifier.height(12.dp))
            LatestRunSection(
                latest = ui.latestRun,
                onOpenHistory = onOpenRunHistory
            )
        }
    }
}
