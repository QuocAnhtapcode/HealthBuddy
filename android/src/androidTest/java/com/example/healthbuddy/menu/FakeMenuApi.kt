package com.example.healthbuddy.menu

import com.example.healthbuddy.data.api.MenuApi
import com.example.healthbuddy.data.model.*

class FakeMenuApi : MenuApi {

    var menuResult: Result<Menu> = Result.success(fakeMenu())
    var recipePageResult: Result<RecipePage> = Result.success(fakeRecipePage())
    var addMealResult: Result<Meal> = Result.success(fakeMeal(99))
    var addRecipeResult: Result<MealRecipe> = Result.success(fakeMealRecipe(99))
    var updateIngredientResult: Result<Unit> = Result.success(Unit)
    var deleteRecipeResult: Result<Unit> = Result.success(Unit)

    override suspend fun getMenuForToday(): Menu =
        menuResult.getOrElse { throw it }

    override suspend fun addMealToMenu(id: Long): Meal =
        addMealResult.getOrElse { throw it }

    override suspend fun updateMeal(mealId: Long, meal: Meal): Meal =
        meal

    override suspend fun getRecipes(
        page: Int,
        size: Int,
        search: String?
    ): RecipePage =
        recipePageResult.getOrElse { throw it }

    override suspend fun addRecipeToMeal(body: MealRecipeRequest): MealRecipe =
        addRecipeResult.getOrElse { throw it }

    override suspend fun deleteMealInRecipe(id: Long) {
        deleteRecipeResult.getOrElse { throw it }
    }

    override suspend fun updateRecipeInMeal(
        id: Long,
        editMealRecipeIngredientRequest: EditMealRecipeIngredientRequest
    ): MealRecipeIngredient {
        updateIngredientResult.getOrElse { throw it }
        return fakeMealRecipeIngredient(id)
    }

    override suspend fun getChatHistory() = error("Not used")
    override suspend fun recommendMenuByChat(request: RecommendChatRequest) = error("Not used")
    override suspend fun getMenuFromChat(chatId: Long) = error("Not used")
    override suspend fun saveChatMenu(request: SaveChatMenuRequest) {}
}
