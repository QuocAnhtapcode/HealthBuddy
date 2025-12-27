package com.example.healthbuddy.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.data.model.HealthInfo
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary
import java.time.LocalDate
import kotlin.math.roundToInt

data class HealthChartPoint(
    val date: String,
    val height: Float,
    val weight: Float,
    val fat: Float,
    val bmi: Float
)
@Composable
fun HealthInfoLineChartCard(healthList: List<HealthInfo>) {
    if (healthList.isEmpty()) {
        EmptyChartCard(
            title = "Chỉ số cơ thể",
            subtitle = "Chưa có dữ liệu sức khoẻ."
        )
        return
    }

    val points = healthList.map {
        HealthChartPoint(
            date = it.createdDate!!.take(10),
            height = it.height,
            weight = it.weight,
            fat = it.fatPercentage,
            bmi = it.bmi ?: 0f
        )
    }

    val maxHeight = points.maxOf { it.height }
    val maxWeight = points.maxOf { it.weight }
    val maxFat = points.maxOf { it.fat }
    val maxBmi = points.maxOf { it.bmi }.coerceAtLeast(1f)

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .padding(16.dp)
    ) {

        Text(
            "Chỉ số cơ thể",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        // Summary
        ChartSummaryRow(
            leftLabel = "Cân nặng",
            leftValue = points.last().weight,
            leftUnit = "kg",
            rightLabel = "BMI",
            rightValue = points.last().bmi,
            rightUnit = ""
        )

        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(BackgroundDark.copy(alpha = 0.35f))
                .pointerInput(points) {
                    detectTapGestures { offset ->
                        val step = size.width / (points.size - 1).coerceAtLeast(1)
                        val i = (offset.x / step).roundToInt()
                            .coerceIn(0, points.size - 1)
                        selectedIndex = if (selectedIndex == i) null else i
                    }
                }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val n = points.size

                fun x(i: Int) = w / (n - 1) * i
                fun y(v: Float) = h * (1f - v)

                // grid
                repeat(4) {
                    val yy = h * (it + 1) / 5f
                    drawLine(
                        Color.White.copy(alpha = 0.06f),
                        Offset(0f, yy),
                        Offset(w, yy),
                        2f
                    )
                }

                fun drawMetric(
                    values: List<Float>,
                    max: Float,
                    color: Color
                ) {
                    val path = Path()
                    values.forEachIndexed { i, v ->
                        val px = x(i)
                        val py = y(v / max)
                        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                    }
                    drawPath(
                        path,
                        color,
                        style = Stroke(6f, cap = StrokeCap.Round)
                    )

                    values.forEachIndexed { i, v ->
                        drawCircle(
                            color,
                            radius = 6f,
                            center = Offset(x(i), y(v / max))
                        )
                    }
                }

                drawMetric(points.map { it.height }, maxHeight, AccentLime)
                drawMetric(points.map { it.weight }, maxWeight, LavenderBand)
                drawMetric(points.map { it.fat }, maxFat, Color(0xFFFF9F1C))
                drawMetric(points.map { it.bmi }, maxBmi, Color(0xFF4D96FF))

                selectedIndex?.let { idx ->
                    drawLine(
                        Color.White.copy(alpha = 0.15f),
                        Offset(x(idx), 0f),
                        Offset(x(idx), h),
                        2f
                    )
                }
            }

            selectedIndex?.let { idx ->
                val p = points[idx]
                TooltipCard(
                    modifier = Modifier.padding(10.dp),
                    title = p.date,
                    lines = listOf(
                        "Chiều cao: ${p.height} cm",
                        "Cân nặng: ${p.weight} kg",
                        "Tỉ lệ mỡ: ${p.fat} %",
                        "BMI: ${"%.2f".format(p.bmi)}"
                    )
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            LegendDot(AccentLime, "Chiều cao")
            LegendDot(LavenderBand, "Cân nặng")
            LegendDot(Color(0xFFFF9F1C), "Tỉ lệ mỡ")
            LegendDot(Color(0xFF4D96FF), "BMI")
        }

        Spacer(Modifier.height(10.dp))

        HealthDetailRowList(points)
    }
}
@Composable
private fun HealthDetailRowList(points: List<HealthChartPoint>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        points.forEach { p ->
            Column(
                modifier = Modifier
                    .widthIn(min = 160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundDark.copy(alpha = 0.55f))
                    .padding(10.dp)
            ) {
                Text(p.date, color = TextSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Text("Chiều cao: ${p.height} cm", color = TextPrimary, fontSize = 12.sp)
                Text("Cân nặng: ${p.weight} kg", color = TextPrimary, fontSize = 12.sp)
                Text("Tỉ lệ mỡ: ${p.fat} %", color = TextPrimary, fontSize = 12.sp)
                Text("BMI: ${"%.2f".format(p.bmi)}", color = TextPrimary, fontSize = 12.sp)
            }
        }
    }
}
