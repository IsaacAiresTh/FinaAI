package com.example.finai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.finai.navigation.AppNavigation
import com.example.finai.ui.theme.FinAiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita o modo Edge-to-Edge para desenhar atrás das barras do sistema
        setContent {
            FinAiTheme {
                AppNavigation()
            }
        }
    }
}
