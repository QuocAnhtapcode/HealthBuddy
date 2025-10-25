@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.healthbuddy.screens.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun SetUpScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onBack.invoke() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = AccentLime,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
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
            Spacer(Modifier.height(32.dp))

            Text(
                text = "Consistency Is\nThe Key To Progress.\nDon't Give Up!",
                color = AccentLime,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LavenderBand)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Small, steady habits lead to real change. We’ll guide you step by step so you can stay focused and see progress over time.",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNext,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBg),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .height(52.dp)
                    .width(200.dp),
            ) {
                Text(
                    text = "Set up your profile",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MotivationPreview() {
    SetUpScreen(
        onBack = {},
        onNext = {}
    )
}
