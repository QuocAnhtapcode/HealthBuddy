package com.example.healthbuddy.screens.setup

import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.healthbuddy.ui.theme.TextSecondary
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    onBack: (() -> Unit)? = null,
    onContinue: (weight: Float, unit: String) -> Unit = { _, _ -> }
) {
    var unit by remember { mutableStateOf("KG") }

    // numeric range; you can tweak
    val weights = (30..200).map { it.toFloat() } // 30kg..200kg
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 45) // ~75
    val selectedIndex by remember {
        derivedStateOf {
            val base = listState.firstVisibleItemIndex
            val offsetPx = listState.firstVisibleItemScrollOffset
            val approxItemPx = 60f
            val offsetItems = offsetPx / approxItemPx
            (base + offsetItems + 2).toInt().coerceIn(0, weights.lastIndex)
        }
    }
    val selectedWeight = weights[selectedIndex]

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
                text = "What Is Your Weight?",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Your weight helps us estimate calorie burn and set healthier targets.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // unit toggle (KG | LB)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentLime)
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "KG",
                    modifier = Modifier
                        .clickable { unit = "KG" }
                        .padding(end = 16.dp),
                    color = BackgroundDark,
                    fontWeight = if (unit == "KG") FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(BackgroundDark)
                )
                Text(
                    "LB",
                    modifier = Modifier
                        .clickable { unit = "LB" }
                        .padding(start = 16.dp),
                    color = BackgroundDark.copy(alpha = if (unit == "LB") 1f else 0.6f),
                    fontWeight = if (unit == "LB") FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val center = selectedIndex
                for (i in -2..2) {
                    val idx = (center + i).coerceIn(0, weights.lastIndex)
                    val value = weights[idx].toInt()
                    Text(
                        text = value.toString(),
                        color = if (i == 0) TextPrimary else TextSecondary,
                        fontSize = if (i == 0) 24.sp else 18.sp,
                        fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

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
                    items(weights.size) { idx ->
                        // draw tick marks
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(if (idx % 5 == 0) 40.dp else 20.dp)
                                .background(Color.White)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Icon(
                painter = painterResource(R.drawable.ic_arrow_up),
                contentDescription = null,
                tint = AccentLime,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedWeight.toInt().toString(),
                    color = TextPrimary,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = unit,
                    color = AccentLime,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { onContinue(selectedWeight, unit) },
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
private fun WeightPreview() {
    WeightScreen()
}
