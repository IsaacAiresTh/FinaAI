package com.example.finai.features.auth.ui

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finai.ui.components.CButtonAuth
import com.example.finai.ui.components.COutlinedTextField

@Composable
fun SignUpScreen(onSignUpClick: () -> Unit, onLoginClick: () -> Unit) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(application)
    )

    val uiState by viewModel.uiState.collectAsState()

    // Observa o sucesso do registro
    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) {
            Toast.makeText(context, "Conta criada com sucesso! Faça login.", Toast.LENGTH_LONG).show()
            onLoginClick() // Volta para o Login
            viewModel.resetState()
        }
    }

    // Observa erros
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

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
            //logo e titulo
            //TODO: botar a logo do app em SVG
            Column (horizontalAlignment = Alignment.CenterHorizontally){
//                Image(painter = painterResource(R.drawable.logo_finai), contentDescription = "Logo FinAI", modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "CRIE SUA CONTA", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            //campo de registro
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    Text(text = "CADASTRAR", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(24.dp))


                    COutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "E-mail",
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    //Campo nome
                    COutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nome Completo",
                        singleLine = true
                    )
                    //Campo email
                    Spacer(modifier = Modifier.height(16.dp))

                    //Campo CPF e telefone
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        COutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = cpf,
                            onValueChange = { cpf = it },
                            label = "CPF",
                            singleLine = true
                        )
                        COutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Telefone",
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    //Campo senha e confirmar senha
                    COutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Senha",
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    COutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirmar Senha",
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // Botão Criar Conta
                    Box(contentAlignment = Alignment.Center) {
                        CButtonAuth(
                            onClick = {
                                if (password == confirmPassword) {
                                    viewModel.register(name, email, cpf, phone, password)
                                } else {
                                    Toast.makeText(context, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                                }
                            },
                            text = if (uiState.isLoading) "" else "CRIAR CONTA"
                        )
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Já tem uma conta?", color = Color.Gray)
                ClickableText(
                    text = AnnotatedString("Faça seu login"),
                    onClick = { onLoginClick() },
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
private fun SignUpScreenPreview() {
    SignUpScreen(onSignUpClick = {}, onLoginClick = {})
}
