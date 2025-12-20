package com.example.healthbuddy.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.healthbuddy.data.model.Recipe
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary
import com.example.healthbuddy.screens.component.CustomSearchBar
import com.example.healthbuddy.screens.component.LoadMoreFooter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipePickerScreen(
    vm: MenuViewModel,
    onBack: () -> Unit,
    onDetail: (Long) -> Unit
) {
    val ui by vm.ui.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadRecipesFirstPage()
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chọn một công thức",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
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
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(BackgroundDark)
        ) {

            // ---------- SEARCH BAR ----------
            CustomSearchBar(
                query = ui.searchQuery,
                onQueryChange = { vm.updateRecipeSearchQuery(it) },
                onSearch = { vm.searchRecipes() }
            )

            Spacer(Modifier.height(8.dp))

            // ---------- LIST ----------
            when {
                ui.loadingRecipes && ui.recipes.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentLime)
                    }
                }

                ui.error != null && ui.recipes.isEmpty() -> {
                    Text(
                        text = ui.error ?: "Error",
                        color = Color.Red,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                else -> {
                    val hasMore = ui.recipePage?.let { p ->
                        p.number < p.totalPages - 1
                    } ?: false

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(ui.recipes) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = { recipeId->
                                    onDetail(recipeId)
                                }
                            )
                        }

                        item {
                            if (hasMore) {
                                LoadMoreFooter(
                                    loading = ui.loadingRecipes,
                                    onLoadMore = { vm.loadNextRecipePage() }
                                )
                            } else if (!ui.loadingRecipes && ui.recipes.isNotEmpty()) {
                                Text(
                                    text = "Hết",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    onClick: (Long) -> Unit
) {
    val macroText =
        "${recipe.calories.toInt()} kcal  •  P ${recipe.protein.toInt()}g  •  C ${recipe.carbs.toInt()}g  •  F ${recipe.fat.toInt()}g"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .clickable(onClick = {
                onClick(recipe.id)
            })
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF2B2B2B)),
            contentAlignment = Alignment.Center
        ) {
            if (!recipe.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_nutrition),
                    contentDescription = null,
                    tint = AccentLime.copy(alpha = 0.7f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Text bên phải
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = recipe.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (!recipe.type.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(LavenderBand.copy(alpha = 0.25f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = recipe.type.uppercase(),
                            color = AccentLime,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // Description ngắn
            if (!recipe.description.isNullOrBlank()) {
                Text(
                    text = recipe.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
            }

            // Macro
            Text(
                text = macroText,
                color = AccentLime,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


