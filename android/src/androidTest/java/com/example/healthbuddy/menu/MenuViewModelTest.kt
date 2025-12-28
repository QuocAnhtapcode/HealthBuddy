package com.example.healthbuddy.menu

import com.example.healthbuddy.MainDispatcherRule
import com.example.healthbuddy.data.repo.MenuRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadMenuForToday_success_setsMenu() = runTest {
        val api = FakeMenuApi()
        val vm = MenuViewModelForTest(MenuRepository(api))

        vm.loadMenuForToday()
        advanceUntilIdle()

        val ui = vm.ui.value
        assertThat(ui.loadingMenu).isFalse()
        assertThat(ui.menu).isNotNull()
        assertThat(ui.error).isNull()
    }

    @Test
    fun loadMenuForToday_fail_setsError() = runTest {
        val api = FakeMenuApi().apply {
            menuResult = Result.failure(RuntimeException("Network error"))
        }
        val vm = MenuViewModelForTest(MenuRepository(api))

        vm.loadMenuForToday()
        advanceUntilIdle()

        val ui = vm.ui.value
        assertThat(ui.menu).isNull()
        assertThat(ui.error).isEqualTo("Network error")
    }

    @Test
    fun loadRecipesFirstPage_success_setsRecipes() = runTest {
        val api = FakeMenuApi()
        val vm = MenuViewModelForTest(MenuRepository(api))

        vm.loadRecipesFirstPage()
        advanceUntilIdle()

        val ui = vm.ui.value
        assertThat(ui.loadingRecipes).isFalse()
        assertThat(ui.recipes).hasSize(2)
    }

    @Test
    fun loadRecipesFirstPage_fail_setsError() = runTest {
        val api = FakeMenuApi().apply {
            recipePageResult = Result.failure(RuntimeException("Timeout"))
        }
        val vm = MenuViewModelForTest(MenuRepository(api))

        vm.loadRecipesFirstPage()
        advanceUntilIdle()

        val ui = vm.ui.value
        assertThat(ui.loadingRecipes).isFalse()
        assertThat(ui.error).isEqualTo("Timeout")
    }


    @Test
    fun addRecipeToMeal_updatesMenuAndEditingRecipe() = runTest {
        val api = FakeMenuApi()
        val vm = MenuViewModelForTest(MenuRepository(api))

        vm.loadMenuForToday()
        advanceUntilIdle()

        vm.addRecipeToMeal(mealId = 1, recipeId = 1)
        advanceUntilIdle()

        val ui = vm.ui.value
        assertThat(ui.editingMealRecipe).isNotNull()
        assertThat(ui.menu?.meals?.first()?.mealRecipes).isNotEmpty()
    }

    @Test
    fun updateIngredientQuantity_updatesEditingMealRecipeOnly() = runTest {
        val vm = MenuViewModelForTest(MenuRepository(FakeMenuApi()))

        val mealRecipe = fakeMealRecipe(1)
        vm.startEditingMealRecipe(mealRecipe)

        vm.updateIngredientQuantity(
            ingredientId = mealRecipe.mealRecipeIngredients!!.first().ingredient.id,
            quantity = 200f
        )

        val updated = vm.ui.value.editingMealRecipe!!
        assertThat(updated.mealRecipeIngredients!!.first().quantity).isEqualTo(200f)
    }
}
