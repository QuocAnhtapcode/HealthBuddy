@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.healthbuddy.screens.setup

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderScreen(
    onBack: () -> Unit,
    onContinue: (isMale: Boolean) -> Unit
) {
    var selectedGender by remember { mutableStateOf<Boolean?>(null) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onBack.invoke() }
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
                            text = "Back",
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
            Spacer(Modifier.height(12.dp))

            Text(
                text = "What’s Your Gender",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LavenderBand)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "We use this information to personalize insights, goals, and health trends for you.",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(32.dp))

            GenderOptionCircle(
                label = "Male",
                selected = selectedGender == true,
                selectedIcon = R.drawable.ic_male_selected,
                unselectedIcon = R.drawable.ic_male_unselected,
                onClick = { selectedGender = true }
            )

            Spacer(Modifier.height(24.dp))

            GenderOptionCircle(
                label = "Female",
                selected = selectedGender == false,
                selectedIcon = R.drawable.ic_female_selected,
                unselectedIcon = R.drawable.ic_female_unselected,
                onClick = { selectedGender = false }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { selectedGender?.let { onContinue(it) } },
                enabled = selectedGender != null,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBg),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .height(52.dp)
                    .width(220.dp)
            ) {
                Text(
                    text = "Continue",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GenderOptionCircle(
    label: String,
    selected: Boolean,
    selectedIcon: Int,
    unselectedIcon: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(if (selected) AccentLime else BackgroundDark)
                .border(
                    width = if (selected) 0.dp else 1.5.dp,
                    color = if (selected) Color.Transparent else TextPrimary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = if (selected) selectedIcon else unselectedIcon
                ),
                contentDescription = label,
                tint = if (selected) BackgroundDark else TextPrimary,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            color = if (selected) AccentLime else TextPrimary,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun GenderScreenPreview() {
    GenderScreen(
        onBack = {},
        onContinue = {}
    )
}
