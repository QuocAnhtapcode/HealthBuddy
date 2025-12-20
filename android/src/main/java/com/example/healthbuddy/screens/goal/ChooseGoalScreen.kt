package com.example.healthbuddy.screens.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.R
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.ButtonBg
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseGoalScreen(
    onBack: (() -> Unit)? = null,
    onContinue: (Int) -> Unit = {}
) {
    var selectedGoal by remember { mutableStateOf<Int?>(null) }

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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundDark),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Mục tiêu của bạn là gì?",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Hãy chọn mục tiêu mô tả chính xác nhất những gì bạn muốn đạt được từ các buổi tập luyện của mình.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            ) {
                GoalCard(
                    title = "Giảm cân",
                    subtitle = "Đốt cháy mỡ thừa, thon gọn vóc dáng và cảm thấy nhẹ nhàng hơn.",
                    selected = selectedGoal == 1,
                    onClick = { selectedGoal = 1 }
                )
                Spacer(Modifier.height(14.dp))

                GoalCard(
                    title = "Tăng cơ",
                    subtitle = "Tăng cường sức mạnh và phát triển cơ bắp săn chắc.",
                    selected = selectedGoal == 2,
                    onClick = { selectedGoal = 2 }
                )
                Spacer(Modifier.height(14.dp))

                GoalCard(
                    title = "Duy trì cân nặng",
                    subtitle = "Giữ cơ thể cân đối, khỏe mạnh và duy trì vóc dáng hiện tại.",
                    selected = selectedGoal == 3,
                    onClick = { selectedGoal = 3 }
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                enabled = selectedGoal != null,
                onClick = { selectedGoal?.let(onContinue) },
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
private fun GoalCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) AccentLime else SurfaceDark
    val contentColor = if (selected) BackgroundDark else TextPrimary
    val subtitleColor = if (selected) BackgroundDark.copy(alpha = 0.8f) else TextSecondary
    val borderColor = if (selected) AccentLime else Color.Transparent

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Column {
            Text(
                text = title,
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = subtitleColor,
                fontSize = 13.sp,
                lineHeight = 17.sp
            )
        }
    }
}
@Preview(showSystemUi = true)
@Composable
fun ChooseGoalPreview(){
    ChooseGoalScreen()
}
