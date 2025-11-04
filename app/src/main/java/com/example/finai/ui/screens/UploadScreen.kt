// Em app/src/main/java/com/example/finai/ui/screens/UploadScreen.kt
package com.example.finai.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // MUDANÇA: Voltamos para LazyColumn
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
import androidx.compose.ui.draw.clip // Importação corrigida
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.finai.R // Import para seus recursos
import com.example.finai.ui.components.CButtonAuth
import com.example.finai.ui.theme.FinAiTheme

@Composable
fun UploadScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // --- Lógica de Upload (Nova) ---
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val hasImage = imageBitmap != null || imageUri != null

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            imageBitmap = bitmap
            imageUri = null
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
            imageUri = uri
            imageBitmap = null
        }
    }

    // --- UI da Tela ---
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2A2A))
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Título
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Fazer Upload", // Título ajustado
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 2. Box de Preview da Imagem (Nova)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(0xFF363636), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!hasImage) {
                    Text(
                        text = "Sua imagem aparecerá aqui",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                AsyncImage(
                    model = imageUri ?: imageBitmap,
                    contentDescription = "Imagem selecionada",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 3. Botões de Ação (Novos)
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF363636))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Tirar Foto",
                            tint = Color(0xFFFFC107)
                        )
                        Text("Tirar Foto", color = Color.White)
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF363636))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Da Galeria",
                            tint = Color(0xFFFFC107)
                        )
                        Text("Da Galeria", color = Color.White)
                    }
                }
            }
        }

        // 4. Botão Enviar (Novo)
        item {
            Box(modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)) {
                CButtonAuth(
                    onClick = {
                        // TODO: Lógica de envio
                        Toast.makeText(context, "Enviando...", Toast.LENGTH_SHORT).show()
                    },
                    text = "ENVIAR ARQUIVO"
                    // idealmente, CButtonAuth aceitaria um 'enabled = hasImage'
                )
            }
        }

        // 5. Título "Arquivos Enviados" (Original)
        item {
            Text(
                text = "Arquivos Enviados",
                fontSize = 20.sp, // Ajustei o tamanho para criar hierarquia
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 6. Lista de Arquivos Enviados (Original)
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

        // 7. Card de Arquivos Locais (Original)
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

// Composable para cada item da lista "Arquivos Enviados" (Original)
// Esta função precisa estar neste arquivo
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
                painter = painterResource(id = R.drawable.ic_table), // Usando um ícone seu
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