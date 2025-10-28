package com.example.finai.ui.screens

import androidx.compose.foundation.layout.padding // <-- Import necessário
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
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_upload),
                    contentDescription = "Upload",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding -> // <-- ESTE É O PADDING IMPORTANTE
        NavHost(
            navController = navController,
            startDestination = "home",
            // --- MUDANÇA AQUI ---
            // Removemos o padding do NavHost
            modifier = Modifier
        ) {
            // --- MUDANÇA AQUI ---
            // E aplicamos o 'innerPadding' em CADA tela
            composable("home") { HomeScreen(modifier = Modifier.padding(innerPadding)) }
            composable("trending") { TrendingScreen(modifier = Modifier.padding(innerPadding)) }
            composable("table") { TableScreen(modifier = Modifier.padding(innerPadding)) }
            composable("profile") { ProfileScreen(modifier = Modifier.padding(innerPadding)) }
            composable("upload") { UploadScreen(modifier = Modifier.padding(innerPadding)) }
        }
    }
}