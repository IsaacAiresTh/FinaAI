package com.example.finai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finai.ui.theme.FinAiTheme

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2A2A)) // Mesmo fundo das outras telas
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Título
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Meu Perfil",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth() // Alinha o título à esquerda
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 2. Informações do Perfil
        item {
            // Placeholder da Imagem de Perfil
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Foto de Perfil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop,
                alpha = 0.5f // Dando um aspecto de placeholder
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Fulano da Silva Jr.",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "fulaninho@gmail.com",
                fontSize = 16.sp,
                color = Color.LightGray
            )
            Text(
                text = "61 91234-5678",
                fontSize = 16.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 3. Botão Editar Salário e Limite
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: Adicionar ação de clique */ },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF363636))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Editar Salário e Limite",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFFFFC107) // Amarelo
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 4. Lista de Opções
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF363636))
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    ProfileOptionItem(
                        icon = Icons.Default.Tune,
                        text = "Preferências",
                        onClick = { /* TODO */ }
                    )
                    Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
                    ProfileOptionItem(
                        icon = Icons.Default.NotificationsNone,
                        text = "Notificações",
                        onClick = { /* TODO */ }
                    )
                    Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
                    ProfileOptionItem(
                        icon = Icons.Default.Shield,
                        text = "Segurança",
                        onClick = { /* TODO */ }
                    )
                    Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
                    ProfileOptionItem(
                        icon = Icons.Outlined.Accessibility,
                        text = "Acessibilidade",
                        onClick = { /* TODO */ }
                    )
                    Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
                    ProfileOptionItem(
                        icon = Icons.Default.HelpOutline,
                        text = "Contate-nos",
                        onClick = { /* TODO */ }
                    )
                    Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
                    ProfileOptionItem(
                        icon = Icons.Default.Logout,
                        text = "Sair",
                        color = Color(0xFFF44336), // Vermelho para "Sair"
                        onClick = { /* TODO */ }
                    )
                }
            }
        }
    }
}

// Composable reutilizável para cada item da lista de opções
@Composable
private fun ProfileOptionItem(
    icon: ImageVector,
    text: String,
    color: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    FinAiTheme {
        ProfileScreen()
    }
}