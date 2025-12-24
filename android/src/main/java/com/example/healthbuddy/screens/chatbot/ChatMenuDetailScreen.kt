package com.example.healthbuddy.screens.chatbot

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.example.healthbuddy.R
import com.example.healthbuddy.data.model.ChatMealPreview
import com.example.healthbuddy.data.model.ChatMealRecipePreview
import com.example.healthbuddy.data.model.ChatMenuPreview
import com.example.healthbuddy.screens.menu.MacroLine
import com.example.healthbuddy.screens.menu.MealType
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.ButtonBg
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatMenuDetailScreen(
    viewModel: ChatBotViewModel,
    chatId: Long,
    onBack: () -> Unit,
    onChosenDone: () -> Unit
) {
    val ui by viewModel.ui.collectAsState()

    LaunchedEffect(chatId) {
        viewModel.selectChat(chatId)
        Log.d("MenuDetailScreen",chatId.toString())
        Log.d("MenuDetailScreen",ui.menuPreview.toString())
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Menu gợi ý",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Quay lại",
                            tint = AccentLime
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        bottomBar = {
            Surface(color = SurfaceDark) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceDark)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.saveSelectedMenu(onDone = onChosenDone) },
                        enabled = !ui.savingMenu && ui.menuPreview != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonBg,
                            disabledContainerColor = ButtonBg.copy(alpha = 0.4f)
                        )
                    ) {
                        if (ui.savingMenu) {
                            CircularProgressIndicator(
                                color = AccentLime,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Chọn menu",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(BackgroundDark)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            if (ui.error != null) {
                ErrorPill(text = ui.error ?: "", onDismiss = viewModel::clearError)
            }

            if (ui.loadingMenuPreview) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentLime)
                }
                return@Column
            }

            val menu = ui.menuPreview
            if (menu == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không tải được menu.", color = TextSecondary)
                }
                return@Column
            }

            Spacer(Modifier.height(12.dp))

            ChatMenuSummaryCard(menu)

            Spacer(Modifier.height(12.dp))

            menu.meals.forEach { meal ->
                MealPreviewCard(meal)
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ChatMenuSummaryCard(menu: ChatMenuPreview) {
    val plan = menu.menuPlan

    val calTarget = plan.targetCalories
    val calActual = menu.actualTotalCalories

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(LavenderBand)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "Tổng quan",
                    color = TextPrimary,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = calActual.toInt().toString(),
                        color = AccentLime,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "/${calTarget.toInt()} kcal",
                        color = AccentLime,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MacroLine(
                    label = "Đạm",
                    actual = menu.actualTotalProtein,
                    target = plan.targetProtein
                )
                MacroLine(
                    label = "Tinh bột",
                    actual = menu.actualTotalCarb,
                    target = plan.targetCarb
                )
                MacroLine(
                    label = "Chất béo",
                    actual = menu.actualTotalFat,
                    target = plan.targetFat
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = "Chat ID: ${menu.menuPlan.id} • Status: ${menu.status ?: "N/A"}",
            color = TextPrimary,
            fontSize = 12.sp
        )
        if (!menu.notes.isNullOrBlank()) {
            Text(
                text = menu.notes,
                color = TextPrimary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun MealPreviewCard(meal: ChatMealPreview) {
    val title = when (meal.mealType) {
        MealType.BREAKFAST -> "Bữa sáng"
        MealType.LUNCH ->  "Bữa trưa"
        MealType.DINNER ->  "Bữa tối"
        MealType.SNACK ->  "Ăn vặt"
        else        -> meal.mealType ?: "Bữa ${meal.id}"
    }

    val iconEmoji = when (meal.mealType) {
        MealType.BREAKFAST  -> "\uD83E\uDDC0" // 🥐
        MealType.LUNCH ->  "\uD83C\uDF72" // 🍲
        MealType.DINNER ->  "\uD83C\uDF7D" // 🍽
        MealType.SNACK ->  "\uD83E\uDD67" // 🧇
        else        -> "\uD83C\uDF7D"
    }

    val calories = meal.mealRecipes
        .sumOf { it.calories.toDouble() }
        .toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(AccentLime.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = title.toString(),
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${calories.toInt()} kcal",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        if (meal.mealRecipes.isEmpty()) {
            Text(
                text = "Chưa có công thức nấu ăn",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                meal.mealRecipes.forEach {
                    ChatMealRecipeRow(it)
                }
            }
        }
    }
}

@Composable
fun ChatMealRecipeRow(
    chatMealRecipePreview: ChatMealRecipePreview
) {
    val recipe = chatMealRecipePreview.recipe
    val name = recipe?.name ?: "Công thức #${chatMealRecipePreview.id}"
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Transparent)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(BackgroundDark.copy(alpha = 0.6f))
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                if (!recipe?.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_nutrition),
                        contentDescription = null,
                        tint = AccentLime.copy(alpha = 0.8f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${chatMealRecipePreview.calories.toInt()} kcal · P ${"%.1f".format(chatMealRecipePreview.protein)} · C ${"%.1f".format(chatMealRecipePreview.carbs)} · F ${"%.1f".format(chatMealRecipePreview.fat)}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

