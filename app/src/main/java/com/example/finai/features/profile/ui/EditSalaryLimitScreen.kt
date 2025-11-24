package com.example.finai.features.profile.ui

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finai.ui.components.CButtonAuth
import com.example.finai.ui.components.COutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSalaryLimitScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(application)
    )
    val uiState by viewModel.uiState.collectAsState()

    // Estados para os campos de texto
    var salary by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("") }
    
    // Carregar dados iniciais apenas uma vez quando o usuário estiver carregado
    LaunchedEffect(uiState.user) {
        if (uiState.user != null) {
             if (salary.isEmpty()) salary = uiState.user?.salary?.toString() ?: ""
             if (limit.isEmpty()) limit = uiState.user?.spendingLimit?.toString() ?: ""
        }
    }
    
    // Observar sucesso da atualização
    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            Toast.makeText(context, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateSuccess()
            onBackClick()
        }
    }
    
    // Observar erros
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2A2A))
    ) {
        // Barra Superior
        TopAppBar(
            title = {
                Text(
                    text = "Salário e Limite",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Conteúdo da tela
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Insira seus dados financeiros. Isso nos ajuda a calcular seus relatórios com mais precisão.",
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Campo Salário
                COutlinedTextField(
                    value = salary,
                    onValueChange = { salary = it },
                    label = "Salário Mensal (R$)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo Limite de Gastos
                COutlinedTextField(
                    value = limit,
                    onValueChange = { limit = it },
                    label = "Limite de Gastos Mensal (R$)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            // Botão Salvar
            CButtonAuth(
                onClick = {
                    val salaryValue = salary.toDoubleOrNull() ?: 0.0
                    val limitValue = limit.toDoubleOrNull() ?: 0.0
                    viewModel.updateUserFinancials(salaryValue, limitValue)
                },
                text = if (uiState.isLoading) "SALVANDO..." else "SALVAR ALTERAÇÕES"
            )
        }
    }
}
