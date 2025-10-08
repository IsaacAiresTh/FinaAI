package com.example.finai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finai.R
import com.example.finai.ui.components.CButtonAuth
import com.example.finai.ui.components.COutlinedTextField

@Composable
fun LoginScreen(onLoginClick: () -> Unit, onSignUpClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1C))
    ) {
        // Camada de fundo amarela no topo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(Color(0xFFFFC107))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Logo e Título
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Image(painter = painterResource(id = R.drawable.logo_finai), contentDescription = "Logo FinAI", modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "FinAI", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }


            // Card de Login
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "FAZER LOGIN", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(24.dp))

                    // Campo de E-mail
                    COutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "E-mail",
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de Senha
                    COutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Senha",
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // Botão Entrar
                    CButtonAuth(
                        onClick = onLoginClick,
                        text = "ENTRAR"
                    )
                }
            }

            // Link para Cadastro
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Não tem uma conta?", color = Color.Gray)
                ClickableText(
                    text = AnnotatedString("Faça seu cadastro"),
                    onClick = { onSignUpClick() },
                    style = TextStyle(
                        color = Color(0xFFFFC107),
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        onLoginClick = {},
        onSignUpClick = {}
    )
}