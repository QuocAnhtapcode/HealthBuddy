package com.example.healthbuddy.data.repo

import com.example.healthbuddy.data.api.MenuApi
import com.example.healthbuddy.data.model.ChatHistoryItem
import com.example.healthbuddy.data.model.ChatHistoryResponse
import com.example.healthbuddy.data.model.ChatMenuPreview
import com.example.healthbuddy.data.model.EditMealRecipeIngredientRequest
import com.example.healthbuddy.data.model.IdRef
import com.example.healthbuddy.data.model.Meal
import com.example.healthbuddy.data.model.MealRecipe
import com.example.healthbuddy.data.model.MealRecipeRequest
import com.example.healthbuddy.data.model.Menu
import com.example.healthbuddy.data.model.RecipePage
import com.example.healthbuddy.data.model.RecommendChatRequest
import com.example.healthbuddy.data.model.SaveChatMenuRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepository @Inject constructor(
    private val api: MenuApi
) {

    suspend fun getMenuForToday(): Result<Menu> =
        runCatching { api.getMenuForToday() }

    suspend fun addMealToMenu(menuId: Long): Result<Meal> =
        runCatching { api.addMealToMenu(menuId) }

    suspend fun deleteRecipeInMeal(id: Long): Result<Unit> =
        runCatching { api.deleteMealInRecipe(id) }

    suspend fun updateMeal(mealId: Long, meal: Meal): Result<Meal> =
        runCatching { api.updateMeal(mealId, meal) }

    suspend fun loadRecipes(
        page: Int,
        size: Int = 10,
        search: String?
    ): Result<RecipePage> =
        runCatching { api.getRecipes(page = page, size = size, search = search) }

    suspend fun addRecipeToMeal(mealId: Long, recipeId: Long): Result<MealRecipe> {
        val body = MealRecipeRequest(
            meal = IdRef(mealId),
            recipe = IdRef(recipeId)
        )
        return runCatching { api.addRecipeToMeal(body) }
    }

    suspend fun updateMealRecipe(
        mealRecipeId: Long,
        quantity: Float
    ): Result<Unit> {
        return runCatching {
            api.updateRecipeInMeal(mealRecipeId,
                EditMealRecipeIngredientRequest(quantity)
            )
        }
    }

    suspend fun getChatHistory(): Result<ChatHistoryResponse>{
        return runCatching {
            api.getChatHistory()
        }
    }

    suspend fun sendMessageToChatBot(message: String): Result<ChatHistoryItem>{
        return runCatching {
            api.recommendMenuByChat(RecommendChatRequest(
                userPrompt = message
            ))
        }
    }
    suspend fun getMenuFromChat(id: Long): Result<ChatMenuPreview>{
        return runCatching {
            api.getMenuFromChat(id)
        }
    }
    suspend fun saveChatMenu(id: Long): Result<Unit>{
        return runCatching {
            api.saveChatMenu(
                SaveChatMenuRequest(chatId = id)
            )
        }
    }
}

