package com.example.healthbuddy.screens.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.R
import com.example.healthbuddy.data.model.GoalWithPlans
import com.example.healthbuddy.data.model.Plan
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.ButtonBg
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoosePlanScreen(
    goal: GoalWithPlans,
    onBack: (() -> Unit)? = null,
    onContinue: (Plan) -> Unit = {}
) {
    var selectedPlanId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onBack?.invoke() }
                            .padding(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = AccentLime,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Quay lại",
                            color = AccentLime,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                },
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(BackgroundDark),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Chọn kế hoạch tập luyện",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = goal.description,
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Plans list
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                goal.plans.forEach { plan ->
                    PlanCard(
                        plan = plan,
                        selected = selectedPlanId == plan.id,
                        onClick = { selectedPlanId = plan.id }
                    )
                    Spacer(Modifier.height(14.dp))
                }
                Spacer(Modifier.height(12.dp))
            }

            Button(
                enabled = selectedPlanId != null,
                onClick = {
                    val plan = goal.plans.firstOrNull { it.id == selectedPlanId } ?: return@Button
                    onContinue(plan)
                },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBg,
                    disabledContainerColor = ButtonBg.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .height(52.dp)
                    .width(220.dp)
            ) {
                Text(
                    "Tiếp tục",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
private fun PlanCard(
    plan: Plan,
    selected: Boolean,
    onClick: () -> Unit
) {
    val sessions = plan.planSessions
    val daysPerWeek = sessions.map { it.sessionDayOfWeek }.distinct().size
    val totalCalories = sessions.sumOf { it.targetCalories.toDouble() }.toFloat()

    val bg = if (selected) AccentLime else SurfaceDark
    val titleColor = if (selected) BackgroundDark else TextPrimary
    val textColor = if (selected) BackgroundDark.copy(alpha = 0.85f) else TextSecondary
    val borderColor = if (selected) AccentLime else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column {
            Text(
                text = plan.name,
                color = titleColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            // Summary line
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_star),
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "$daysPerWeek ngày/tuần",
                    color = textColor,
                    fontSize = 12.sp
                )

                Spacer(Modifier.width(12.dp))

                Icon(
                    painter = painterResource(R.drawable.ic_calories_black),
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${totalCalories.toInt()} Calo/tuần",
                    color = textColor,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            // Day chips row
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                sessions
                    .forEach { s ->
                        DayChip(
                            day = s.sessionDayOfWeek,
                            category = s.category,
                            selected = selected
                        )
                    }
            }
        }
    }
}

@Composable
private fun DayChip(day: String, category: String, selected: Boolean) {
    val shortDay = when (day.uppercase()) {
        "MONDAY"    -> "Mon"
        "TUESDAY"   -> "Tue"
        "WEDNESDAY" -> "Wed"
        "THURSDAY"  -> "Thu"
        "FRIDAY"    -> "Fri"
        "SATURDAY"  -> "Sat"
        "SUNDAY"    -> "Sun"
        else        -> day.take(3).replaceFirstChar { it.uppercase() }
    }

    val isCardio = category.equals("CARDIO", ignoreCase = true)
    val chipBg = if (selected) BackgroundDark.copy(alpha = 0.15f) else BackgroundDark.copy(alpha = 0.6f)
    val chipText = Color.White

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(chipBg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(shortDay, color = chipText, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}


