package com.example.healthbuddy.data.model

import com.example.healthbuddy.screens.menu.MealType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Menu(
    val id: Long,
    val menuPlan: MenuPlan,
    val notes: String?,
    val actualTotalCalories: Float=0f,
    val actualTotalProtein: Float=0f,
    val actualTotalCarb: Float=0f,
    val actualTotalFat: Float=0f,
    val meals: List<Meal>,
    val status: String?
)

@JsonClass(generateAdapter = true)
data class MenuPlan(
    val id: Long,
    val dayOfWeek: String,
    val targetCalories: Float,
    val targetProtein: Float,
    val targetCarb: Float,
    val targetFat: Float
)

@JsonClass(generateAdapter = true)
data class Meal(
    val id: Long,
    val mealType: MealType?,
    @Json(name = "mealRecipe")
    val mealRecipes: List<MealRecipe>?=mutableListOf()
)

@JsonClass(generateAdapter = true)
data class MealRecipe(
    val id: Long,

    val recipe: Recipe? = null,
    val meal: Meal? = null,
    // ingredients người dùng thực tế sử dụng
    val mealRecipeIngredients: List<MealRecipeIngredient>? = null,

    // tổng macro thực tế của mealRecipe
    val fat: Float=0f,
    val calories: Float=0f,
    val carbs: Float=0f,
    val protein: Float=0f
)

@JsonClass(generateAdapter = true)
data class Recipe(
    val id: Long,
    val name: String,
    val description: String?,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val imageUrl: String?,
    val imageId: String?,
    val type: String?,

    // nguyên liệu mẫu (fixed)
    @Json(name = "recipeIngredients")
    val recipeIngredients: List<RecipeIngredient>
)

@JsonClass(generateAdapter = true)
data class Ingredient(
    val id: Long,
    val name: String,
    val description: String?,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val imageUrl: String?,
    val imageId: String?
)

// nguyên liệu mẫu
@JsonClass(generateAdapter = true)
data class RecipeIngredient(
    val id: Long,
    val ingredient: Ingredient,
    val quantity: Float
)

// nguyên liệu người dùng nhập
@JsonClass(generateAdapter = true)
data class MealRecipeIngredient(
    val id: Long=0,
    val ingredient: Ingredient,
    val quantity: Float=0f
)

@JsonClass(generateAdapter = true)
data class EditMealRecipeIngredientRequest(
    val quantity: Float
)

@JsonClass(generateAdapter = true)
data class RecipePage(
    val content: List<Recipe>,
    val page: Page
)

@JsonClass(generateAdapter = true)
data class Page(
    val size: Int,
    val number: Int,
    val totalElements: Int,
    val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class MealRecipeRequest(
    val meal: IdRef,
    val recipe: IdRef
)

@JsonClass(generateAdapter = true)
data class IdRef(
    val id: Long
)

@JsonClass(generateAdapter = true)
data class MealUpdateRequest(
    val mealType : MealType
)
