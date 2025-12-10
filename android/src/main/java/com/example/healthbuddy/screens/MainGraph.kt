package com.example.healthbuddy.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.healthbuddy.R
import com.example.healthbuddy.data.model.HealthInfo
import com.example.healthbuddy.data.model.HealthInfoRequest
import com.example.healthbuddy.data.model.User
import com.example.healthbuddy.screens.auth.AuthViewModel
import com.example.healthbuddy.screens.goal.GoalViewModel
import com.example.healthbuddy.screens.menu.EditMealRecipeScreen
import com.example.healthbuddy.screens.profile.ProfileOverviewScreen
import com.example.healthbuddy.screens.menu.MenuTodayScreen
import com.example.healthbuddy.screens.menu.MenuViewModel
import com.example.healthbuddy.screens.menu.RecipeDetailScreen
import com.example.healthbuddy.screens.menu.RecipePickerScreen
import com.example.healthbuddy.screens.profile.UpdateHealthInfoScreen
import com.example.healthbuddy.screens.userinfo.UserInfoViewModel
import com.example.healthbuddy.screens.workout.AddExerciseScreen
import com.example.healthbuddy.screens.workout.ExercisePickerScreen
import com.example.healthbuddy.screens.workout.TodayWorkoutScreen
import com.example.healthbuddy.screens.workout.WorkoutViewModel
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary

sealed class Tab(val route: String, val label: String, val icon: Int) {
    data object Home      : Tab("home",      "Home",      R.drawable.ic_home)
    data object Workout   : Tab("workout",   "Work out",  R.drawable.ic_dumbbell)
    data object Nutrition : Tab("nutrition", "Nutrition", R.drawable.ic_nutrition)
    data object Profile   : Tab("profile",   "Profile",   R.drawable.ic_profile)
}

private val TABS = listOf(Tab.Home, Tab.Workout, Tab.Nutrition, Tab.Profile)

