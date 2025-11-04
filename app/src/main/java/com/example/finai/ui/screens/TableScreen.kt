// Em app/src/main/java/com/example/finai/ui/screens/TableScreen.kt
package com.example.finai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finai.ui.components.CDashboardBox
import com.example.finai.ui.theme.FinAiTheme

// --- 1. Modelo de Dados para uma Despesa ---
data class Expense(
    val id: Int,
    val description: String,
    val amount: Double,
    val category: String,
    val date: String,
    val icon: ImageVector
)

// --- 2. Dados Fictícios (Mock Data) ---
val mockExpenses = listOf(
    Expense(1, "Compra Mensal", 580.50, "Supermercado", "02/11/2025", Icons.Default.ShoppingCart),
    Expense(2, "Conta de Luz - NOV/25", 150.75, "Contas", "05/11/2025", Icons.Default.Lightbulb),
    Expense(3, "iFood", 89.90, "Alimentação", "07/11/2025", Icons.Default.Fastfood),
    Expense(4, "Farmácia", 120.00, "Saúde", "08/11/2025", Icons.Default.ShoppingCart),
    Expense(5, "Almoço", 45.00, "Alimentação", "09/11/2025", Icons.Default.Fastfood),
    Expense(6, "Conta de Internet", 99.90, "Contas", "10/11/2025", Icons.Default.Lightbulb)
)


// --- 3. Tela Principal (TableScreen) ---
@Composable
fun TableScreen(modifier: Modifier = Modifier) {
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

        // Box de Resumo (Reutilizando seu CDashboardBox)
        item {
            CDashboardBox(title = "Resumo do Mês") {
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
                    Text(
                        text = "R$ 1.086,05", // Soma fictícia
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFC800) // Amarelo
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
        items(mockExpenses) { expense ->
            ExpenseItem(expense = expense)
            Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        }
    }
}


// --- 4. Composable para cada Item da Lista ---
@Composable
private fun ExpenseItem(expense: Expense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone da Categoria
        Icon(
            imageVector = expense.icon,
            contentDescription = expense.category,
            tint = Color(0xFFFFC107), // Amarelo
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF363636), shape = CircleShape) // <-- CORREÇÃO: Agora compila
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Descrição e Data
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = expense.description,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "${expense.category}  •  ${expense.date}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Valor
        Text(
            text = "R$ ${"%.2f".format(expense.amount)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


// --- 5. Preview ---
@Preview(showBackground = true)
@Composable
private fun TableScreenPreview() {
    FinAiTheme {
        TableScreen()
    }
}