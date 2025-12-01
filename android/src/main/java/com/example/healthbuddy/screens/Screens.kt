package com.example.healthbuddy.screens

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.healthbuddy.data.model.HealthInfoRequest
import com.example.healthbuddy.data.model.SignUpRequest
import com.example.healthbuddy.screens.auth.AuthViewModel
import com.example.healthbuddy.screens.auth.ForgotPasswordScreen
import com.example.healthbuddy.screens.auth.LoginScreen
import com.example.healthbuddy.screens.auth.RegisterScreen
import com.example.healthbuddy.screens.auth.ResetPasswordScreen
import com.example.healthbuddy.screens.setup.BirthdayScreen
import com.example.healthbuddy.screens.goal.ChooseGoalScreen
import com.example.healthbuddy.screens.goal.ChoosePlanScreen
import com.example.healthbuddy.screens.goal.GoalViewModel
import com.example.healthbuddy.screens.menu.MenuViewModel
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
    data object Age : Screen("age")
    data object Goal : Screen("goal")
    data object Plan : Screen("plan")
    data object Weight : Screen("weight")
    data object Height : Screen("height")
    data object FatPercentage : Screen("fat")
    data object Quiz : Screen("quiz")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    authViewModel: AuthViewModel = hiltViewModel(),
    userInfoViewModel: UserInfoViewModel = hiltViewModel(),
    quizViewModel: QuizViewModel = hiltViewModel(),
    goalViewModel: GoalViewModel = hiltViewModel(),
    menuViewModel: MenuViewModel = hiltViewModel()
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

    // Lần đầu vào app: nếu chưa login thì vào Auth
    // Nếu sau này ui.isLoggedIn true (do token), LaunchedEffect sẽ điều hướng tiếp
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
        }else {
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
            // ---------- AUTH ----------
            navigation(startDestination = Screen.Welcome.route, route = Graph.Auth) {
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
                            // chỉ login -> LaunchedEffect(ui.isLoggedIn) sẽ xử lý.
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
                        onGoogle = {},
                        onFacebook = {},
                        onBiometric = {},
                        onTerms = {},
                        onPrivacy = {}
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
                            val dateOfBirth = LocalDate.of(year, month, day)
                            val today = LocalDate.now()
                            val age = Period.between(dateOfBirth, today).years

                            signUpRequest = signUpRequest.copy(
                                birthDay = dateOfBirth.toString(),
                                age = age
                            )

                            authViewModel.signUp(signUpRequest)
                            nav.navigate(Screen.Login.route)
                        }
                    )
                }
            }

            // ---------- ONBOARDING FLOW ----------
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
                    onContinue = { fatPercentage->
                        healthInfoRequest = healthInfoRequest.copy(
                            fatPercentage = fatPercentage
                        )
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

            // ---------- MAIN ----------
            navigation(startDestination = "main/screen", route = Graph.Main) {
                composable("main/screen") {
                    MainScreenGraph(
                        authViewModel = authViewModel,
                        userInfoViewModel = userInfoViewModel,
                        menuViewModel = menuViewModel
                    )
                }
            }
        }
    }
}
