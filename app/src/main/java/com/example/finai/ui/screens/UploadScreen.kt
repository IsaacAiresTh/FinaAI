package com.example.finai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finai.R // Import para seus recursos
import com.example.finai.ui.theme.FinAiTheme

@Composable
fun UploadScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2A2A)) // Mesmo fundo das outras telas
            .padding(horizontal = 16.dp)
    ) {
        // 1. Título
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Arquivos Enviados",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 2. Lista de Arquivos Enviados
        item {
            UploadedFileItem(
                title = "Lista de Compras",
                description = "Praesent quis nisl elit. Vivamus mi velit, feugiat sit amet imperdiet sed, tincidunt ac velit. Aenean nec ante in purus euismod..."
            )
        }
        item {
            UploadedFileItem(
                title = "Boleto",
                description = "Praesent quis nisl elit. Vivamus mi velit, feugiat sit amet imperdiet sed, tincidunt ac velit. Aenean nec ante in purus euismod..."
            )
        }

        // 3. Card de Arquivos Locais
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 200.dp), // Garante altura mínima
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF363636))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Arquivos Locais",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Grid de placeholders
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp), // Limita a altura da grid
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(8) { // 8 placeholders de exemplo
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f) // Garante que seja quadrado
                                    .background(
                                        Color.Gray.copy(alpha = 0.3f),
                                        RoundedCornerShape(8.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

// Composable para cada item da lista "Arquivos Enviados"
@Composable
private fun UploadedFileItem(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Placeholder da Imagem da Nota Fiscal
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 100.dp)
                .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            // Imagem de placeholder (você pode trocar o ID se tiver uma)
            Image(
                painter = painterResource(id = R.drawable.ic_table), // Usando um ícone seu como placeholder
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.LightGray,
                lineHeight = 20.sp,
                maxLines = 3 // Limita a 3 linhas como no design
            )
        }
    }
}

@Preview
@Composable
private fun UploadScreenPreview() {
    FinAiTheme {
        UploadScreen()
    }
}