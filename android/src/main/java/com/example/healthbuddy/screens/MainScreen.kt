package com.example.healthbuddy.screens

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthbuddy.screens.auth.ForgotPasswordScreen
import com.example.healthbuddy.screens.auth.LoginScreen
import com.example.healthbuddy.screens.auth.RegisterScreen
import com.example.healthbuddy.screens.auth.ResetPasswordScreen
import com.example.healthbuddy.screens.wellcome.WelcomeScreen

enum class MainScreen() {
    Welcome(),
    Login(),
    Register(),
    ForgotPassword(),
    ResetPassword()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(){
    val navController: NavHostController = rememberNavController()
    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainScreen.Welcome.name,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(route = MainScreen.Welcome.name) {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(MainScreen.Login.name)
                    }
                )
            }
            composable(route = MainScreen.Login.name) {
                LoginScreen(
                    onBack = {
                        navController.navigate(MainScreen.Welcome.name)
                    },
                    onForgotPassword = {
                        navController.navigate(MainScreen.ForgotPassword.name)
                    },
                    onLogin = { email, password ->
                        Log.d("Login email",email)
                        Log.d("Login password",password)
                    },
                    onSignUp = {
                        navController.navigate(MainScreen.Register.name)
                    }
                )
            }
            composable(route = MainScreen.Register.name) {
                RegisterScreen(
                    onBack = {
                        navController.navigate(MainScreen.Login.name)
                    },
                    onLogin = {
                        navController.navigate(MainScreen.Login.name)
                    },
                    onRegister = { name, emailOrPhone, password ->
                        Log.d("Register name", name)
                        Log.d("Register email or phone", emailOrPhone)
                        Log.d("Register password", password)
                    },
                    onGoogle = {},
                    onFacebook = {},
                    onBiometric = {},
                    onTerms = {},
                    onPrivacy = {}
                )
            }
            composable(route = MainScreen.ForgotPassword.name) {
                ForgotPasswordScreen(
                    onBack = {
                        navController.navigate(MainScreen.Login.name)
                    },
                    onContinue = { email ->
                        Log.d("Email reset",email)
                        navController.navigate(MainScreen.ResetPassword.name)
                    }
                )
            }
            composable(route = MainScreen.ResetPassword.name) {
                ResetPasswordScreen(
                    onBack = {
                        navController.navigate(MainScreen.ForgotPassword.name)
                    },
                    onReset = { code, newPassword ->
                        Log.d("Reset code",code)
                        Log.d("Reset password", newPassword)
                        navController.navigate(MainScreen.Login.name)
                    }
                )
            }
        }
    }
}
