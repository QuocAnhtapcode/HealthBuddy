package com.example.healthbuddy.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlin.math.roundToInt

@Composable
fun CaloriesChartsSection(
    ui: HomeUiState,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onApply: () -> Unit,
    onModeChange: (CaloriesChartMode) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Biểu đồ Calo",
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
    }
}

@Composable
private fun CaloriesLineChartCard(stats: List<CaloriesStat>) {
    if (stats.isEmpty()) {
        EmptyChartCard(title = "Nạp vào vs Tiêu thụ", subtitle = "Không có dữ liệu trong khoảng ngày này.")
        return
    }

    val eaten = stats.map { it.eatenCalories }
    val burned = stats.map { it.burnedCalories }
    val maxValue = max(eaten.maxOrNull() ?: 0f, burned.maxOrNull() ?: 0f).coerceAtLeast(1f)

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        Text("Nạp vào vs Tiêu thụ", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        // Summary row
        ChartSummaryRow(
            leftLabel = "Trung bình nạp vào",
            leftValue = eaten.average().toFloat(),
            leftUnit = "kcal",
            rightLabel = "Trung bình đốt",
            rightValue = burned.average().toFloat(),
            rightUnit = "kcal"
        )

        Spacer(Modifier.height(10.dp))

        val chartHeight = 170.dp
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
                .clip(RoundedCornerShape(18.dp))
                .background(BackgroundDark.copy(alpha = 0.35f))
                .pointerInput(stats) {
                    detectTapGestures { offset ->
                        val w = size.width.toFloat().coerceAtLeast(1f)
                        val n = stats.size
                        val step = if (n <= 1) w else w / (n - 1)
                        val i = ((offset.x / step).roundToInt()).coerceIn(0, n - 1)
                        selectedIndex = if (selectedIndex == i) null else i
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val n = stats.size.coerceAtLeast(2)

                fun x(i: Int) = (w / (n - 1)) * i
                fun y(v: Float): Float {
                    val topPad = h * 0.08f
                    val botPad = h * 0.10f
                    val usable = h - topPad - botPad
                    return h - botPad - (v / maxValue) * usable
                }

                // Grid (4 lines)
                val gridLines = 4
                repeat(gridLines + 1) { gi ->
                    val yy = (h / (gridLines + 1)) * (gi + 1)
                    drawLine(
                        color = Color.White.copy(alpha = 0.06f),
                        start = Offset(0f, yy),
                        end = Offset(w, yy),
                        strokeWidth = 2f
                    )
                }

                // Baseline
                drawLine(
                    color = Color.White.copy(alpha = 0.10f),
                    start = Offset(0f, h * 0.92f),
                    end = Offset(w, h * 0.92f),
                    strokeWidth = 2f
                )

                // Paths
                fun buildPath(values: List<Float>): Path = Path().apply {
                    values.forEachIndexed { i, v ->
                        val p = Offset(x(i), y(v))
                        if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }
                }

                val eatenPath = buildPath(eaten)
                val burnedPath = buildPath(burned)

                drawPath(eatenPath, AccentLime, style = Stroke(width = 6f, cap = StrokeCap.Round))
                drawPath(burnedPath, LavenderBand, style = Stroke(width = 6f, cap = StrokeCap.Round))

                // Dots
                eaten.forEachIndexed { i, v ->
                    drawCircle(
                        color = AccentLime,
                        radius = 7f,
                        center = Offset(x(i), y(v))
                    )
                }
                burned.forEachIndexed { i, v ->
                    drawCircle(
                        color = LavenderBand,
                        radius = 7f,
                        center = Offset(x(i), y(v))
                    )
                }

                // Tooltip line + highlight
                selectedIndex?.let { idx ->
                    val xx = x(idx)
                    drawLine(
                        color = Color.White.copy(alpha = 0.15f),
                        start = Offset(xx, 0f),
                        end = Offset(xx, h),
                        strokeWidth = 2f
                    )
                }
            }

            // Tooltip card overlay
            selectedIndex?.let { idx ->
                val s = stats[idx]
                TooltipCard(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    title = s.date,
                    lines = listOf(
                        "Ăn: ${s.eatenCalories.toInt()} kcal",
                        "Đốt: ${s.burnedCalories.toInt()} kcal"
                    )
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendDot(color = AccentLime, label = "Nạp vào")
            LegendDot(color = LavenderBand, label = "Tiêu thụ")
        }

        Spacer(Modifier.height(10.dp))

        CaloriesDetailRowList(stats = stats)
    }
}

@Composable
private fun CaloriesDetailRowList(stats: List<CaloriesStat>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        stats.forEach { s ->
            Column(
                modifier = Modifier
                    .widthIn(min = 140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundDark.copy(alpha = 0.55f))
                    .padding(10.dp)
            ) {
                Text(s.date, color = TextSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Text("Ăn: ${s.eatenCalories.toInt()} kcal", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("Đốt:  ${s.burnedCalories.toInt()} kcal", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ChartSummaryRow(
    leftLabel: String,
    leftValue: Float,
    leftUnit: String,
    rightLabel: String,
    rightValue: Float,
    rightUnit: String
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SummaryChip(label = leftLabel, value = "${leftValue.toInt()} $leftUnit", modifier = Modifier.weight(1f))
        SummaryChip(label = rightLabel, value = "${rightValue.toInt()} $rightUnit", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SummaryChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(BackgroundDark.copy(alpha = 0.55f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Spacer(Modifier.height(2.dp))
        Text(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TooltipCard(
    modifier: Modifier = Modifier,
    title: String,
    lines: List<String>
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark.copy(alpha = 0.95f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Text(title, color = AccentLime, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        lines.forEach {
            Text(it, color = TextPrimary, fontSize = 12.sp)
        }
    }
}

@Composable
fun EmptyChartCard(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, color = TextSecondary, fontSize = 12.sp)
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
        Text("Truy vấn theo ngày", color = TextPrimary, fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DatePickPill(
                label = "Ngày bắt đầu",
                value = startDate,
                onClick = { showStartPicker = true },
                modifier = Modifier.weight(1f)
            )
            DatePickPill(
                label = "Ngày kết thúc",
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
            Text("Tìm kiếm", color = TextPrimary)
        }
    }

    if (showStartPicker) {
        SingleDatePickerDialog(
            title = "Chọn ngày bắt đầu",
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
            title = "Chọn ngày kết thúc",
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
                Text("OK", color = BackgroundDark)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = BackgroundDark)
            }
        }
    ) {
        DatePicker(state = state)
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
            text = "Nạp vs Tiêu thụ",
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
            text = "Thực nhận",
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
    if (stats.isEmpty()) {
        EmptyChartCard(title = "Calo thực nhận", subtitle = "Không có dữ liệu.")
        return
    }

    val values = stats.map { it.eatenCalories - it.burnedCalories }
    val maxAbs = (values.maxOfOrNull { kotlin.math.abs(it) } ?: 1f).coerceAtLeast(1f)
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        Text("Calo thực nhận (Nạp - Tiêu)", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(BackgroundDark.copy(alpha = 0.35f))
                .pointerInput(stats) {
                    detectTapGestures { offset ->
                        val w = size.width.toFloat().coerceAtLeast(1f)
                        val n = values.size
                        val step = w / n
                        val i = (offset.x / step).toInt().coerceIn(0, n - 1)
                        selectedIndex = if (selectedIndex == i) null else i
                    }
                }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val n = values.size
                val step = w / n
                val barW = step * 0.55f
                val midY = h * 0.52f

                // mid line
                drawLine(
                    color = Color.White.copy(alpha = 0.10f),
                    start = Offset(0f, midY),
                    end = Offset(w, midY),
                    strokeWidth = 2f
                )

                values.forEachIndexed { i, v ->
                    val xCenter = (i + 0.5f) * step
                    val xLeft = xCenter - barW / 2f
                    val barH = (kotlin.math.abs(v) / maxAbs) * (h * 0.40f)

                    val top = if (v >= 0f) midY - barH else midY
                    val height = barH

                    val isSelected = selectedIndex == i

                    drawRoundRect(
                        color = if (v >= 0f) AccentLime else LavenderBand,
                        topLeft = Offset(xLeft, top),
                        size = androidx.compose.ui.geometry.Size(barW, height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
                        alpha = if (isSelected || selectedIndex == null) 1f else 0.45f
                    )
                }
            }

            selectedIndex?.let { idx ->
                val v = values[idx]
                TooltipCard(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    title = stats[idx].date,
                    lines = listOf("Net: ${v.toInt()} kcal")
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Dương = dư thừa, Âm = thâm hụt", color = TextSecondary, fontSize = 12.sp)

        Spacer(Modifier.height(12.dp))
        CaloriesDetailRowList(stats = stats)
    }
}

@Composable
private fun MacrosStackedBarChartCard(stats: List<CaloriesStat>) {
    if (stats.isEmpty()) {
        EmptyChartCard(title = "Macros mỗi ngày", subtitle = "Không có dữ liệu.")
        return
    }

    val maxTotal = stats.maxOfOrNull { it.eatenProteins + it.eatenCarbs + it.eatenFats }?.coerceAtLeast(1f) ?: 1f
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val avgP = stats.map { it.eatenProteins }.average().toFloat()
    val avgC = stats.map { it.eatenCarbs }.average().toFloat()
    val avgF = stats.map { it.eatenFats }.average().toFloat()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        Text("Macros mỗi ngày", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        // Summary
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SummaryChip("Đạm", "${avgP.toInt()} g", Modifier.weight(1f))
            SummaryChip("Tinh bột", "${avgC.toInt()} g", Modifier.weight(1f))
            SummaryChip("Chất béo", "${avgF.toInt()} g", Modifier.weight(1f))
        }

        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(BackgroundDark.copy(alpha = 0.35f))
                .pointerInput(stats) {
                    detectTapGestures { offset ->
                        val w = size.width.toFloat().coerceAtLeast(1f)
                        val n = stats.size
                        val step = w / n
                        val i = (offset.x / step).toInt().coerceIn(0, n - 1)
                        selectedIndex = if (selectedIndex == i) null else i
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val n = stats.size
                val step = w / n
                val barW = step * 0.55f

                // grid
                repeat(4) { k ->
                    val yy = (h / 5f) * (k + 1)
                    drawLine(
                        color = Color.White.copy(alpha = 0.06f),
                        start = Offset(0f, yy),
                        end = Offset(w, yy),
                        strokeWidth = 2f
                    )
                }

                stats.forEachIndexed { i, s ->
                    val xCenter = (i + 0.5f) * step
                    val xLeft = xCenter - barW / 2f

                    fun segH(v: Float): Float = (v / maxTotal) * (h * 0.88f)

                    val hP = segH(s.eatenProteins)
                    val hC = segH(s.eatenCarbs)
                    val hF = segH(s.eatenFats)

                    var y = h * 0.95f

                    val alpha = if (selectedIndex == null || selectedIndex == i) 1f else 0.45f

                    // Fat
                    drawRoundRect(
                        color = LavenderBand,
                        topLeft = Offset(xLeft, y - hF),
                        size = androidx.compose.ui.geometry.Size(barW, hF),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f),
                        alpha = alpha
                    )
                    y -= hF

                    // Carbs
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.25f),
                        topLeft = Offset(xLeft, y - hC),
                        size = androidx.compose.ui.geometry.Size(barW, hC),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f),
                        alpha = alpha
                    )
                    y -= hC

                    // Protein
                    drawRoundRect(
                        color = AccentLime,
                        topLeft = Offset(xLeft, y - hP),
                        size = androidx.compose.ui.geometry.Size(barW, hP),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f),
                        alpha = alpha
                    )
                }
            }

            selectedIndex?.let { idx ->
                val s = stats[idx]
                TooltipCard(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    title = s.date,
                    lines = listOf(
                        "Đạm:     ${s.eatenProteins.toInt()} g",
                        "Tinh bột:${s.eatenCarbs.toInt()} g",
                        "Chất béo:${s.eatenFats.toInt()} g"
                    )
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendDot(AccentLime, "Đạm")
            LegendDot(Color.White.copy(alpha = 0.25f), "Tinh bột")
            LegendDot(LavenderBand, "Chất béo")
            Text("• Chạm để xem", color = TextSecondary, fontSize = 12.sp)
        }

        Spacer(Modifier.height(12.dp))
        MacrosDetailRowList(stats)
    }
}

@Composable
private fun MacrosDetailRowList(stats: List<CaloriesStat>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        stats.forEach { s ->
            Column(
                modifier = Modifier
                    .widthIn(min = 170.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundDark.copy(alpha = 0.55f))
                    .padding(10.dp)
            ) {
                Text(s.date, color = TextSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Text("P: ${s.eatenProteins.toInt()} g", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("C: ${s.eatenCarbs.toInt()} g", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("F: ${s.eatenFats.toInt()} g", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun LegendDot(color: Color, label: String) {
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
