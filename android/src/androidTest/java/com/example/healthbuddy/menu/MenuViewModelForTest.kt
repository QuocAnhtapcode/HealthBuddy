package com.example.healthbuddy.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.data.model.*
import com.example.healthbuddy.data.repo.MenuRepository
import com.example.healthbuddy.screens.menu.MenuUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModelForTest(
    private val repo: MenuRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(MenuUiState())
    val ui = _ui.asStateFlow()

    fun loadMenuForToday() {
        viewModelScope.launch {
            _ui.update { it.copy(loadingMenu = true, error = null) }

            repo.getMenuForToday()
                .onSuccess { menu ->
                    _ui.update { it.copy(loadingMenu = false, menu = menu) }
                }
                .onFailure { e ->
                    _ui.update { it.copy(loadingMenu = false, error = e.message) }
                }
        }
    }

    fun loadRecipesFirstPage() {
        viewModelScope.launch {
            _ui.update { it.copy(loadingRecipes = true, error = null) }

            repo.loadRecipes(page = 0, size = 10, search = null)
                .onSuccess { pageResult ->
                    _ui.update {
                        it.copy(
                            loadingRecipes = false,
                            recipes = pageResult.content,
                            recipePage = pageResult.page,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingRecipes = false,
                            error = e.message
                        )
                    }
                }
        }
    }


    fun addRecipeToMeal(mealId: Long, recipeId: Long) {
        val menu = _ui.value.menu ?: return

        viewModelScope.launch {
            repo.addRecipeToMeal(mealId, recipeId)
                .onSuccess { mealRecipe ->
                    val updatedMeals = menu.meals.map {
                        if (it.id == mealId)
                            it.copy(mealRecipes = it.mealRecipes?.plus(mealRecipe))
                        else it
                    }

                    _ui.update {
                        it.copy(
                            menu = menu.copy(meals = updatedMeals),
                            editingMealRecipe = mealRecipe
                        )
                    }
                }
        }
    }

    fun startEditingMealRecipe(mealRecipe: MealRecipe) {
        _ui.update { it.copy(editingMealRecipe = mealRecipe) }
    }

    fun updateIngredientQuantity(ingredientId: Long, quantity: Float) {
        val current = _ui.value.editingMealRecipe ?: return

        val updated = current.mealRecipeIngredients?.map {
            if (it.ingredient.id == ingredientId)
                it.copy(quantity = quantity)
            else it
        }

        _ui.update {
            it.copy(editingMealRecipe = current.copy(mealRecipeIngredients = updated))
        }
    }
}
