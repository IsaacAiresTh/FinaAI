package com.example.finai.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finai.R
import com.example.finai.features.upload.ui.UploadScreen
import com.example.finai.ui.components.CBottomNavBar

@Composable
fun MainScreen(onLogout: () -> Unit = {}) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding())) {
            
            // 1. Conteúdo da Tela (Fundo)
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                // Padding inferior = Altura da Barra (80dp) + Insets do Sistema + Espaço extra
                // Garante que o conteúdo role até o fim sem ficar atrás da barra
                val navBarHeight = 80.dp
                // Não conseguimos pegar o valor exato dos insets aqui dentro do NavHost facilmente sem passar padding, 
                // então usamos um valor seguro maior.
                val bottomPadding = 80.dp

                composable("home") { HomeScreen(modifier = Modifier.padding(bottom = bottomPadding)) }
                composable("trending") { TrendingScreen(modifier = Modifier.padding(bottom = bottomPadding)) }
                composable("table") { TableScreen(modifier = Modifier.padding(bottom = bottomPadding)) }
                composable("profile") {
                    ProfileScreen(
                        modifier = Modifier.padding(bottom = bottomPadding),
                        navController = navController,
                        onLogout = onLogout
                    )
                }
                composable("upload") { UploadScreen(modifier = Modifier.padding(bottom = bottomPadding)) }
                
                // Telas sem NavBar
                composable("preferences") {
                    PreferencesScreen(onBackClick = { navController.popBackStack() })
                }
                composable("notifications") {
                    NotificationsScreen(onBackClick = { navController.popBackStack() })
                }
                composable("security") {
                    SecurityScreen(onBackClick = { navController.popBackStack() })
                }
                composable("accessibility") {
                    AccessibilityScreen(onBackClick = { navController.popBackStack() })
                }
                composable("contact_us") {
                    ContactUsScreen(onBackClick = { navController.popBackStack() })
                }
                composable("edit_salary_limit") {
                    EditSalaryLimitScreen(onBackClick = { navController.popBackStack() })
                }
            }

            // 2. Elementos de Navegação Flutuantes (Frente)
            val mainRoutes = listOf("home", "trending", "table", "profile", "upload")
            if (currentRoute in mainRoutes) {
                
                // Bottom Navigation Bar
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    CBottomNavBar(
                        selectedItem = currentRoute,
                        onItemSelected = {
                            navController.navigate(it) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                // Botão de Upload Flutuante
                // Cálculo: Altura Barra (80dp) - Metade Botão (28dp) = 52dp Base
                // + Altura da Navegação do Sistema (Insets) para não ficar afundado em telas com botões virtuais
                val fabBottomPadding = 52.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                
                FloatingActionButton(
                    onClick = {
                        navController.navigate("upload") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    shape = CircleShape,
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 8.dp
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = fabBottomPadding)
                        .size(56.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_upload),
                        contentDescription = "Upload",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
