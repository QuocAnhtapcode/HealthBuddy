package com.example.healthbuddy.screens.menu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun MenuTodayScreen(
    menuViewModel: MenuViewModel,
    onOpenChat: () -> Unit,
    onOpenRecipePicker: (Meal) -> Unit,
    onEditMealRecipe: (mealId: Long, mealRecipeId: Long) -> Unit
) {
    val ui by menuViewModel.ui.collectAsState()
    var showPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        menuViewModel.loadMenuForToday()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        when {
            ui.loadingMenu -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentLime)
                }
            }

            ui.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Hôm nay bạn chưa ăn gì",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Hãy thêm bữa ăn đầu tiên của bạn",
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
                    onAddMealClick = { showPicker = true },
                    onOpenChat = onOpenChat,
                    onOpenRecipePicker = onOpenRecipePicker,
                    onEditMealRecipe = onEditMealRecipe,
                    onDeleteMealRecipe = {
                        menuViewModel.deleteRecipeInMeal(it)
                    }
                )
            }
        }

        MealTypePickerBottomSheet(
            visible = showPicker,
            onDismiss = { showPicker = false },
            onSelect = { type ->
                showPicker = false
                menuViewModel.addMeal(type)
            }
        )
    }
}

@Composable
private fun TodayMenuContent(
    menu: Menu,
    onAddMealClick: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenRecipePicker: (Meal) -> Unit,
    onEditMealRecipe: (mealId: Long, mealRecipeId: Long) -> Unit,
    onDeleteMealRecipe: (mealRecipeId: Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TodayHeader(
            menu = menu,
            onAddManual = { onAddMealClick() },
            onAddByAI = { onOpenChat() }
        )


        Spacer(Modifier.height(8.dp))

        if (menu.meals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Chưa có bữa ăn. Nhấn \"+\" để thêm",
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
                        onEditMealRecipe = onEditMealRecipe,
                        onDeleteMealRecipe = onDeleteMealRecipe
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun TodayHeader(
    menu: Menu,
    onAddManual: () -> Unit,
    onAddByAI: () -> Unit
) {
    val today = remember {
        LocalDate.now()
            .format(DateTimeFormatter.ofPattern("EEEE, dd MMM"))
    }

    var showAddOptions by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
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
            }

            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = "Add meal",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { showAddOptions = true }
            )
        }

        Spacer(Modifier.height(16.dp))
        TodaySummaryCard(menu)
    }

    if (showAddOptions) {
        AddOptionsBottomSheet(
            onDismiss = { showAddOptions = false },
            onManual = {
                showAddOptions = false
                onAddManual()
            },
            onAI = {
                showAddOptions = false
                onAddByAI()
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddOptionsBottomSheet(
    onDismiss: () -> Unit,
    onManual: () -> Unit,
    onAI: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(44.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White.copy(alpha = 0.12f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 18.dp)
        ) {
            Text(
                text = "Thêm bữa ăn",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Chọn cách bạn muốn tạo bữa ăn hôm nay",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(14.dp))

            AddOptionCard(
                title = "Thêm thủ công",
                subtitle = "Tự chọn món, chỉnh ingredient và khẩu phần",
                icon = { // icon bạn có thể thay bằng painterResource của bạn
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = AccentLime
                    )
                },
                accent = AccentLime,
                onClick = onManual
            )

            Spacer(Modifier.height(10.dp))

            AddOptionCard(
                title = "Chat với AI",
                subtitle = "Nhập sở thích / thời tiết / mục tiêu để AI gợi ý menu",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = null,
                        tint = LavenderBand
                    )
                },
                accent = LavenderBand,
                onClick = onAI
            )

            Spacer(Modifier.height(14.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Hủy", color = TextPrimary)
            }
        }
    }
}

@Composable
private fun AddOptionCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    accent: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(BackgroundDark.copy(alpha = 0.65f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .height(34.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(accent.copy(alpha = 0.16f))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Chọn", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}


@Composable
private fun TodaySummaryCard(menu: Menu) {
    val plan = menu.menuPlan

    val calTarget = plan.targetCalories
    val calActual = menu.actualTotalCalories
    val calRatioRaw = if (calTarget > 0f) calActual / calTarget else 0f
    val calRatio = calRatioRaw.coerceIn(0f, 1.3f)
    val animatedCalRatio by animateFloatAsState(
        targetValue = calRatio.coerceIn(0f, 1f),
        label = "CalProgress"
    )

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
                    text = "Calo hôm nay",
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

        Spacer(Modifier.height(12.dp))

        // progress bar của calories
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedCalRatio.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(999.dp))
                    .background(AccentLime)
            )
        }
    }
}

@Composable
fun MacroLine(
    label: String,
    actual: Float,
    target: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 10.sp
        )
        Text(
            text = "${actual.toInt()}/${target.toInt()} g",
            color = TextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}



@Composable
private fun MealCard(
    meal: Meal,
    onOpenRecipePicker: (Meal) -> Unit,
    onEditMealRecipe: (mealId: Long, mealRecipeId: Long) -> Unit,
    onDeleteMealRecipe: (mealRecipeId: Long) -> Unit
) {
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
                    .background(BackgroundDark.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = 20.sp
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
                Text("Thêm công thức", fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(10.dp))

        if (meal.mealRecipes.isNullOrEmpty()) {
            Text(
                text = "Chưa có công thức nấu ăn",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                meal.mealRecipes.forEach { mr ->
                    MealRecipeRow(
                        mealRecipe = mr,
                        onClick = { onEditMealRecipe(meal.id, mr.id) },
                        onDelete = {
                            onDeleteMealRecipe(mr.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MealRecipeRow(
    mealRecipe: MealRecipe,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val recipe = mealRecipe.recipe
    val name = recipe?.name ?: "Công thức #${mealRecipe.id}"

    var showDeleteConfirm by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                // Thay vì xóa ngay, ta hiện Dialog
                showDeleteConfirm = true
                // Trả về false để Item không bị biến mất ngay lập tức khỏi UI
                false
            } else false
        }
    )

    LaunchedEffect(showDeleteConfirm) {
        if (!showDeleteConfirm && dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissState.reset()
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = SurfaceDark,
            title = { Text("Xác nhận xóa", color = TextPrimary) },
            text = { Text("Bạn có muốn xóa \"$name\" không?", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete() // Thực hiện xóa
                        showDeleteConfirm = false
                    }
                ) {
                    Text("Xóa", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Hủy", color = TextPrimary)
                }
            }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val isDismissing = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
            val alpha by animateFloatAsState(if (isDismissing) 1f else 0f, label = "alpha")
            val scale by animateFloatAsState(if (isDismissing) 1.2f else 0.8f, label = "scale")

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDismissing) Color.Red.copy(alpha = 0.8f) else Color.Transparent)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = Color.White,
                    modifier = Modifier
                        .scale(scale)
                        .graphicsLayer(alpha = alpha)
                )
            }
        }
    ) {
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
                    text = "${mealRecipe.calories.toInt()} kcal · P ${"%.1f".format(mealRecipe.protein)} · C ${"%.1f".format(mealRecipe.carbs)} · F ${"%.1f".format(mealRecipe.fat)}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

