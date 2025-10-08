package com.example.finai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finai.ui.screens.LoginScreen
import com.example.finai.ui.screens.SignUpScreen
import com.example.finai.ui.screens.SplashScreen
import com.example.finai.ui.theme.FinAiTheme

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("login") {
                    // Impede que o usuário volte para a splash screen
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(
                onLoginClick = {
                    // Aqui vamos adicionar a lógica de login no futuro.
                    // Por enquanto, podemos navegar para a tela principal (home).
                    // navController.navigate("home")
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
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinAiTheme {
                AppNavigation()
            }
        }
    }
}


