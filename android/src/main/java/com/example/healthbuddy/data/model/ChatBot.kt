package com.example.healthbuddy.data.model

import com.example.healthbuddy.screens.menu.MealType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Chatbot menu preview: id có thể null
@JsonClass(generateAdapter = true)
data class ChatMenuPreview(
    val id: Long? = null,
    val menuPlan: MenuPlan,
    val notes: String? = null,
    val actualTotalCalories: Float = 0f,
    val actualTotalProtein: Float = 0f,
    val actualTotalCarb: Float = 0f,
    val actualTotalFat: Float = 0f,
    val meals: List<ChatMealPreview> = emptyList(),
    val status: String? = null
)

@JsonClass(generateAdapter = true)
data class ChatMealPreview(
    val id: Long? = null,
    val mealType: MealType?,
    @Json(name = "mealRecipe")
    val mealRecipes: List<ChatMealRecipePreview> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ChatMealRecipePreview(
    val id: Long? = null,
    val recipe: Recipe? = null,
    val meal: ChatMealPreview? = null,
    val mealRecipeIngredients: List<MealRecipeIngredientPreview>? = null,
    val fat: Float = 0f,
    val calories: Float = 0f,
    val carbs: Float = 0f,
    val protein: Float = 0f
)

@JsonClass(generateAdapter = true)
data class MealRecipeIngredientPreview(
    val id: Long?,
    val ingredient: Ingredient,
    val quantity: Float=0f
)

@JsonClass(generateAdapter = true)
data class ChatHistoryResponse(
    val content: List<ChatHistoryItem>,
    val page: Page
)

@JsonClass(generateAdapter = true)
data class ChatHistoryItem(
    val id: Long,
    val userPrompt: String,
    val aiResponse: String,
    val category: String
)

@JsonClass(generateAdapter = true)
data class RecommendChatRequest(
    val userPrompt: String
)

@JsonClass(generateAdapter = true)
data class SaveChatMenuRequest(
    val chatId: Long
)
