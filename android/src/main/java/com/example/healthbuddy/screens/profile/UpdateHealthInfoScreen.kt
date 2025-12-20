package com.example.healthbuddy.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.R
import com.example.healthbuddy.data.model.HealthInfo
import com.example.healthbuddy.data.model.User
import com.example.healthbuddy.screens.component.Avatar
import com.example.healthbuddy.screens.component.DarkTextField
import com.example.healthbuddy.screens.component.LabeledField
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateHealthInfoScreen(
    user: User,
    healthInfo: HealthInfo?,
    avatarUrl: String? = null,
    onBack: (() -> Unit)? = null,
    onUpdate: (updatedHealthInfo: HealthInfo) -> Unit
) {

    val baseHeight = healthInfo?.height ?: 0f
    val baseWeight = healthInfo?.weight ?: 0f
    val baseFat = healthInfo?.fatPercentage ?: 0f

    var height by remember { mutableStateOf(if (baseHeight > 0f) baseHeight.toInt().toString() else "") }
    var weight by remember { mutableStateOf(if (baseWeight > 0f) baseWeight.toInt().toString() else "") }
    var fat by remember { mutableStateOf(if (baseFat > 0f) baseFat.toString() else "") }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cập nhật chỉ số cơ thể",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack?.invoke() }) {
                        Icon(
                            painterResource(R.drawable.ic_back),
                            contentDescription = "Quay lại",
                            tint = AccentLime
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(BackgroundDark)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LavenderBand)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        Avatar(avatarUrl = avatarUrl, size = 88.dp)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 6.dp, y = 6.dp)
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(AccentLime),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit_avatar),
                                contentDescription = "Thay đổi ảnh đại diện",
                                tint = BackgroundDark,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = user.username,
                        color = TextPrimary,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Chỉ số cơ thể",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            LabeledField("Chiều cao (cm)") {
                DarkTextField(
                    value = height,
                    onValueChange = { new ->
                        if (new.all { it.isDigit() } || new.isEmpty()) height = new
                    },
                    placeholder = "175"
                )
            }

            LabeledField("Cân nặng (kg)") {
                DarkTextField(
                    value = weight,
                    onValueChange = { new ->
                        if (new.all { it.isDigit() } || new.isEmpty()) weight = new
                    },
                    placeholder = "70"
                )
            }

            LabeledField("Tỉ lệ mỡ (%)") {
                DarkTextField(
                    value = fat,
                    onValueChange = { new ->
                        if (new.all { it.isDigit() || it == '.' } || new.isEmpty()) fat = new
                    },
                    placeholder = "18.5"
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val updatedHealth = HealthInfo(
                        id = healthInfo?.id,
                        height = height.toFloatOrNull() ?: baseHeight,
                        weight = weight.toFloatOrNull() ?: baseWeight,
                        bmi = healthInfo?.bmi,
                        bmr = healthInfo?.bmr,
                        fatPercentage = fat.toFloatOrNull() ?: baseFat,
                        createdDate = healthInfo?.createdDate,
                        updatedDate = healthInfo?.updatedDate
                    )

                    onUpdate(updatedHealth)
                },
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .height(48.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentLime,
                    contentColor = BackgroundDark
                )
            ) {
                Text(
                    text = "Lưu",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

