package com.example.finai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.finai.navigation.AppNavigation
import com.example.finai.ui.theme.FinAiTheme

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