@Composable
fun MainScreenGraph(
    authViewModel: AuthViewModel,
    goalViewModel: GoalViewModel,
    userInfoViewModel: UserInfoViewModel,
    menuViewModel: MenuViewModel,
    workoutViewModel: WorkoutViewModel
) {
    val tabNav = rememberNavController()
    val backStack by tabNav.currentBackStackEntryAsState()
    val currentDestination = backStack?.destination

    val showBar = currentDestination
        ?.hierarchy
        ?.any { d -> TABS.any { it.route == d.route } } == true

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = {
            if (showBar) {
                BottomBar(
                    currentDestination = currentDestination,
                    onSelect = { route ->
                        tabNav.navigate(route) {
                            popUpTo(tabNav.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { inner ->
        NavHost(
            navController = tabNav,
            startDestination = Tab.Home.route,
            modifier = Modifier.padding(inner)
        ) {

            /* ------------ HOME TAB ------------ */
            composable(Tab.Home.route) {
                val ui by userInfoViewModel.ui.collectAsState()

                HomeScreen(
                    user = ui.user,
                    healthInfo = ui.healthInfo
                )
            }

            /* ------- WORKOUT TAB -------- */
            navigation(startDestination = "workout/today", route = Tab.Workout.route) {

                composable("workout/today") {
                    val userUi by userInfoViewModel.ui.collectAsState()
                    val activityLevel = userUi.user?.activityLevel?.lowercase() ?:"beginner"

                    TodayWorkoutScreen(
                        viewModel = workoutViewModel,
                        userActivityLevel = activityLevel,
                        onOpenExercisePicker = {
                            tabNav.navigate("workout/exercises")
                        }
                    )
                }

                composable("workout/exercises") {
                    val userUi by userInfoViewModel.ui.collectAsState()
                    val activityLevel = userUi.user?.activityLevel?.lowercase() ?:"beginner"

                    ExercisePickerScreen(
                        viewModel = workoutViewModel,
                        userActivityLevel = activityLevel,
                        onBack = { tabNav.popBackStack() },
                        onExerciseSelected = { exerciseId ->
                            tabNav.navigate("workout/add/$exerciseId")
                        }
                    )
                }

                composable("workout/add/{exerciseId}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("exerciseId")!!.toLong()
                    AddExerciseScreen(
                        viewModel = workoutViewModel,
                        exerciseId = id,
                        onBack = {
                            tabNav.navigate("workout/today") {
                                popUpTo("workout/today") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            navigation(startDestination = "nutrition/menu", route = Tab.Nutrition.route) {

                composable("nutrition/menu") {
                    MenuTodayScreen(
                        menuViewModel = menuViewModel,
                        onOpenRecipePicker = { meal ->
                            tabNav.navigate("nutrition/recipePicker/${meal.id}")
                        },
                        onEditMealRecipe = { mealId, mealRecipeId ->
                            tabNav.navigate("nutrition/meal/$mealId/recipe/$mealRecipeId")
                        }
                    )
                }

                composable("nutrition/recipePicker/{mealId}") { backStack ->
                    val mealId = backStack.arguments?.getString("mealId")?.toLongOrNull()
                        ?: return@composable

                    RecipePickerScreen(
                        vm = menuViewModel,
                        onBack = { tabNav.popBackStack() },
                        onDetail = { recipeId ->
                            // Đẩy cả mealId + recipeId sang detail
                            tabNav.navigate("recipe/detail/$mealId/$recipeId")
                        }
                    )
                }

                composable("recipe/detail/{mealId}/{recipeId}") { backStackEntry ->
                    val mealId = backStackEntry.arguments?.getString("mealId")!!.toLong()
                    val recipeId = backStackEntry.arguments?.getString("recipeId")!!.toLong()

                    val ui by menuViewModel.ui.collectAsState()
                    val recipe = ui.recipes.first { it.id == recipeId }

                    RecipeDetailScreen(
                        mealId = mealId,
                        recipe = recipe,
                        vm = menuViewModel,
                        onBack = { tabNav.popBackStack() },
                        onAdded = {
                            tabNav.navigate("nutrition/menu") {
                                popUpTo(0) { inclusive = true }     // XÓA TOÀN BỘ BACKSTACK
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(
                    route = "nutrition/meal/{mealId}/recipe/{mealRecipeId}"
                ) { backStackEntry ->
                    val mealId = backStackEntry
                        .arguments
                        ?.getString("mealId")
                        ?.toLongOrNull() ?: return@composable

                    val mealRecipeId = backStackEntry
                        .arguments
                        ?.getString("mealRecipeId")
                        ?.toLongOrNull() ?: return@composable

                    // Lấy MealRecipe từ ui (menu hiện tại)
                    val ui by menuViewModel.ui.collectAsState()

                    val mealRecipe = ui.menu
                        ?.meals
                        ?.firstOrNull { it.id == mealId }
                        ?.mealRecipes
                        ?.firstOrNull { it.id == mealRecipeId }

                    if (mealRecipe == null) {
                        LaunchedEffect(Unit) { tabNav.popBackStack() }
                        return@composable
                    }

                    LaunchedEffect(mealRecipeId) {
                        menuViewModel.startEditingMealRecipe(mealRecipe)
                    }

                    EditMealRecipeScreen(
                        vm = menuViewModel,
                        onBack = {
                            tabNav.navigate("nutrition/menu") {
                                popUpTo(0) { inclusive = true }     // XÓA TOÀN BỘ BACKSTACK
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            /* ---------- PROFILE TAB ---------- */
            composable(Tab.Profile.route) {
                val ui by userInfoViewModel.ui.collectAsState()

                when {
                    ui.error != null -> {
                        Text(
                            text = ui.error ?: "Error loading profile",
                            color = Color.Red,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    ui.user != null -> {
                        ProfileOverviewScreen(
                            user = ui.user!!,
                            healthInfo = ui.healthInfo!!,
                            avatarUrl = null,
                            onBack = { },
                            onEditProfile = {
                                tabNav.navigate(
                                    route = "profile/edit"
                                )
                            },
                            onChooseNewPlan = {

                            },
                            onOpenPrivacy = { },
                            onOpenSettings = { },
                            onOpenHelp = { },
                            onLogout = {
                                authViewModel.logout()
                            }
                        )
                    }
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AccentLime)
                        }
                    }
                }
            }
            composable(route = "profile/edit") {
                val ui by userInfoViewModel.ui.collectAsState()

                UpdateHealthInfoScreen(
                    user = ui.user!!,
                    healthInfo = ui.healthInfo!!,
                    avatarUrl = null,
                    onBack = {
                        tabNav.popBackStack()
                    },
                    onUpdate = { healthInfo ->
                        userInfoViewModel.submit(HealthInfoRequest(
                            healthInfo.height,
                            healthInfo.weight,
                            healthInfo.fatPercentage
                        ))
                        tabNav.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    currentDestination: NavDestination?,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LavenderBand)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TABS.forEach { tab ->
            val selected =
                currentDestination?.hierarchy?.any { it.route == tab.route } == true

            BottomBarIcon(
                icon = tab.icon,
                contentDescription = tab.label,
                selected = selected,
                onClick = { onSelect(tab.route) }
            )
        }
    }
}

@Composable
private fun BottomBarIcon(
    icon: Int,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val targetSize = if (selected) 30.dp else 22.dp
    val size by animateDpAsState(targetValue = targetSize, label = "iconSize")
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(
                    bounded = false,
                    radius = 22.dp,
                    color = Color.White.copy(alpha = 0.2f)
                ),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(size)
        )
    }
}


@Composable
private fun HomeScreen(
    user: User?,
    healthInfo: HealthInfo?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(20.dp)
    ) {
        Text(
            text = "Hi, ${user?.username ?: "Athlete"} 👋",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Ready for today’s workout and meals?",
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(20.dp))

        healthInfo?.let {
            Text(
                text = "BMI: ${"%.1f".format(it.bmi)}   BMR: ${"%.0f".format(it.bmr)} kcal",
                color = AccentLime,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmptyStateScreen(
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(subtitle, color = TextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}
