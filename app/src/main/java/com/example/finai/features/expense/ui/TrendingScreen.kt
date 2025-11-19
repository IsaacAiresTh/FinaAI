package com.example.finai.features.expense.ui

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finai.ui.theme.FinAiTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TrendingScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: TrendingViewModel = viewModel(
        factory = TrendingViewModel.Factory(application)
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
            Text(
                text = "Análise Mensal",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.monthlyTrends.isEmpty() && !uiState.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhum dado suficiente para análise.", color = Color.Gray)
                }
            }
        } else {
            items(uiState.monthlyTrends) { trend ->
                val formattedAmount = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                    .format(trend.amount)
                
                AnalysisMonthCard(
                    monthYear = trend.monthYear,
                    amount = formattedAmount,
                    isIncrease = trend.isIncrease,
                    description = trend.description,
                    initialExpanded = false
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalysisMonthCard(
    monthYear: String,
    amount: String,
    isIncrease: Boolean,
    description: String,
    initialExpanded: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(initialExpanded) }

    val amountColor = if (isIncrease) Color(0xFFF44336) else Color(0xFF4CAF50) // Aumento em despesas é "ruim" (Vermelho), redução é "bom" (Verde)
    val arrowIcon = if (isIncrease) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward
    val expandIcon = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    Card(
        onClick = { isExpanded = !isExpanded },
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
                        text = amount,
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

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Placeholder para gráfico
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Gráfico de Evolução", color = Color.White)
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
