package com.example.finai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finai.features.auth.ui.LoginScreen
import com.example.finai.features.auth.ui.SignUpScreen
import com.example.finai.ui.screens.MainScreen
import com.example.finai.ui.screens.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onTimeout = { isLoggedIn ->
                val startDestination = if (isLoggedIn) "main" else "login"
                navController.navigate(startDestination) {
                    // Impede que o usuário volte para a splash screen
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(
                onLoginClick = {
                    // Navega para a tela principal (MainScreen)
                    navController.navigate("main") {
                        // Limpa o back stack para que o usuário não possa voltar para a tela de login
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpClick = {
                    // Navega para a tela de cadastro
                    navController.navigate("signup")
                }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignUpClick = {
                    // TODO:  adicionar a lógica de cadastro do usuario
                },
                onLoginClick = {
                    // Fecha a tela de cadastro e volta para a tela de login
                    navController.popBackStack()
                }
            )
        }
        composable("main") {
            MainScreen(
                onLogout = {
                    // Navega para a tela de login e limpa o histórico
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
