package com.example.healthbuddy.screens

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthbuddy.screens.auth.AuthViewModel
import com.example.healthbuddy.screens.auth.ForgotPasswordScreen
import com.example.healthbuddy.screens.auth.LoginScreen
import com.example.healthbuddy.screens.auth.RegisterScreen
import com.example.healthbuddy.screens.auth.ResetPasswordScreen
import com.example.healthbuddy.screens.setup.AgeScreen
import com.example.healthbuddy.screens.setup.GenderScreen
import com.example.healthbuddy.screens.setup.HeightScreen
import com.example.healthbuddy.screens.setup.SetUpScreen
import com.example.healthbuddy.screens.setup.WeightScreen
import com.example.healthbuddy.screens.wellcome.WelcomeScreen

enum class Screens {
    Welcome,

    // Auth
    Login, Register, ForgotPassword, ResetPassword, SetUp, Gender, Age, Weight, Height,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    authViewModel: AuthViewModel = hiltViewModel()
){
    val navController: NavHostController = rememberNavController()
    val authUiState by authViewModel.ui.collectAsState()

    LaunchedEffect(authUiState.isLoggedIn) {
        if (authUiState.isLoggedIn) {
            navController.navigate(Screens.SetUp.name) {
                popUpTo(Screens.Welcome.name) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.Welcome.name,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(route = Screens.Welcome.name) {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(Screens.Login.name)
                    }
                )
            }
            composable(route = Screens.Login.name) {
                LoginScreen(
                    uiState = authUiState,
                    onBack = {
                        navController.popBackStack()
                    },
                    onForgotPassword = {
                        navController.navigate(Screens.ForgotPassword.name)
                    },
                    onLogin = { email, password ->
                        authViewModel.login(email, password)
                    },
                    onSignUp = {
                        navController.navigate(Screens.Register.name)
                    }
                )
            }
            composable(route = Screens.Register.name) {
                RegisterScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onLogin = {
                        navController.navigate(Screens.Login.name)
                    },
                    onRegister = { name, emailOrPhone, password ->
                        navController.navigate(Screens.Login.name) {
                            popUpTo(Screens.Welcome.name) { inclusive = true }
                        }
                    },
                    onGoogle = {},
                    onFacebook = {},
                    onBiometric = {},
                    onTerms = {},
                    onPrivacy = {}
                )
            }
            composable(route = Screens.ForgotPassword.name) {
                ForgotPasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onContinue = { email ->
                        Log.d("Email reset",email)
                        navController.navigate(Screens.ResetPassword.name)
                    }
                )
            }
            composable(route = Screens.ResetPassword.name) {
                ResetPasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onReset = { code, newPassword ->
                        navController.navigate(Screens.Login.name) {
                            popUpTo(Screens.ForgotPassword.name) { inclusive = true }
                        }
                    }
                )
            }
            composable(route = Screens.SetUp.name) {
                SetUpScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onNext = {
                        navController.navigate(Screens.Gender.name)
                    }
                )
            }
            composable(route = Screens.Gender.name) {
                GenderScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onContinue = {
                        navController.navigate(Screens.Age.name)
                    }
                )
            }
            composable(route = Screens.Age.name) {
                AgeScreen(
                    onBack = {
                        navController.navigate(Screens.Gender.name)
                    },
                    onContinue = {
                        navController.navigate(Screens.Weight.name)
                    }
                )
            }
            composable(route = Screens.Weight.name) {
                WeightScreen(
                    onBack = {
                        navController.navigate(Screens.Age.name)
                    },
                    onContinue = { _,_ ->
                        navController.navigate(Screens.Height.name)
                    }
                )
            }
            composable(route = Screens.Height.name) {
                HeightScreen(
                    onBack = {
                      navController.navigate(Screens.Weight.name)
                    },
                    onContinue = {

                    }
                )
            }
        }
    }
}
