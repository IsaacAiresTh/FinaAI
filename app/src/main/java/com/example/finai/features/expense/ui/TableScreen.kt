package com.example.finai.features.expense.ui

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finai.core.database.entities.ExpenseEntity
import com.example.finai.ui.components.CDashboardBox
import com.example.finai.ui.theme.FinAiTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TableScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: TableViewModel = viewModel(
        factory = TableViewModel.Factory(application)
    )
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2A2A))
            .padding(horizontal = 16.dp)
    ) {
        // Título da Tela
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Relatório de Gastos",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Box de Resumo
        item {
            CDashboardBox(title = "Resumo Total") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Gasto:",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    val formattedTotal = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                        .format(uiState.totalAmount)
                    
                    Text(
                        text = formattedTotal,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFC800)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Cabeçalho da Lista de Despesas
        item {
            Text(
                text = "Todas as Despesas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Lista de Despesas
        if (uiState.expenses.isEmpty() && !uiState.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhuma despesa registrada.", color = Color.Gray)
                }
            }
        } else {
            items(uiState.expenses) { expense ->
                ExpenseItem(expense = expense)
                Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
            }
        }
        
        if (uiState.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun ExpenseItem(expense: ExpenseEntity) {
    val icon = when(expense.type) {
        "Boleto" -> Icons.Default.Description
        "Nota Fiscal" -> Icons.Default.ShoppingCart
        "Recibo" -> Icons.Default.Description
        "Alimentação" -> Icons.Default.Fastfood // Exemplo, se o Gemini retornar categorias específicas
        else -> Icons.Default.ShoppingCart
    }

    // Formatação de Data (YYYY-MM-DD -> DD/MM/YYYY)
    val formattedDate = try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val output = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        output.format(input.parse(expense.date)!!)
    } catch (e: Exception) {
        expense.date
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone da Categoria
        Icon(
            imageVector = icon,
            contentDescription = expense.type,
            tint = Color(0xFFFFC107),
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF363636), shape = CircleShape)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Descrição e Data
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = expense.description.ifBlank { expense.establishment },
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = "${expense.type}  •  $formattedDate",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Valor
        val formattedAmount = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(expense.amount)
        Text(
            text = formattedAmount,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TableScreenPreview() {
    FinAiTheme {
        TableScreen()
    }
}
