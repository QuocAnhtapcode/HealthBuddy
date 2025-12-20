package com.example.healthbuddy.screens.menu

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.healthbuddy.R
import com.example.healthbuddy.data.model.Recipe
import com.example.healthbuddy.data.model.RecipeIngredient
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.ButtonBg
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    mealId: Long,
    recipe: Recipe,
    vm: MenuViewModel,
    onBack: () -> Unit,
    onAdded: () -> Unit
) {
    var adding by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chi tiết công thức",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (!adding) {
                            adding = true
                            vm.addRecipeToMeal(
                                mealId = mealId,
                                recipeId = recipe.id,
                                onDone = {
                                    adding = false
                                    onAdded()
                                }
                            )
                        }
                    },
                    enabled = !adding,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonBg,
                        disabledContainerColor = ButtonBg.copy(alpha = 0.4f)
                    )
                ) {
                    if (adding) {
                        CircularProgressIndicator(
                            color = AccentLime,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Thêm vào bữa ăn",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
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
        ) {
            // -------- IMAGE --------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                if (recipe.imageUrl != null) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = recipe.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onState = { /* có thể handle error nếu muốn */ }
                    )
                } else {
                    Text(
                        text = recipe.name.take(1).uppercase(),
                        color = AccentLime,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // -------- NAME + MACROS --------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = recipe.name,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = recipe.type ?: "Công thức",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MacroChip(
                        label = "Kcal",
                        value = recipe.calories.toInt().toString()
                    )
                    MacroChip(
                        label = "Đạm",
                        value = "${"%.2f".format(recipe.protein)} g"
                    )
                    MacroChip(
                        label = "Tinh bột",
                        value = "${"%.2f".format(recipe.carbs)} g"
                    )
                    MacroChip(
                        label = "Chất béo",
                        value = "${"%.2f".format(recipe.fat)} g"
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // -------- DESCRIPTION --------
            if (!recipe.description.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Mô tả",
                        color = AccentLime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = recipe.description.orEmpty(),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(Modifier.height(16.dp))
            }

            // -------- INGREDIENTS --------
            Text(
                text = "Nguyên liệu",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recipe.recipeIngredients.forEach { ri ->
                    IngredientRow(ri)
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun MacroChip(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = AccentLime,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun IngredientRow(
    ri: RecipeIngredient
) {
    val ing = ri.ingredient

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // avatar / image
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF262626)),
            contentAlignment = Alignment.Center
        ) {
            if (!ing.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ing.imageUrl,
                    contentDescription = ing.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = ing.name.take(1).uppercase(),
                    color = AccentLime,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = ing.name,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!ing.description.isNullOrBlank()) {
                Text(
                    text = ing.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${ri.quantity.toInt()} g · " +
                    "${ing.calories} kcal / 1g",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
