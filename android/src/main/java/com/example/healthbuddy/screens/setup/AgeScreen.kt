@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.healthbuddy.screens.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary

@Composable
fun AgeScreen(
    onBack: (() -> Unit)? = null,
    onContinue: (age: Int) -> Unit = {}
) {
    val ages = (1..120).toList()

    val defaultIndex = ages.indexOf(28).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = defaultIndex)

    val selectedIndex by remember {
        derivedStateOf {
            val base = listState.firstVisibleItemIndex
            val offsetPx = listState.firstVisibleItemScrollOffset
            val approxItemPx = 60f // must match each item's width below
            val offsetItems = offsetPx / approxItemPx
            (base + offsetItems + 2) // +2 means "center-ish" of our window
                .toInt()
                .coerceIn(0, ages.lastIndex)
        }
    }

    val selectedAge = ages[selectedIndex]

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
                            "Back",
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
                text = "How Old Are You?",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Tell us your age so we can adapt goals, coaching and recommendations to you.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // This row mimics the top row in WeightScreen (73 74 75 76 77)
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val center = selectedIndex
                for (i in -2..2) {
                    val idx = (center + i).coerceIn(0, ages.lastIndex)
                    val value = ages[idx]
                    Text(
                        text = value.toString(),
                        color = if (i == 0) TextPrimary else TextSecondary,
                        fontSize = if (i == 0) 24.sp else 18.sp,
                        fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            // Lavender ruler band (like weight ruler)
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .background(LavenderBand)
            ) {
                LazyRow(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(ages.size) { idx ->
                        // We'll just show tick marks.
                        // Every 5 years -> taller tick
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(if (ages[idx] % 5 == 0) 40.dp else 20.dp)
                                .background(Color.White)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Lime caret below the band
            Icon(
                painter = painterResource(R.drawable.ic_arrow_up),
                contentDescription = null,
                tint = AccentLime,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Big selected age
            Text(
                text = selectedAge.toString(),
                color = TextPrimary,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { onContinue(selectedAge) },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBg),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .height(52.dp)
                    .width(220.dp)
            ) {
                Text(
                    "Continue",
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
private fun AgeScreenPreview() {
    AgeScreen()
}
