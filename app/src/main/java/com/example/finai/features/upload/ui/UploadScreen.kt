// Em app/src/main/java/com/example/finai/features/upload/ui/UploadScreenDeprecated.kt
package com.example.finai.features.upload.ui

import android.Manifest
import android.app.Application
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.finai.R
import com.example.finai.ui.components.CButtonAuth
import com.example.finai.ui.theme.FinAiTheme

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
                            text = "Sua imagem aparecerá aqui",
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
            
//             Exibição do Texto Extraído (Para Debug/Confirmação)
//            item {
//                if (!uiState.extractedText.isNullOrBlank()) {
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Card(
//                        colors = CardDefaults.cardColors(containerColor = Color(0xFF424242)),
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Column(modifier = Modifier.padding(16.dp)) {
//                            Text(
//                                text = "Texto Extraído:",
//                                color = Color(0xFFFFC107),
//                                fontWeight = FontWeight.Bold
//                            )
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Text(
//                                text = uiState.extractedText ?: "",
//                                color = Color.White,
//                                fontSize = 14.sp
//                            )
//                        }
//                    }
//                }
//            }


            // 3. Botões de Ação
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

            // 4. Botão Enviar
            item {
                Box(modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)) {
                    CButtonAuth(
                        onClick = {
                            // TODO: Implementar navegação ou salvar no banco de dados
                            Toast.makeText(context, "Processando texto...", Toast.LENGTH_SHORT).show()
                        },
                        text = "ENVIAR ARQUIVO"
                        // enabled = !uiState.isLoading && (uiState.imageUri != null || uiState.imageBitmap != null)
                    )
                }
            }

            // 5. Título "Arquivos Enviados"
            item {
                Text(
                    text = "Arquivos Enviados",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 6. Lista de Arquivos Enviados
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

            // 7. Card de Arquivos Locais
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 200.dp),
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

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(8) {
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
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
}

@Composable
private fun UploadedFileItem(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 100.dp)
                .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_table),
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
                maxLines = 3
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
