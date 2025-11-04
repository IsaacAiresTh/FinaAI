// Em app/src/main/java/com/example/finai/ui/screens/NotificationsScreen.kt
package com.example.finai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finai.ui.components.CDashboardBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    // Estados para os toggles
    var notificacoesGerais by remember { mutableStateOf(true) }
    var alertasLimite by remember { mutableStateOf(true) }
    var notificacoesPromocionais by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2A2A))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Notificações",
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
                .padding(16.dp)
        ) {
            CDashboardBox(title = "Alertas") {
                Column {
                    NotificationOptionItem(
                        title = "Notificações Gerais",
                        description = "Receber atualizações gerais do app.",
                        checked = notificacoesGerais,
                        onCheckedChange = { notificacoesGerais = it }
                    )
                    Divider(color = Color.Gray.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 8.dp))
                    NotificationOptionItem(
                        title = "Alertas de Limite",
                        description = "Avisar quando estiver próximo do seu limite de gastos.",
                        checked = alertasLimite,
                        onCheckedChange = { alertasLimite = it }
                    )
                    Divider(color = Color.Gray.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 8.dp))
                    NotificationOptionItem(
                        title = "Notificações Promocionais",
                        description = "Receber dicas financeiras e promoções.",
                        checked = notificacoesPromocionais,
                        onCheckedChange = { notificacoesPromocionais = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationOptionItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = description,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFFFFC107), // Amarelo
                checkedTrackColor = Color(0xFFFFC107).copy(alpha = 0.5f),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
            )
        )
    }
}