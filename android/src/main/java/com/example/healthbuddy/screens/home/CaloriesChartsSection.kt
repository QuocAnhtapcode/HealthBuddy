package com.example.healthbuddy.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.data.model.CaloriesStat
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.ButtonBg
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary
import kotlin.math.max

@Composable
fun CaloriesChartsSection(
    ui: HomeUiState,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onApply: () -> Unit,
    onModeChange: (CaloriesChartMode) -> Unit,
    onRetry: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Calories charts",
            color = AccentLime,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))

        // --- Date range card ---
        DateRangeCard(
            startDate = ui.range.startDate,
            endDate = ui.range.endDate,
            onStartDateChange = onStartDateChange,
            onEndDateChange = onEndDateChange,
            onApply = onApply
        )

        Spacer(Modifier.height(10.dp))

        // --- Mode tabs ---
        ChartModeTabs(
            selected = ui.chartMode,
            onSelect = onModeChange
        )

        Spacer(Modifier.height(10.dp))

        when (ui.chartMode) {
            CaloriesChartMode.LINE_EATEN_BURNED ->
                CaloriesLineChartCard(ui.caloriesStats)

            CaloriesChartMode.BAR_MACROS ->
                MacrosStackedBarChartCard(ui.caloriesStats)

            CaloriesChartMode.BAR_NET ->
                NetCaloriesBarChartCard(ui.caloriesStats)
        }
        /*
        when {
            ui.loadingStats -> LoadingCard()
            ui.statsError != null -> ErrorCard(ui.statsError, onRetry)
            ui.caloriesStats.isEmpty() -> EmptyCard("No stats in this range.")
            else -> {
                when (ui.chartMode) {
                    CaloriesChartMode.LINE_EATEN_BURNED ->
                        CaloriesLineChartCard(ui.caloriesStats)

                    CaloriesChartMode.BAR_MACROS ->
                        MacrosStackedBarChartCard(ui.caloriesStats)

                    CaloriesChartMode.BAR_NET ->
                        NetCaloriesBarChartCard(ui.caloriesStats)
                }
            }
        }
         */
    }
}

@Composable
private fun CaloriesLineChartCard(stats: List<CaloriesStat>) {
    val eaten = stats.map { it.eatenCalories }
    val burned = stats.map { it.burnedCalories }

    val maxValue = max(
        eaten.maxOrNull() ?: 0f,
        burned.maxOrNull() ?: 0f
    ).coerceAtLeast(1f)

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        Text(
            text = "Eaten vs Burned",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(10.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val w = size.width
            val h = size.height
            val n = stats.size.coerceAtLeast(2)

            fun y(v: Float): Float = h - (v / maxValue) * (h * 0.9f) - (h * 0.05f)
            fun x(i: Int): Float = (w / (n - 1)) * i

            // grid baseline
            drawLine(
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.08f),
                start = Offset(0f, h * 0.95f),
                end = Offset(w, h * 0.95f),
                strokeWidth = 2f
            )

            // eaten path (AccentLime)
            val eatenPath = Path().apply {
                eaten.forEachIndexed { i, v ->
                    val p = Offset(x(i), y(v))
                    if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
            }
            drawPath(
                path = eatenPath,
                color = AccentLime,
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            // burned path (LavenderBand)
            val burnedPath = Path().apply {
                burned.forEachIndexed { i, v ->
                    val p = Offset(x(i), y(v))
                    if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
            }
            drawPath(
                path = burnedPath,
                color = LavenderBand,
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )
        }

        Spacer(Modifier.height(10.dp))

        // legend
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendDot(color = AccentLime, label = "Eaten")
            LegendDot(color = LavenderBand, label = "Burned")
        }
    }
}

@Composable
private fun DateRangeCard(
    startDate: String, // yyyy-MM-dd
    endDate: String,   // yyyy-MM-dd
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onApply: () -> Unit
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .padding(14.dp)
    ) {
        Text("Query by date", color = TextPrimary, fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DatePickPill(
                label = "Start date",
                value = startDate,
                onClick = { showStartPicker = true },
                modifier = Modifier.weight(1f)
            )
            DatePickPill(
                label = "End date",
                value = endDate,
                onClick = { showEndPicker = true },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onApply,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonBg),
            modifier = Modifier
                .align(Alignment.End)
                .height(44.dp)
        ) {
            Text("Apply", color = TextPrimary)
        }
    }

    if (showStartPicker) {
        SingleDatePickerDialog(
            title = "Select start date",
            initialDate = startDate,
            onDismiss = { showStartPicker = false },
            onConfirm = { picked ->
                onStartDateChange(picked)
                showStartPicker = false
            }
        )
    }

    if (showEndPicker) {
        SingleDatePickerDialog(
            title = "Select end date",
            initialDate = endDate,
            onDismiss = { showEndPicker = false },
            onConfirm = { picked ->
                onEndDateChange(picked)
                showEndPicker = false
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleDatePickerDialog(
    title: String,
    initialDate: String,              // yyyy-MM-dd
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit       // yyyy-MM-dd
) {
    val initialMillis = remember(initialDate) {
        runCatching {
            val localDate = java.time.LocalDate.parse(initialDate)
            localDate
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }.getOrNull()
    }

    val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = state.selectedDateMillis
                    if (millis != null) {
                        val picked = millisToLocalDateString(millis)
                        onConfirm(picked)
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text("OK", color = AccentLime)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))

            DatePicker(state = state)
        }
    }
}

private fun millisToLocalDateString(millisUtc: Long): String {
    return java.time.Instant
        .ofEpochMilli(millisUtc)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()
        .toString()
}

@Composable
private fun DatePickPill(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BackgroundDark)
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = AccentLime
            )
        }
    }
}

