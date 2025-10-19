package com.example.finai.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finai.ui.components.CBottomNavBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    // Scaffold fornece a estrutura básica de layout do Material Design
    Scaffold(
        bottomBar = {
            // Renderiza o BottomBar personalizado
            CBottomNavBar(
                selectedItem = currentRoute,
                onItemSelected = {
                    // Navega para a rota selecionada com lógica para otimizar a back stack.
                    navController.navigate(it) {
                        // Evita o empilhamento de telas ao voltar para a tela inicial do gráfico.
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Garante que haja apenas uma cópia de um destino na pilha.
                        launchSingleTop = true
                        // Restaura o estado ao selecionar novamente um item já visitado.
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        // NavHost hospeda as diferentes telas que podem ser navegadas pela BottomNavBar.
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding) // Aplica o padding interno do Scaffold.
        ) {
            composable("home") { HomeScreen() }
            composable("trending") { TrendingScreen() }
            composable("table") { TableScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}
