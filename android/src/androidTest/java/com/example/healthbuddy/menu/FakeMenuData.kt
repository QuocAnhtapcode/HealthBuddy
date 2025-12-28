package com.example.healthbuddy.menu

import com.example.healthbuddy.screens.menu.MealType
import com.example.healthbuddy.data.model.*

fun fakeMenuPlan() = MenuPlan(
    id = 1,
    dayOfWeek = "MONDAY",
    targetCalories = 2000f,
    targetProtein = 120f,
    targetCarb = 250f,
    targetFat = 60f
)

fun fakeIngredient(id: Long, name: String) = Ingredient(
    id = id,
    name = name,
    description = null,
    calories = 50f,
    protein = 5f,
    carbs = 6f,
    fat = 2f,
    imageUrl = null,
    imageId = null
)

fun fakeMealRecipeIngredient(id: Long) = MealRecipeIngredient(
    id = id,
    ingredient = fakeIngredient(id, "Rice"),
    quantity = 100f
)

fun fakeRecipe(id: Long) = Recipe(
    id = id,
    name = "Chicken Rice",
    description = "Healthy meal",
    calories = 500f,
    protein = 30f,
    carbs = 60f,
    fat = 10f,
    imageUrl = null,
    imageId = null,
    type = "MAIN",
    recipeIngredients = emptyList()
)

fun fakeMealRecipe(id: Long) = MealRecipe(
    id = id,
    recipe = fakeRecipe(1),
    mealRecipeIngredients = listOf(fakeMealRecipeIngredient(1)),
    calories = 500f,
    protein = 30f,
    carbs = 60f,
    fat = 10f
)

fun fakeMeal(id: Long) = Meal(
    id = id,
    mealType = MealType.LUNCH,
    mealRecipes = listOf(fakeMealRecipe(1))
)

fun fakeMenu() = Menu(
    id = 100,
    menuPlan = fakeMenuPlan(),
    notes = null,
    actualTotalCalories = 500f,
    actualTotalProtein = 30f,
    actualTotalCarb = 60f,
    actualTotalFat = 10f,
    meals = listOf(fakeMeal(1)),
    status = "ACTIVE"
)

fun fakeRecipePage() = RecipePage(
    content = listOf(fakeRecipe(1), fakeRecipe(2)),
    page = Page(size = 10, number = 0, totalElements = 2, totalPages = 1)
)

