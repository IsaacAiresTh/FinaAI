package com.example.finai.features.upload.ui

import android.Manifest
import android.app.Application
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.finai.ui.components.CButtonAuth
import com.example.finai.ui.components.COutlinedTextField
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@Composable
fun UploadScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: UploadViewModel = viewModel(
        factory = UploadViewModel.Factory(application)
    )
    
    val uiState by viewModel.uiState.collectAsState()

    // Efeitos colaterais para Toast de erro
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // Efeito de sucesso
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            Toast.makeText(context, "Despesa salva com sucesso!", Toast.LENGTH_SHORT).show()
            // Aguarda um pouco e reseta o estado de salvo para não exibir o toast novamente
            delay(2000)
            viewModel.resetSavedState()
        }
    }

    // Dialog para entrada manual de valor
    if (uiState.isManualInputRequired) {
        ManualValueDialog(
            onConfirm = { value -> viewModel.onManualValueEntered(value) },
            onDismiss = { viewModel.onManualInputCancelled() }
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.onImageCaptured(bitmap)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(context, "Permissão da câmera negada", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onImageSelected(uri)
        }
    }

    // --- UI da Tela ---
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2C2A2A))
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Título
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Fazer Upload",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. Box de Preview da Imagem
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color(0xFF363636), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.imageUri == null && uiState.imageBitmap == null) {
                        Text(
                            text = "Selecione uma imagem ou tire foto",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    } else {
                        AsyncImage(
                            model = uiState.imageUri ?: uiState.imageBitmap,
                            contentDescription = "Imagem selecionada",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Overlay de carregamento
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFFFFC107))
                        }
                    }
                }
            }
            
            // Exibição do Resultado do Gemini (Texto Formatado)
            item {
                if (uiState.analyzedData != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF424242)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Dados Analisados:",
                                color = Color(0xFFFFC107),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = uiState.analyzedData ?: "",
                                color = Color.White,
                                fontSize = 16.sp,
                                lineHeight = 24.sp // Melhora a legibilidade do texto multilinha
                            )
                        }
                    }
                }
            }

            // 3. Botões de Ação (Câmera e Galeria)
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Botão Câmera
                    Button(
                        onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF363636))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Tirar Foto",
                                tint = if(uiState.isLoading) Color.Gray else Color(0xFFFFC107)
                            )
                            Text("Tirar Foto", color = if(uiState.isLoading) Color.Gray else Color.White)
                        }
                    }
                    // Botão Galeria
                    Button(
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF363636))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Da Galeria",
                                tint = if(uiState.isLoading) Color.Gray else Color(0xFFFFC107)
                            )
                            Text("Da Galeria", color = if(uiState.isLoading) Color.Gray else Color.White)
                        }
                    }
                }
            }

            // 4. Botão Confirmar e Salvar
            item {
                Box(modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)) {
                    // Define o texto e a cor do botão com base no estado
                    val buttonText = when {
                        uiState.isLoading -> "ANALISANDO..."
                        uiState.pendingExpense != null -> "CONFIRMAR E SALVAR"
                        uiState.isSaved -> "SALVO COM SUCESSO"
                        else -> "AGUARDANDO IMAGEM"
                    }

                    CButtonAuth(
                        onClick = {
                            if (uiState.pendingExpense != null) {
                                viewModel.confirmExpense()
                            } else if (uiState.imageUri == null && uiState.imageBitmap == null) {
                                Toast.makeText(context, "Utilize os botões acima para selecionar uma imagem", Toast.LENGTH_SHORT).show()
                            }
                        },
                        text = buttonText
                    )
                }
            }

            // 5. Título "Arquivos Enviados Recentemente"
            item {
                Text(
                    text = "Arquivos Enviados (Recentes)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 6. Lista de Arquivos Enviados (Do Banco de Dados)
            if (uiState.recentExpenses.isEmpty()) {
                item {
                    Text("Nenhum envio recente.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                items(uiState.recentExpenses) { expense ->
                    val formattedAmount = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(expense.amount)
                    val title = expense.establishment.ifBlank { expense.description }
                    val description = "$title - $formattedAmount"
                    
                    UploadedFileItem(
                        title = expense.type,
                        description = description
                    )
                }
            }

//            // 7. Card de Arquivos Locais (Placeholder)
//            item {
//                Spacer(modifier = Modifier.height(32.dp))
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .defaultMinSize(minHeight = 200.dp),
//                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
//                    colors = CardDefaults.cardColors(containerColor = Color(0xFF363636))
//                ) {
//                    Column(modifier = Modifier.padding(16.dp)) {
//                        Text(
//                            text = "Arquivos Locais",
//                            fontSize = 20.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.White
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        LazyVerticalGrid(
//                            columns = GridCells.Fixed(4),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .heightIn(max = 300.dp),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            items(8) {
//                                Box(
//                                    modifier = Modifier
//                                        .aspectRatio(1f)
//                                        .background(
//                                            Color.Gray.copy(alpha = 0.3f),
//                                            RoundedCornerShape(8.dp)
//                                        )
//                                )
//                            }
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.height(80.dp)) // Espaço extra para não cobrir com a Nav Bar
//            }
        }
    }
}

@Composable
fun UploadedFileItem(title: String, description: String) {
    val icon = when(title) {
        "Boleto" -> Icons.Default.Description
        "Nota Fiscal" -> Icons.Default.ShoppingCart
        "Recibo" -> Icons.Default.Description
        "Alimentação" -> Icons.Default.Fastfood
        else -> Icons.Default.ShoppingCart
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFFFFC107),
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF363636), shape = CircleShape)
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = description, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
        }
    }
}

@Composable
fun ManualValueDialog(onConfirm: (Double) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF363636), // Cor de fundo do diálogo
        titleContentColor = Color.White, // Cor do título
        textContentColor = Color.White, // Cor do texto
        title = { Text("Valor não identificado", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Identificamos o documento, mas não conseguimos ler o valor. Por favor, insira manualmente:")
                Spacer(modifier = Modifier.height(16.dp))
                // Campo de texto personalizado com estilo do app
                COutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = "Valor (R$)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = text.toDoubleOrNull()
                    if (value != null && value > 0) {
                        onConfirm(value)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC107), // Amarelo padrão
                    contentColor = Color.Black
                )
            ) {
                Text("Confirmar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFFC107))
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Preview
@Composable
private fun UploadScreenPreview() {
    UploadScreen()
}
