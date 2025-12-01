package com.example.healthbuddy.data.api

import com.example.healthbuddy.data.model.EditMealRecipeIngredientRequest
import com.example.healthbuddy.data.model.Meal
import com.example.healthbuddy.data.model.MealRecipe
import com.example.healthbuddy.data.model.MealRecipeIngredient
import com.example.healthbuddy.data.model.MealRecipeRequest
import com.example.healthbuddy.data.model.MealUpdateRequest
import com.example.healthbuddy.data.model.Menu
import com.example.healthbuddy.data.model.RecipePage
import com.example.healthbuddy.screens.menu.MealType
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MenuApi {

    // 1) Lấy menu cho hôm nay
    @GET("menu/today")
    suspend fun getMenuForToday(): Menu

    // 2) Thêm meal rỗng vào menu
    @POST("meals/menu/{id}")
    suspend fun addMealToMenu(
        @Path("id") menuId: Long
    ): Meal

    @PUT("meals/{id}")
    suspend fun updateMeal(
        @Path("id") mealId: Long,
        @Body meal: Meal
    ): Meal

    // 3) Lấy danh sách recipe (paged)
    @GET("recipes")
    suspend fun getRecipes(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("name") search: String? = null
    ): RecipePage

    // 4) Thêm recipe vào meal
    @POST("meal-recipes")
    suspend fun addRecipeToMeal(
        @Body mealRecipe: MealRecipeRequest
    ): MealRecipe

    // 5) Update lại khối lượng nguyên liệu trong mealRecipe
    @PUT("/meal-recipe-ingredients/{id}")
    suspend fun updateRecipeInMeal(
        @Path("id") id: Long,
        @Body editMealRecipeIngredientRequest: EditMealRecipeIngredientRequest
    ): MealRecipe
}



