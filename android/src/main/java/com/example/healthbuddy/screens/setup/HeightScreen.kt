@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.healthbuddy.screens.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
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
fun HeightScreen(
    onBack: (() -> Unit)? = null,
    onContinue: (heightCm: Int) -> Unit = {}
) {
    val heights = (140..210).toList()

    val rowHeightDp = 24.dp
    val rowHeightPx = with(LocalDensity.current) { rowHeightDp.toPx() }

    // Start around 165cm
    val startIndex = heights.indexOf(165).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (startIndex - 5).coerceAtLeast(0))

    val selectedIndex by remember {
        derivedStateOf {
            val base = listState.firstVisibleItemIndex
            val offsetPx = listState.firstVisibleItemScrollOffset
            val offsetItems = offsetPx / rowHeightPx
            (base + offsetItems + 5) // "+5" is how far down the marker line sits
                .toInt()
                .coerceIn(0, heights.lastIndex)
        }
    }
    val selectedHeight = heights[selectedIndex]

    val labelValues = remember(selectedHeight) {
        listOf(
            selectedHeight + 10,
            selectedHeight + 5,
            selectedHeight,
            selectedHeight - 5,
            selectedHeight - 10
        )
    }

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
                text = "What Is Your Height?",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(12.dp))

            // keep the original lorem ipsum style text
            Text(
                text = "Your height helps us estimate BMI ranges and build healthier targets.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Big current height readout: "165 Cm"
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = selectedHeight.toString(),
                    color = TextPrimary,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Cm",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            // The picker row: left labels, right ruler
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                // LEFT: vertical list of 5 reference values
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.height(260.dp)
                ) {
                    labelValues.forEach { value ->
                        // is this the highlighted middle line?
                        val isSelected = value == selectedHeight
                        Text(
                            text = value.toString(),
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontSize = if (isSelected) 24.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                // RIGHT: purple ruler with ticks and lime marker
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .height(260.dp)
                        .width(110.dp)
                ) {
                    // ruler background
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(70.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(LavenderBand)
                    )

                    // scrollable ticks
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(70.dp)
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(heights.size) { idx ->
                            val cm = heights[idx]

                            // each row should match rowHeightDp so math stays consistent
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .height(rowHeightDp)
                                    .fillMaxWidth()
                            ) {
                                // We draw ticks like | or ---- for every line.
                                // Your mock shows long ticks on major steps and short ticks between.
                                Box(
                                    modifier = Modifier
                                        .height(2.dp)
                                        .width(
                                            if (cm % 5 == 0) 40.dp else 20.dp
                                        )
                                        .background(Color.White)
                                )
                            }
                        }
                    }

                    // center lime marker line + arrow (fixed, not scrolling)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(40.dp)
                                .background(AccentLime)
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_left),
                            contentDescription = null,
                            tint = AccentLime,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { onContinue(selectedHeight) },
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
private fun HeightScreenPreview() {
    HeightScreen()
}
