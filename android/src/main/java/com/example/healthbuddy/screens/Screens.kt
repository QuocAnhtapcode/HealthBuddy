package com.example.healthbuddy.screens

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.healthbuddy.R
import com.example.healthbuddy.data.model.HealthInfoRequest
import com.example.healthbuddy.data.model.SignUpRequest
import com.example.healthbuddy.screens.auth.AuthViewModel
import com.example.healthbuddy.screens.auth.ForgotPasswordScreen
import com.example.healthbuddy.screens.auth.LoginScreen
import com.example.healthbuddy.screens.auth.RegisterScreen
import com.example.healthbuddy.screens.auth.ResetPasswordScreen
import com.example.healthbuddy.screens.chatbot.ChatBotViewModel
import com.example.healthbuddy.screens.chatbot.ChatMenuDetailScreen
import com.example.healthbuddy.screens.chatbot.ChatScreen
import com.example.healthbuddy.screens.goal.ChooseGoalScreen
import com.example.healthbuddy.screens.goal.ChoosePlanScreen
import com.example.healthbuddy.screens.goal.GoalViewModel
import com.example.healthbuddy.screens.home.HomeScreen
import com.example.healthbuddy.screens.home.HomeViewModel
import com.example.healthbuddy.screens.menu.EditMealRecipeScreen
import com.example.healthbuddy.screens.menu.MenuTodayScreen
import com.example.healthbuddy.screens.menu.MenuViewModel
import com.example.healthbuddy.screens.menu.RecipeDetailScreen
import com.example.healthbuddy.screens.menu.RecipePickerScreen
import com.example.healthbuddy.screens.profile.ProfileOverviewScreen
import com.example.healthbuddy.screens.profile.UpdateHealthInfoScreen
import com.example.healthbuddy.screens.setup.BirthdayScreen
import com.example.healthbuddy.screens.setup.FatPercentageScreen
import com.example.healthbuddy.screens.setup.GenderScreen
import com.example.healthbuddy.screens.setup.HeightScreen
import com.example.healthbuddy.screens.setup.QuizScreen
import com.example.healthbuddy.screens.setup.QuizViewModel
import com.example.healthbuddy.screens.setup.SetUpScreen
import com.example.healthbuddy.screens.setup.WeightScreen
import com.example.healthbuddy.screens.userinfo.EntryRoute
import com.example.healthbuddy.screens.userinfo.UserInfoViewModel
import com.example.healthbuddy.screens.wellcome.WelcomeScreen
import com.example.healthbuddy.screens.workout.ExerciseDetailScreen
import com.example.healthbuddy.screens.workout.ExercisePickerScreen
import com.example.healthbuddy.screens.workout.TodayWorkoutScreen
import com.example.healthbuddy.screens.workout.WorkoutViewModel
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.LavenderBand
import java.time.LocalDate
import java.time.Period

object Graph {
    const val Auth = "auth"
    const val Onboarding = "onboarding"
    const val Main = "main"
}

sealed class Screen(val route: String) {
    // Authentication
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Forgot : Screen("forgot")
    data object Reset : Screen("reset")

    // Onboarding
    data object SetUp : Screen("setup")
    data object Gender : Screen("gender")
    data object BirthdayScreen : Screen("birthday")
    data object Goal : Screen("goal")
    data object Plan : Screen("plan")
    data object Weight : Screen("weight")
    data object Height : Screen("height")
    data object FatPercentage : Screen("fat")
    data object Quiz : Screen("quiz")
}

sealed class Tab(val route: String, val label: String, val icon: Int) {
    data object Home      : Tab("home",      "Home",      R.drawable.ic_home)
    data object Workout   : Tab("workout",   "Work out",  R.drawable.ic_dumbbell)
    data object Nutrition : Tab("nutrition", "Nutrition", R.drawable.ic_nutrition)
    data object Profile   : Tab("profile",   "Profile",   R.drawable.ic_profile)
}

