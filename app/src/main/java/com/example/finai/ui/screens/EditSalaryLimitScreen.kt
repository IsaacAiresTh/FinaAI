// Em app/src/main/java/com/example/finai/ui/screens/EditSalaryLimitScreen.kt
package com.example.finai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finai.ui.components.CButtonAuth
import com.example.finai.ui.components.COutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSalaryLimitScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
    // TODO: No futuro, injetar um ViewModel para carregar os valores atuais e salvar os novos
) {
    // Estados para os campos de texto.
    // No futuro, eles devem ser inicializados com os valores atuais do usuário (vindos do ViewModel)
    var salario by remember { mutableStateOf("") }
    var limite by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2A2A)) // Mesmo fundo
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
            verticalArrangement = Arrangement.SpaceBetween // Empurra o botão para baixo
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
                    value = salario,
                    onValueChange = { salario = it },
                    label = "Salário Mensal (R$)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo Limite de Gastos
                COutlinedTextField(
                    value = limite,
                    onValueChange = { limite = it },
                    label = "Limite de Gastos Mensal (R$)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            // Botão Salvar
            CButtonAuth(
                onClick = {
                    // TODO: Adicionar lógica de validação e salvamento no ViewModel
                    // Por enquanto, apenas fecha a tela ao salvar
                    onBackClick()
                },
                text = "SALVAR ALTERAÇÕES"
            )
        }
    }
}