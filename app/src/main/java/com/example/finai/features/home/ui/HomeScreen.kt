package com.example.finai.features.home.ui

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finai.ui.components.CDashboardBox

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(application)
    )
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2A2A))
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
            // Exibe a saudação com o primeiro nome
            Text(
                text = "Olá, ${uiState.userName}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 1. Box de Gasto Total e Limite
        item {
            CDashboardBox {
                Column {
                    // Linha para o Total Gasto
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Gasto:",
                            fontSize = 22.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "R$ 1.250,00",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFC800)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Linha para o Limite
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Limite:",
                            fontSize = 22.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "R$ 2.000,00",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 2. Box de Análise Mensal
        item {
            CDashboardBox(title = "Análise Mensal") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Placeholder para o Gráfico de Pizza
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Gráfico", color = Color.White)
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Resumo do Mês",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = Color.White
                        )
                        Text("Seus gastos diminuíram 15% em relação ao mês anterior.", color = Color.Gray)
                    }
                }
            }
        }

        // 3. Box de Arquivos Recentes
        item {
            CDashboardBox(title = "Arquivos Recentes") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    RecentFileItem(description = "Conta de Luz - Maio/2024")
                    RecentFileItem(description = "Nota Fiscal - Supermercado")
                    RecentFileItem(description = "Fatura Cartão - Final 9876")
                }
            }
        }
    }
}

//Funcao temporaria
@Composable
fun RecentFileItem(description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Placeholder para a imagem do arquivo
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.DarkGray, shape = MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(description, color = Color.White, modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
