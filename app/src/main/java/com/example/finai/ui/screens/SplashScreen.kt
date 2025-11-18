package com.example.finai.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finai.core.session.SessionManager
import kotlinx.coroutines.delay

// Supondo que você tenha um logo na pasta res/drawable
// Se o nome do arquivo for diferente, altere aqui.
import com.example.finai.R

@Composable
fun SplashScreen(onTimeout: (Boolean) -> Unit) { // Alterado para retornar true/false se tem sessão
    val context = LocalContext.current
    var startAnimation by remember { mutableStateOf(false) }
    val sessionManager = remember { SessionManager(context) }

    val backgroundColor by animateColorAsState(
        targetValue = if (startAnimation) Color(0xFF1C1C1C) else Color(0xFFFFC107),
        animationSpec = tween(durationMillis = 1500),
        label = "backgroundColor"
    )

    LaunchedEffect(key1 = true) {
        delay(1000L)
        startAnimation = true
        delay(2000L)
        
        // Verifica se o usuário está logado e tem sessão válida
        val isLoggedIn = sessionManager.isLoggedIn()
        onTimeout(isLoggedIn)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Se você ainda não tem a imagem do logo no projeto,
            // esta linha pode dar erro. Pode comentar ela por enquanto.
            // Image(painter = painterResource(id = R.drawable.logo_finai), contentDescription = "Logo FinAI")

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "FinAI",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = if (startAnimation) Color(0xFFFFC107) else Color.Black
            )
        }
    }
}