@Composable
private fun ChartModeTabs(
    selected: CaloriesChartMode,
    onSelect: (CaloriesChartMode) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(SurfaceDark)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TabPill(
            text = "Eaten vs Burned",
            selected = selected == CaloriesChartMode.LINE_EATEN_BURNED,
            onClick = { onSelect(CaloriesChartMode.LINE_EATEN_BURNED) },
            modifier = Modifier.weight(1f)
        )
        TabPill(
            text = "Macros",
            selected = selected == CaloriesChartMode.BAR_MACROS,
            onClick = { onSelect(CaloriesChartMode.BAR_MACROS) },
            modifier = Modifier.weight(1f)
        )
        TabPill(
            text = "Net",
            selected = selected == CaloriesChartMode.BAR_NET,
            onClick = { onSelect(CaloriesChartMode.BAR_NET) },
            modifier = Modifier.weight(0.8f)
        )
    }
}

@Composable
private fun TabPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) AccentLime else BackgroundDark
    val fg = if (selected) BackgroundDark else TextPrimary

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
@Composable
private fun NetCaloriesBarChartCard(stats: List<CaloriesStat>) {
    val values = stats.map { it.eatenCalories - it.burnedCalories }
    val maxAbs = (values.maxOfOrNull { kotlin.math.abs(it) } ?: 1f).coerceAtLeast(1f)

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        Text("Net calories (Eaten - Burned)", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))

        Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            val w = size.width
            val h = size.height
            val n = values.size.coerceAtLeast(1)
            val barW = (w / n) * 0.6f
            val gap = (w / n) * 0.4f

            val midY = h * 0.5f

            // mid line
            drawLine(
                color = Color.White.copy(alpha = 0.08f),
                start = Offset(0f, midY),
                end = Offset(w, midY),
                strokeWidth = 2f
            )

            values.forEachIndexed { i, v ->
                val x = i * (barW + gap) + gap / 2f
                val barH = (kotlin.math.abs(v) / maxAbs) * (h * 0.45f)

                val top = if (v >= 0f) midY - barH else midY
                val bottom = if (v >= 0f) midY else midY + barH

                drawRoundRect(
                    color = if (v >= 0f) AccentLime else LavenderBand,
                    topLeft = Offset(x, top),
                    size = androidx.compose.ui.geometry.Size(barW, bottom - top),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Positive = surplus, Negative = deficit",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}
@Composable
private fun MacrosStackedBarChartCard(stats: List<CaloriesStat>) {
    val maxTotal = stats.maxOfOrNull { it.eatenProteins + it.eatenCarbs + it.eatenFats }
        ?.coerceAtLeast(1f) ?: 1f

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        Text("Macros per day (stacked)", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))

        Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            val w = size.width
            val h = size.height
            val n = stats.size.coerceAtLeast(1)
            val barW = (w / n) * 0.6f
            val gap = (w / n) * 0.4f

            stats.forEachIndexed { i, s ->
                val x = i * (barW + gap) + gap / 2f
                val total = (s.eatenProteins + s.eatenCarbs + s.eatenFats).coerceAtLeast(1f)

                fun segHeight(v: Float): Float = (v / maxTotal) * (h * 0.9f)

                val hP = segHeight(s.eatenProteins)
                val hC = segHeight(s.eatenCarbs)
                val hF = segHeight(s.eatenFats)

                var y = h * 0.95f

                // Fat (bottom)
                drawRoundRect(
                    color = LavenderBand,
                    topLeft = Offset(x, y - hF),
                    size = androidx.compose.ui.geometry.Size(barW, hF),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f)
                )
                y -= hF

                // Carbs (middle)
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.25f),
                    topLeft = Offset(x, y - hC),
                    size = androidx.compose.ui.geometry.Size(barW, hC),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f)
                )
                y -= hC

                // Protein (top)
                drawRoundRect(
                    color = AccentLime,
                    topLeft = Offset(x, y - hP),
                    size = androidx.compose.ui.geometry.Size(barW, hP),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f)
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendDot(AccentLime, "Protein")
            LegendDot(Color.White.copy(alpha = 0.25f), "Carbs")
            LegendDot(LavenderBand, "Fat")
        }
    }
}
@Composable
private fun LegendDot(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(label, color = TextSecondary, fontSize = 12.sp)
    }
}
