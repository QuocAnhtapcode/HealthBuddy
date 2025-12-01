package com.example.healthbuddy.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.data.model.Meal
import com.example.healthbuddy.data.model.MealRecipe
import com.example.healthbuddy.data.model.Menu
import com.example.healthbuddy.data.model.Page
import com.example.healthbuddy.data.model.Recipe
import com.example.healthbuddy.data.repo.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MenuUiState(
    val loadingMenu: Boolean = false,
    val menu: Menu? = null,

    val loadingRecipes: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val recipePage: Page? = null,
    val searchQuery: String = "",

    val loadingEdit: Boolean = false,
    val editingMealRecipe: MealRecipe? = null,

    val error: String? = null
)

@HiltViewModel
class MenuViewModel @Inject constructor(
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

    fun addMeal(mealType: MealType) {
        val currentMenu = _ui.value.menu ?: return
        viewModelScope.launch {
            repo.addMealToMenu(currentMenu.id)
                .onSuccess { newMeal ->
                    val newMealUpdated = newMeal.copy(mealType=mealType)
                    val updatedMeals = currentMenu.meals + newMealUpdated
                    updateMealType(newMeal.id, newMealUpdated)
                    _ui.update {
                        it.copy(menu = currentMenu.copy(meals = updatedMeals))
                    }
                }
                .onFailure { e ->
                    _ui.update { it.copy(error = e.message) }
                }
        }
    }
    fun updateMealType(id: Long,meal: Meal){
        viewModelScope.launch {
            repo.updateMeal(id, meal)
                .onSuccess {

                }
                .onFailure { e ->
                    _ui.update { it.copy(error = e.message) }
                }
        }
    }

    fun loadRecipesFirstPage() {
        loadRecipesInternal(page = 0, reset = true)
    }

    // load thêm page tiếp theo
    fun loadNextRecipePage() {
        val pageInfo = _ui.value.recipePage ?: return
        val isLastPage = pageInfo.number >= pageInfo.totalPages - 1
        if (isLastPage || _ui.value.loadingRecipes) return

        loadRecipesInternal(page = pageInfo.number + 1, reset = false)
    }

    // cập nhật query khi user gõ search
    fun updateRecipeSearchQuery(newQuery: String) {
        _ui.update { it.copy(searchQuery = newQuery) }
    }

    // user nhấn Search / Done → reload từ page 0
    fun searchRecipes() {
        loadRecipesInternal(page = 0, reset = true)
    }

    private fun loadRecipesInternal(
        page: Int,
        reset: Boolean
    ) {
        viewModelScope.launch {
            _ui.update { it.copy(loadingRecipes = true, error = null) }

            val query = _ui.value.searchQuery.ifBlank { null }

            repo.loadRecipes(page = page, size = 10, search = query)
                .onSuccess { pageResult ->
                    _ui.update { old ->
                        val newList =
                            if (reset) pageResult.content
                            else old.recipes + pageResult.content

                        old.copy(
                            loadingRecipes = false,
                            recipes = newList,
                            recipePage = pageResult.page,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingRecipes = false,
                            error = e.message ?: "Error loading recipes"
                        )
                    }
                }
        }
    }

    // --- 4) Add recipe vào 1 meal cụ thể ---
    fun addRecipeToMeal(
        mealId: Long,
        recipeId: Long,
        onDone: () -> Unit = {}
    ) {
        val currentMenu = _ui.value.menu ?: return

        viewModelScope.launch {
            repo.addRecipeToMeal(mealId, recipeId)
                .onSuccess { mealRecipe ->
                    val updatedMeals = currentMenu.meals.map { meal ->
                        if (meal.id == mealId) {
                            val newList = meal.mealRecipes?.plus(mealRecipe)
                            meal.copy(mealRecipes = newList)
                        } else meal
                    }

                    _ui.update {
                        it.copy(
                            menu = currentMenu.copy(meals = updatedMeals),
                            editingMealRecipe = mealRecipe
                        )
                    }
                    onDone()
                }
                .onFailure { e ->
                    _ui.update { it.copy(error = e.message) }
                }
        }
    }

    fun startEditingMealRecipe(mealRecipe: MealRecipe) {
        _ui.update { it.copy(editingMealRecipe = mealRecipe) }
    }

    // --- 5) Update local quantity từng ingredient trước khi gửi lên server ---
    fun updateIngredientQuantity(ingredientId: Long, newQuantity: Float) {
        val current = _ui.value.editingMealRecipe ?: return

        val newList = current.mealRecipeIngredients?.map { mri ->
            if (mri.ingredient.id == ingredientId) mri.copy(quantity = newQuantity)
            else mri
        } ?: emptyList()

        // Re-calc macro tổng từ các ingredient nếu bạn muốn
        val newCalories = newList.sumOf { (it.quantity * it.ingredient.calories).toDouble() }.toFloat()
        val newProtein  = newList.sumOf { (it.quantity * it.ingredient.protein).toDouble() }.toFloat()
        val newCarbs    = newList.sumOf { (it.quantity * it.ingredient.carbs).toDouble() }.toFloat()
        val newFat      = newList.sumOf { (it.quantity * it.ingredient.fat).toDouble() }.toFloat()

        _ui.update {
            it.copy(
                editingMealRecipe = current.copy(
                    mealRecipeIngredients = newList,
                    calories = newCalories,
                    protein = newProtein,
                    carbs = newCarbs,
                    fat = newFat
                )
            )
        }
    }

    // --- 6) Gửi request updateMealRecipe lên backend ---
    fun saveEditedMealRecipe(onDone: () -> Unit = {}) {
        val editing = _ui.value.editingMealRecipe ?: return

        viewModelScope.launch {
            _ui.update { it.copy(loadingEdit = true, error = null) }

            val ingredients = editing.mealRecipeIngredients ?: emptyList()

            // 1) Gửi từng request update quantity
            for (item in ingredients) {
                val result = repo.updateMealRecipe(item.id,item.quantity)

                result.onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingEdit = false,
                            error = e.message ?: "Lỗi khi cập nhật nguyên liệu"
                        )
                    }
                    return@launch
                }
            }

            // 2) Sau khi tất cả đều OK, reload lại menu để sync macro, calories…
            val menuResult = repo.getMenuForToday()

            menuResult
                .onSuccess { newMenu ->
                    _ui.update {
                        it.copy(
                            loadingEdit = false,
                            menu = newMenu,
                            editingMealRecipe = null   // đóng mode edit
                        )
                    }
                    onDone()
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingEdit = false,
                            error = e.message ?: "Lỗi khi reload menu"
                        )
                    }
                }
        }
    }
}