private val TABS = listOf(Tab.Home, Tab.Workout, Tab.Nutrition, Tab.Profile)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    authViewModel: AuthViewModel = hiltViewModel(),
    userInfoViewModel: UserInfoViewModel = hiltViewModel(),
    quizViewModel: QuizViewModel = hiltViewModel(),
    goalViewModel: GoalViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    menuViewModel: MenuViewModel = hiltViewModel(),
    chatBotViewModel: ChatBotViewModel = hiltViewModel(),
    workoutViewModel: WorkoutViewModel = hiltViewModel()
) {
    val nav = rememberNavController()
    val ui by authViewModel.ui.collectAsState()

    var signUpRequest by remember {
        mutableStateOf(
            SignUpRequest(
                email = "",
                username = "",
                password = ""
            )
        )
    }

    var healthInfoRequest by remember {
        mutableStateOf(
            HealthInfoRequest(
                height = 0f,
                weight = 0f,
                fatPercentage = 0f
            )
        )
    }

    var goalId by remember { mutableIntStateOf(0) }

    val startDest = Graph.Auth

    LaunchedEffect(ui.isLoggedIn) {
        val currentRoute = nav.currentBackStackEntry?.destination?.route

        if (ui.isLoggedIn) {
            val entry = userInfoViewModel.resolveEntryRoute()
            when (entry) {
                EntryRoute.WEIGHT_HEIGHT -> {
                    nav.navigate(Screen.SetUp.route) {
                        popUpTo(Graph.Auth) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                EntryRoute.QUIZ -> {
                    nav.navigate(Screen.Quiz.route) {
                        popUpTo(Graph.Auth) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                EntryRoute.GOAL -> {
                    nav.navigate(Screen.Goal.route) {
                        popUpTo(Graph.Auth) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                EntryRoute.MAIN -> {
                    nav.navigate(Graph.Main) {
                        popUpTo(Graph.Auth) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        } else {
            // Nếu logout thì quay lại Auth graph
            if (currentRoute?.startsWith(Graph.Auth) != true) {
                nav.navigate(Graph.Auth) {
                    popUpTo(nav.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    Scaffold { inner ->
        NavHost(
            navController = nav,
            startDestination = startDest,
            modifier = Modifier.padding(inner)
        ) {
            /* ---------------- AUTH GRAPH ---------------- */
            navigation(
                startDestination = Screen.Welcome.route,
                route = Graph.Auth
            ) {
                composable(Screen.Welcome.route) {
                    WelcomeScreen(
                        onGetStarted = { nav.navigate(Screen.Login.route) }
                    )
                }

                composable(Screen.Login.route) {
                    LoginScreen(
                        uiState = ui,
                        onBack = { nav.popBackStack() },
                        onForgotPassword = { nav.navigate(Screen.Forgot.route) },
                        onLogin = { email, pw ->
                            authViewModel.login(email, pw)
                        },
                        onSignUp = { nav.navigate(Screen.Register.route) }
                    )
                }

                composable(Screen.Register.route) {
                    RegisterScreen(
                        onBack = { nav.popBackStack() },
                        onLogin = { nav.popBackStack() },
                        onRegister = { username, email, password ->
                            signUpRequest = signUpRequest.copy(
                                username = username,
                                email = email,
                                password = password
                            )
                            nav.navigate(Screen.Gender.route)
                        },
                        onTerms = {},
                        onPrivacy = {},
                        onGoogle = {},
                        onFacebook = {},
                        onBiometric = {}
                    )
                }

                composable(Screen.Forgot.route) {
                    ForgotPasswordScreen(
                        onBack = { nav.popBackStack() },
                        onContinue = { nav.navigate(Screen.Reset.route) }
                    )
                }

                composable(Screen.Reset.route) {
                    ResetPasswordScreen(
                        onBack = { nav.popBackStack() },
                        onReset = { _, _ ->
                            nav.navigate(Screen.Login.route) {
                                popUpTo(Screen.Forgot.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Screen.Gender.route) {
                    GenderScreen(
                        onBack = { nav.popBackStack() },
                        onContinue = { isMale ->
                            signUpRequest = signUpRequest.copy(
                                gender = if (isMale) "male" else "female"
                            )
                            nav.navigate(Screen.BirthdayScreen.route)
                        }
                    )
                }

                composable(Screen.BirthdayScreen.route) {
                    BirthdayScreen(
                        onBack = { nav.popBackStack() },
                        onContinue = { day, month, year ->
                            val dob = LocalDate.of(year, month, day)
                            val age = Period.between(dob, LocalDate.now()).years

                            signUpRequest = signUpRequest.copy(
                                birthDay = dob.toString(),
                                age = age
                            )

                            authViewModel.signUp(signUpRequest)
                            nav.navigate(Screen.Login.route)
                        }
                    )
                }
            }

            composable(Screen.SetUp.route) {
                SetUpScreen(
                    onBack = {},
                    onNext = { nav.navigate(Screen.Weight.route) }
                )
            }

            composable(Screen.Weight.route) {
                WeightScreen(
                    onBack = { nav.popBackStack() },
                    onContinue = { weight, _ ->
                        healthInfoRequest = healthInfoRequest.copy(weight = weight)
                        nav.navigate(Screen.Height.route)
                    }
                )
            }

            composable(Screen.Height.route) {
                HeightScreen(
                    onBack = { nav.popBackStack() },
                    onContinue = { heightCm ->
                        healthInfoRequest = healthInfoRequest.copy(
                            height = heightCm.toFloat()
                        )
                        nav.navigate(Screen.FatPercentage.route)
                    }
                )
            }

            composable(Screen.FatPercentage.route) {
                FatPercentageScreen(
                    onBack = { nav.popBackStack() },
                    onContinue = { fat ->
                        healthInfoRequest = healthInfoRequest.copy(fatPercentage = fat)
                        userInfoViewModel.submit(healthInfoRequest)
                        nav.navigate(Screen.Quiz.route)
                    }
                )
            }

            composable(Screen.Quiz.route) {
                val quizUi by quizViewModel.ui.collectAsState()

                LaunchedEffect(Unit) {
                    quizViewModel.loadQuiz()
                }

                LaunchedEffect(quizUi.submitSuccess) {
                    if (quizUi.submitSuccess) {
                        nav.navigate(Screen.Goal.route)
                    }
                }

                QuizScreen(
                    uiState = quizUi,
                    onBack = { nav.popBackStack() },
                    onSelectAnswer = { qId, optId ->
                        quizViewModel.selectAnswer(qId, optId)
                    },
                    onSubmit = { quizViewModel.submitQuiz() }
                )
            }

            composable(Screen.Goal.route) {
                ChooseGoalScreen(
                    onBack = { nav.popBackStack() },
                    onContinue = { goal ->
                        goalId = goal
                        nav.navigate(Screen.Plan.route)
                    }
                )
            }

            composable(Screen.Plan.route) {
                val goalUi by goalViewModel.ui.collectAsState()

                LaunchedEffect(goalId) {
                    Log.d("PlanScreen", ">>> Calling getGoalWithPlan($goalId)")
                    goalViewModel.getGoalWithPlan(goalId)
                }

                when {
                    goalUi.loading -> {
                        Text("Loading...", color = Color.White)
                    }

                    goalUi.error != null -> {
                        Text("Error: ${goalUi.error}", color = Color.Red)
                    }

                    goalUi.goalWithPlans != null -> {
                        ChoosePlanScreen(
                            goal = goalUi.goalWithPlans!!,
                            onBack = { nav.popBackStack() },
                            onContinue = { plan ->
                                goalViewModel.addUserPlan(plan.id)
                                nav.navigate(Graph.Main) {
                                    popUpTo(Graph.Onboarding) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }

            navigation(
                startDestination = "main/screen",
                route = Graph.Main
            ) {
                composable("main/screen") {
                    MainScreenGraph(
                        rootNav = nav,
                        authViewModel = authViewModel,
                        goalViewModel = goalViewModel,
                        homeViewModel = homeViewModel,
                        userInfoViewModel = userInfoViewModel,
                        menuViewModel = menuViewModel,
                        chatBotViewModel = chatBotViewModel,
                        workoutViewModel = workoutViewModel
                    )
                }
            }
        }
    }
}

private fun shouldHideBottomBar(route: String?): Boolean {
    if (route == null) return false

    // Các màn "focus" (Edit/Detail/Add/Picker) -> ẩn bottom bar
    return route in setOf(
        "nutrition/recipePicker/{mealId}",
        "recipe/detail/{mealId}/{recipeId}",
        "nutrition/meal/{mealId}/recipe/{mealRecipeId}",

        "workout/exercises/{muscleId}",
        "workout/add/{exerciseId}",
        "workout/detail/{exerciseId}",

        "profile/edit",

        "chat",
        "chat/menu/{chatId}"
    )
}

@Composable
fun MainScreenGraph(
    rootNav: NavHostController,
    authViewModel: AuthViewModel,
    goalViewModel: GoalViewModel,
    homeViewModel: HomeViewModel,
    userInfoViewModel: UserInfoViewModel,
    menuViewModel: MenuViewModel,
    chatBotViewModel: ChatBotViewModel,
    workoutViewModel: WorkoutViewModel
) {
    val tabNav: NavHostController = rememberNavController()
    val backStack by tabNav.currentBackStackEntryAsState()
    val currentDestination = backStack?.destination

    val currentRoute = currentDestination?.route

    val isInTabGraph = currentDestination
        ?.hierarchy
        ?.any { d -> TABS.any { it.route == d.route } } == true

    val showBar = isInTabGraph && !shouldHideBottomBar(currentRoute)

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

            composable(Tab.Home.route) {
                HomeScreen(homeViewModel=homeViewModel)
            }

            navigation(
                startDestination = "workout/today",
                route = Tab.Workout.route
            ) {

                composable("workout/today") {
                    val userUi by userInfoViewModel.ui.collectAsState()
                    val level = userUi.user?.activityLevel ?: "beginner"

                    TodayWorkoutScreen(
                        viewModel = workoutViewModel,
                        userActivityLevel = level,
                        onOpenExercisePicker = { group ->
                            tabNav.navigate("workout/exercises/${group.id}")
                        },
                        onOpenExerciseDetail = { exerciseId ->
                            tabNav.navigate("workout/detail/$exerciseId")
                        }
                    )
                }

                composable("workout/exercises/{muscleId}") { backStack ->
                    val muscleId = backStack.arguments?.getString("muscleId")!!.toLong()
                    val userUi by userInfoViewModel.ui.collectAsState()
                    val level = userUi.user?.activityLevel ?: "beginner"

                    ExercisePickerScreen(
                        viewModel = workoutViewModel,
                        userActivityLevel = level,
                        muscleGroupId = muscleId,
                        onBack = { tabNav.popBackStack() },
                        onOpenExerciseDetail = { ex ->
                            tabNav.navigate("workout/add/${ex.id}")
                        }
                    )
                }

                composable("workout/add/{exerciseId}") { backStackEntry ->
                    val id =
                        backStackEntry.arguments?.getString("exerciseId")!!.toLong()
                    ExerciseDetailScreen(
                        viewModel = workoutViewModel,
                        exerciseId = id,
                        isUpdateScreen = false,
                        onBack = {
                            tabNav.navigate("workout/today") {
                                popUpTo("workout/today") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable("workout/detail/{exerciseId}") { backStackEntry ->
                    val id =
                        backStackEntry.arguments?.getString("exerciseId")!!.toLong()
                    ExerciseDetailScreen(
                        viewModel = workoutViewModel,
                        exerciseId = id,
                        isUpdateScreen = true,
                        onBack = {
                            tabNav.navigate("workout/today") {
                                popUpTo("workout/today") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            navigation(
                startDestination = "nutrition/menu",
                route = Tab.Nutrition.route
            ) {

                composable("nutrition/menu") {
                    MenuTodayScreen(
                        menuViewModel = menuViewModel,
                        onOpenRecipePicker = { meal ->
                            tabNav.navigate("nutrition/recipePicker/${meal.id}")
                        },
                        onOpenChat = {
                            tabNav.navigate("chat")
                        },
                        onEditMealRecipe = { mealId, mealRecipeId ->
                            tabNav.navigate("nutrition/meal/$mealId/recipe/$mealRecipeId")
                        }
                    )
                }

                composable("nutrition/recipePicker/{mealId}") { backStack ->
                    val mealId =
                        backStack.arguments?.getString("mealId")?.toLongOrNull()
                            ?: return@composable

                    RecipePickerScreen(
                        vm = menuViewModel,
                        onBack = { tabNav.popBackStack() },
                        onDetail = { recipeId ->
                            tabNav.navigate("recipe/detail/$mealId/$recipeId")
                        }
                    )
                }

                composable("recipe/detail/{mealId}/{recipeId}") { backStackEntry ->
                    val mealId =
                        backStackEntry.arguments?.getString("mealId")!!.toLong()
                    val recipeId =
                        backStackEntry.arguments?.getString("recipeId")!!.toLong()

                    val ui by menuViewModel.ui.collectAsState()
                    val recipe = ui.recipes.first { it.id == recipeId }

                    RecipeDetailScreen(
                        mealId = mealId,
                        recipe = recipe,
                        vm = menuViewModel,
                        onBack = { tabNav.popBackStack() },
                        onAdded = {
                            tabNav.navigate("nutrition/menu") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(
                    route = "nutrition/meal/{mealId}/recipe/{mealRecipeId}"
                ) { backStackEntry ->
                    val mealId =
                        backStackEntry.arguments?.getString("mealId")
                            ?.toLongOrNull() ?: return@composable

                    val mealRecipeId =
                        backStackEntry.arguments?.getString("mealRecipeId")
                            ?.toLongOrNull() ?: return@composable

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
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable("chat") {
                    ChatScreen(
                        viewModel = chatBotViewModel,
                        onBack = { tabNav.popBackStack() },
                        onOpenMenuDetail = { chatId ->
                            tabNav.navigate("chat/menu/$chatId")
                        }
                    )
                }

                composable("chat/menu/{chatId}") { backStack ->
                    val chatId = backStack.arguments?.getString("chatId")!!.toLong()
                    ChatMenuDetailScreen(
                        viewModel = chatBotViewModel,
                        chatId = chatId,
                        onBack = { tabNav.popBackStack() },
                        onChosenDone = {
                            // chọn xong -> quay về chat, hoặc về MenuToday tùy bạn
                            tabNav.popBackStack() // về ChatScreen
                        }
                    )
                }
            }

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
                            onEditProfile = {
                                tabNav.navigate("profile/edit")
                            },
                            onChooseNewPlan = {
                                rootNav.navigate(Screen.SetUp.route) {
                                    popUpTo(Graph.Main) { inclusive = true }
                                    launchSingleTop = true
                                }
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

            composable("profile/edit") {
                val ui by userInfoViewModel.ui.collectAsState()

                UpdateHealthInfoScreen(
                    user = ui.user!!,
                    healthInfo = ui.healthInfo!!,
                    avatarUrl = null,
                    onBack = { tabNav.popBackStack() },
                    onUpdate = { newHealth ->
                        userInfoViewModel.submit(
                            HealthInfoRequest(
                                height = newHealth.height,
                                weight = newHealth.weight,
                                fatPercentage = newHealth.fatPercentage
                            )
                        )
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
