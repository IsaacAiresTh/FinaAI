package com.example.finai.ui.screens

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
import com.example.finai.ui.components.CBottomNavBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        containerColor = Color.Transparent, // Remove fundo branco
        contentColor = Color.White,
        bottomBar = {
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
        },
        floatingActionButton = {
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
                    .size(56.dp)
                    .offset(y = 32.dp) // Aumentado para descer mais o botão
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_upload),
                    contentDescription = "Upload",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
        ) {
            composable("home") { HomeScreen(modifier = Modifier.padding(innerPadding)) }
            composable("trending") { TrendingScreen(modifier = Modifier.padding(innerPadding)) }
            composable("table") { TableScreen(modifier = Modifier.padding(innerPadding)) }
            composable("profile") {
                ProfileScreen(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController // Passando o controlador
                )
            }
            composable("upload") { UploadScreen(modifier = Modifier.padding(innerPadding)) }
            // NOVAS TELAS (elas não usam innerPadding pois não têm BottomBar)
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
    }
}

