package com.example.healthbuddy.presentation

import ExerciseGoalsRoute
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.example.healthbuddy.app.Screen
import com.example.healthbuddy.app.Screen.*
import com.example.healthbuddy.app.navigateToTopLevel
import com.example.healthbuddy.presentation.dialogs.ExerciseNotAvailable
import com.example.healthbuddy.presentation.exercise.ExerciseRoute
import com.example.healthbuddy.presentation.exercise.ExerciseViewModel
import com.example.healthbuddy.presentation.home.WatchHomeViewModel
import com.example.healthbuddy.presentation.preparing.PreparingExerciseRoute
import com.example.healthbuddy.presentation.summary.SummaryRoute

@Composable
fun ExerciseApp(
    navController: NavHostController,
    onFinishActivity: () -> Unit,
    viewModel: ExerciseViewModel,
) {
    AppScaffold {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = Exercise.route

        ) {
            composable(PreparingExercise.route) {
                PreparingExerciseRoute(
                    onStart = {
                        navController.navigate(Exercise.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = false
                            }
                        }
                    },
                    onNoExerciseCapabilities = {
                        navController.navigate(ExerciseNotAvailable.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = false
                            }
                        }
                    },
                    onFinishActivity = onFinishActivity,
                    onGoals = { navController.navigate(Goals.route) }
                )
            }

            composable(Exercise.route) {
                ExerciseRoute(
                    onSummary = {
                        viewModel.sendExerciseSummary(it)
                        navController.navigateToTopLevel(Summary, Summary.buildRoute(it))
                    },
                    onRestart = {
                        navController.navigateToTopLevel(PreparingExercise)
                    },
                    onFinishActivity = onFinishActivity
                )
            }

            composable(ExerciseNotAvailable.route) {
                ExerciseNotAvailable()
            }

            composable(
                Summary.route + "/{averageHeartRate}/{totalDistance}/{totalCalories}/{elapsedTime}",
                arguments = listOf(
                    navArgument(Summary.averageHeartRateArg) { type = NavType.FloatType },
                    navArgument(Summary.totalDistanceArg) { type = NavType.FloatType },
                    navArgument(Summary.totalCaloriesArg) { type = NavType.FloatType },
                    navArgument(Summary.elapsedTimeArg) { type = NavType.StringType }
                )
            ) {
                SummaryRoute(
                    onRestartClick = {
                        navController.navigateToTopLevel(PreparingExercise)
                    }
                )
            }
            composable(Goals.route) {
                ExerciseGoalsRoute(onSet = { navController.popBackStack() })
            }
        }
    }
}
