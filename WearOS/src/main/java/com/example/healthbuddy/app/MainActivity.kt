package com.example.healthbuddy.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.healthbuddy.presentation.ExerciseApp
import com.example.healthbuddy.presentation.exercise.ExerciseViewModel
import com.example.healthbuddy.presentation.preparing.PreparingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private lateinit var navController: NavHostController
    private val exerciseViewModel by viewModels<ExerciseViewModel>()
    private val preparingViewModel by viewModels<PreparingViewModel>()

    // Register the permissions callback
    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // All permissions granted, proceed with exercise
            exerciseViewModel.startExercise()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        var pendingNavigation = true

        splash.setKeepOnScreenCondition { pendingNavigation }

        super.onCreate(savedInstanceState)

        // Request permissions when activity is created
        requestPermissions.launch(PreparingViewModel.permissions.toTypedArray())

        setContent {
            navController = rememberSwipeDismissableNavController()

            ExerciseApp(
                navController,
                onFinishActivity = { this.finish() },
                exerciseViewModel
            )

            LaunchedEffect(Unit) {
                prepareIfNoExercise()
                pendingNavigation = false
            }
        }
    }

    private suspend fun prepareIfNoExercise() {
        val isRegularLaunch =
            navController.currentDestination?.route == Screen.Exercise.route
        if (isRegularLaunch && !exerciseViewModel.isExerciseInProgress()) {
            navController.navigate(Screen.PreparingExercise.route)
        }
    }
}
