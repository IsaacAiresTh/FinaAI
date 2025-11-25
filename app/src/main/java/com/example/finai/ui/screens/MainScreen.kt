package com.example.finai.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.finai.features.expense.ui.TableScreen
import com.example.finai.features.expense.ui.TrendingScreen
import com.example.finai.features.home.ui.HomeScreen
import com.example.finai.features.profile.ui.EditSalaryLimitScreen
import com.example.finai.features.profile.ui.ProfileScreen
import com.example.finai.features.upload.ui.UploadScreen
import com.example.finai.ui.components.CBottomNavBar

@Composable
fun MainScreen(onLogout: () -> Unit = {}) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"
    
    val mainRoutes = listOf("home", "trending", "table", "profile", "upload")
    val showBottomBar = currentRoute in mainRoutes

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = Color.White,
        contentWindowInsets = WindowInsets(0.dp), // Mantemos o controle manual dos insets se necessário, mas agora Scaffold gerencia o padding
        bottomBar = {
            if (showBottomBar) {
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
        },
        floatingActionButton = {
            if (showBottomBar) {
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
                    // Ajuste de offset para "encaixar" o FAB na barra de navegação personalizada
                    // O valor de 60.dp empurra o FAB para baixo para sobrepor a barra
                    modifier = Modifier
                        .offset(y = 50.dp) 
                        .size(56.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_upload),
                        contentDescription = "Upload",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        // NavHost agora respeita o innerPadding fornecido pelo Scaffold
        // Isso evita que o conteúdo fique escondido atrás da barra
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("home") { HomeScreen() }
                composable("trending") { TrendingScreen() }
                composable("table") { TableScreen() }
                composable("profile") {
                    ProfileScreen(
                        navController = navController,
                        onLogout = onLogout
                    )
                }
                composable("upload") { UploadScreen() }
                
                // Telas Secundárias
                composable("preferences") { PreferencesScreen(onBackClick = { navController.popBackStack() }) }
                composable("notifications") { NotificationsScreen(onBackClick = { navController.popBackStack() }) }
                composable("security") { SecurityScreen(onBackClick = { navController.popBackStack() }) }
                composable("accessibility") { AccessibilityScreen(onBackClick = { navController.popBackStack() }) }
                composable("contact_us") { ContactUsScreen(onBackClick = { navController.popBackStack() }) }
                composable("edit_salary_limit") { EditSalaryLimitScreen(onBackClick = { navController.popBackStack() }) }
            }
        }
    }
}
