@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.healthbuddy.screens.wellcome

import androidx.compose.foundation.Image
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
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary


@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit = {}
) {
    Scaffold(
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundDark),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .padding(bottom = 20.dp)
            )

            Text(
                text = "Welcome to HealthBuddy",
                color = AccentLime,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Track your health, manage your sleep, and stay connected with your well-being — all in one place.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onGetStarted,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "Get Started",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Version 1.0.0",
                color = TextSecondary.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun WelcomePreview() {
    WelcomeScreen()
}
