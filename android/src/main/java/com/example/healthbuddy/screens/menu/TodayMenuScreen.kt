package com.example.healthbuddy.screens.menu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.healthbuddy.R
import com.example.healthbuddy.data.model.Meal
import com.example.healthbuddy.data.model.MealRecipe
import com.example.healthbuddy.data.model.Menu
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.ButtonBg
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary


@Composable
fun MenuTodayScreen(
    menuViewModel: MenuViewModel,
    onOpenRecipePicker: (Meal) -> Unit,
    onEditMealRecipe: (mealId: Long, mealRecipeId: Long) -> Unit
) {
    val ui by menuViewModel.ui.collectAsState()

    LaunchedEffect(Unit) {
        menuViewModel.loadMenuForToday()
    }

    when {
        ui.loadingMenu -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentLime)
            }
        }

        ui.error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = ui.error ?: "Something went wrong",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { menuViewModel.loadMenuForToday() },
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBg),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Retry", color = TextPrimary)
                    }
                }
            }
        }

        ui.menu == null -> {
            // Trường hợp không có menu hôm nay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No menu for today yet",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Start by adding your first meal.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        else -> {
            val menu = ui.menu!!
            TodayMenuContent(
                menu = menu,
                onOpenRecipePicker = onOpenRecipePicker,
                onEditMealRecipe = onEditMealRecipe
            )
        }
    }
}

@Composable
private fun TodayMenuContent(
    menu: Menu,
    onOpenRecipePicker: (Meal) -> Unit,
    onEditMealRecipe: (mealId: Long, mealRecipeId: Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // ---------- HEADER: Today + summary ----------
        TodayHeader(menu)

        Spacer(Modifier.height(8.dp))

        // ---------- LIST MEALS ----------
        if (menu.meals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No meals yet. Add recipes to start tracking today.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(menu.meals, key = { it.id }) { meal ->
                    MealCard(
                        meal = meal,
                        onOpenRecipePicker = onOpenRecipePicker,
                        onEditMealRecipe = onEditMealRecipe
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TodayHeader(menu: Menu) {
    val today = remember {
        java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, dd MMM"))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = "Today",
            color = AccentLime,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = today,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        TodaySummaryCard(menu)
    }
}

@Composable
private fun TodaySummaryCard(menu: Menu) {
    val target = menu.menuPlan.targetCalories
    val actual = menu.actualTotalCalories
    val ratioRaw = if (target > 0f) actual / target else 0f
    val ratio = ratioRaw.coerceIn(0f, 1.3f)   // cho phép hơi vượt 1 tí để nhìn rõ
    val animatedRatio by animateFloatAsState(
        targetValue = ratio.coerceIn(0f, 1f),
        label = "calorieProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF403266))      // tím đậm giống screenshot
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // ---- LEFT: Calories today ----
            Column {
                Text(
                    text = "Calories today",
                    color = TextPrimary.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = actual.toInt().toString(),
                        color = AccentLime,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = " / ${target.toInt()} kcal",
                        color = AccentLime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            // ---- RIGHT: macros ----
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MacroLine(label = "Protein", value = menu.actualTotalProtein)
                MacroLine(label = "Carbs",   value = menu.actualTotalCarb)
                MacroLine(label = "Fat",     value = menu.actualTotalFat)
            }
        }

        Spacer(Modifier.height(12.dp))

        // ---- Progress bar ----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedRatio.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(999.dp))
                    .background(AccentLime)
            )
        }
    }
}

@Composable
private fun MacroLine(
    label: String,
    value: Float
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)) {
        Text(
            text = label,
            color = TextSecondary.copy(alpha = 0.9f),
            fontSize = 11.sp
        )
        Text(
            text = "${value.toInt()} g",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}


@Composable
private fun MealCard(
    meal: Meal,
    onOpenRecipePicker: (Meal) -> Unit,
    onEditMealRecipe: (mealId: Long, mealRecipeId: Long) -> Unit
) {
    val title = when (meal.mealType) {
        MealType.BREAKFAST -> "Breakfast"
        MealType.LUNCH ->  "Lunch"
        MealType.DINNER ->  "Dinner"
        MealType.SNACK ->  "Snack"
        else        -> meal.mealType ?: "Meal ${meal.id}"
    }

    val iconEmoji = when (meal.mealType) {
        MealType.BREAKFAST  -> "\uD83E\uDDC0" // 🥐
        MealType.LUNCH ->  "\uD83C\uDF72" // 🍲
        MealType.DINNER ->  "\uD83C\uDF7D" // 🍽
        MealType.SNACK ->  "\uD83E\uDD67" // 🧇
        else        -> "\uD83C\uDF7D"
    }

    val calories = (meal.mealRecipes ?: emptyList())
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

            OutlinedButton(
                onClick = { onOpenRecipePicker(meal) },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, AccentLime),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = AccentLime
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = "Add recipe",
                    tint = AccentLime,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Add recipe", fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(10.dp))

        if (meal.mealRecipes.isNullOrEmpty()) {
            Text(
                text = "No recipes yet. Tap \"Add recipe\" to start.",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                meal.mealRecipes.forEach { mr ->
                    MealRecipeRow(
                        mealRecipe = mr,
                        onClick = { onEditMealRecipe(meal.id, mr.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MealRecipeRow(
    mealRecipe: MealRecipe,
    onClick: () -> Unit
) {
    val recipe = mealRecipe.recipe
    val name = recipe?.name ?: "Recipe #${mealRecipe.id}"
    val calories = mealRecipe.calories.takeIf { it > 0f } ?: recipe?.calories ?: 0f
    val protein = mealRecipe.protein.takeIf { it > 0f } ?: recipe?.protein ?: 0f
    val carbs   = mealRecipe.carbs.takeIf { it > 0f }   ?: recipe?.carbs   ?: 0f
    val fat     = mealRecipe.fat.takeIf { it > 0f }     ?: recipe?.fat     ?: 0f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundDark.copy(alpha = 0.6f))
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        // Thumbnail
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
                text = "${calories.toInt()} kcal · P ${protein.toInt()} · C ${carbs.toInt()} · F ${fat.toInt()}",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

