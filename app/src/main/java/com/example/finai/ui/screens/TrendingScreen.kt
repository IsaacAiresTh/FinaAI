package com.example.finai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finai.ui.theme.FinAiTheme

// Imports adicionados para o estado
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun TrendingScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2A2A))
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Análise Mensal",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Card Expandido (como 07/25)
        item {
            AnalysisMonthCard(
                monthYear = "07/25",
                amount = "200,00",
                isIncrease = true,
                initialExpanded = true, // <-- MUDANÇA (nome do parâmetro)
                description = "Donec in lectus efficitur erat semper suscipit non et tortor. Suspendisse convallis pharetra mauris, quis rhoncus dolor pellentesque a. Nullam euismod feugiat risus, in laoreet lorem."
            )
        }

        // Card Contraído (como 08/25)
        item {
            AnalysisMonthCard(
                monthYear = "08/25",
                amount = "450,00",
                isIncrease = false,
                // initialExpanded = false (não precisa, é o padrão) // <-- MUDANÇA
                description = "Etiam molestie at purus id iaculis. Nullam condimentum urna sodales magna fringilla porta. Phasellus quis mi ut odio faucibus tempus."
            )
        }

        item {
            AnalysisMonthCard(
                monthYear = "06/25",
                amount = "310,50",
                isIncrease = true,
                description = "Descrição do mês 06/25..."
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // <-- MUDANÇA (Necessário para o Card onClick)
@Composable
private fun AnalysisMonthCard(
    monthYear: String,
    amount: String,
    isIncrease: Boolean,
    description: String,
    initialExpanded: Boolean = false // <-- MUDANÇA (nome do parâmetro e valor padrão)
) {
    // --- GERENCIAMENTO DE ESTADO ---
    // Cada card agora "lembra" seu próprio estado de expansão
    var isExpanded by remember { mutableStateOf(initialExpanded) } // <-- MUDANÇA

    val amountColor = if (isIncrease) Color(0xFF4CAF50) else Color(0xFFF44336)
    val arrowIcon = if (isIncrease) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward

    // O ícone de expandir agora muda com base no estado 'isExpanded'
    val expandIcon = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown // <-- MUDANÇA

    Card(
        onClick = { isExpanded = !isExpanded }, // <-- MUDANÇA (Tornamos o Card clicável)
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF363636))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthYear,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "R$:$amount",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = amountColor
                    )
                    Icon(
                        imageVector = arrowIcon,
                        contentDescription = if (isIncrease) "Aumento" else "Redução",
                        tint = amountColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // O conteúdo agora é exibido com base no estado que "lembramos"
            if (isExpanded) { // <-- MUDANÇA (Baseado no estado)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Placeholder Gráfico", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Icon(
                imageVector = expandIcon,
                contentDescription = "Expandir/Contrair",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrendingScreenPreview() {
    FinAiTheme {
        TrendingScreen()
    }
}