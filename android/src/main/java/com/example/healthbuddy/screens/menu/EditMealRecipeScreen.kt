package com.example.healthbuddy.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthbuddy.R
import com.example.healthbuddy.data.model.MealRecipe
import com.example.healthbuddy.data.model.MealRecipeIngredient
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.ButtonBg
import com.example.healthbuddy.ui.theme.InputText
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMealRecipeScreen(
    vm: MenuViewModel,
    onBack: () -> Unit
) {
    val ui by vm.ui.collectAsState()
    val editing = ui.editingMealRecipe

    if (editing == null) {
        LaunchedEffect(Unit) {
            vm.loadMenuForToday()
        }
        return
    }

    val ingredients = editing.mealRecipeIngredients ?: emptyList()

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Adjust ingredients",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = AccentLime
                        )
                    }
                },
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
                .background(BackgroundDark)
        ) {
            // Header: tên món + macro tổng
            RecipeHeader(editing)

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(ingredients, key = { it.id }) { mealRecipeIngredient ->
                    IngredientEditRow(
                        mealRecipeIngredient = mealRecipeIngredient,
                        onQuantityChange = { newQuantity ->
                            vm.updateIngredientQuantity(
                                ingredientId = mealRecipeIngredient.ingredient.id,
                                newQuantity = newQuantity
                            )
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                }

                item { Spacer(Modifier.height(8.dp)) }
            }

            Button(
                onClick = {
                    vm.saveEditedMealRecipe(onDone = onBack)
                },
                enabled = !ui.loadingEdit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBg,
                    disabledContainerColor = ButtonBg.copy(alpha = 0.4f)
                )
            ) {
                if (ui.loadingEdit) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = AccentLime,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Save",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
@Composable
private fun RecipeHeader(mealRecipe: MealRecipe) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        Text(
            text = mealRecipe.recipe?.name ?: "No name",
            color = AccentLime,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        mealRecipe.recipe?.description?.let {
            Spacer(Modifier.height(6.dp))
            Text(
                text = it,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 17.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MacroChip(label = "Calories",value = mealRecipe.calories, unit = "kcal")
            MacroChip(label = "Protein",value = mealRecipe.protein,  unit = "g")
            MacroChip(label = "Carbs",value = mealRecipe.carbs,    unit = "g")
            MacroChip(label = "Fat",value = mealRecipe.fat,      unit = "g")
        }
    }
}

@Composable
private fun MacroChip(
    label: String,
    value: Float,
    unit: String
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp
        )
        Text(
            text = "${value.toInt()} $unit",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
@Composable
fun IngredientEditRow(
    mealRecipeIngredient: MealRecipeIngredient,
    onQuantityChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BackgroundDark.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mealRecipeIngredient.ingredient.name.firstOrNull()?.uppercase() ?: "",
                    color = AccentLime,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = mealRecipeIngredient.ingredient.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                mealRecipeIngredient.ingredient.description?.let {
                    Text(
                        text = it,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 2
                    )
                }
            }

            QuantityPillField(
                initial = mealRecipeIngredient.quantity,
                onQuantityChange = onQuantityChange
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MacroPill("Cal", mealRecipeIngredient.ingredient.calories)
            MacroPill("P",   mealRecipeIngredient.ingredient.protein)
            MacroPill("C",   mealRecipeIngredient.ingredient.carbs)
            MacroPill("F",   mealRecipeIngredient.ingredient.fat)
        }
    }
}


@Composable
private fun MacroPill(
    label: String,
    value: Float
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(BackgroundDark.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$label ${"%.1f".format(value)}",
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}
@Composable
private fun QuantityPillField(
    initial: Float,
    onQuantityChange: (Float) -> Unit
) {
    var text by remember(initial) {
        mutableStateOf(
            if (initial > 0f) {
                if (initial % 1f == 0f) initial.toInt().toString() else initial.toString()
            } else ""
        )
    }
    var isFocused by remember { mutableStateOf(false) }

    val borderColor =
        if (isFocused) AccentLime else Color.White.copy(alpha = 0.06f)

    BasicTextField(
        value = text,
        onValueChange = { new ->
            // Chỉ cho số + dấu chấm
            val filtered = new.filter { it.isDigit() || it == '.' }

            text = filtered

            val q = filtered.toFloatOrNull() ?: 0f
            onQuantityChange(q)
        },
        singleLine = true,
        textStyle = TextStyle(
            color = TextPrimary,
            fontSize = 14.sp,
            textAlign = TextAlign.End
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        cursorBrush = SolidColor(AccentLime),
        modifier = Modifier
            .width(96.dp)
            .height(40.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .clip(RoundedCornerShape(999.dp))
            .background(SurfaceDark)
            .border(1.dp, borderColor, RoundedCornerShape(999.dp)),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = "0",
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }
                    innerTextField()
                }

                Spacer(Modifier.width(6.dp))

                Text(
                    text = "g",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    )
}

