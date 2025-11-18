package com.example.finai.features.upload.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finai.core.ocr.OcrService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UploadUiState(
    val imageBitmap: Bitmap? = null,
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val extractedText: String? = null,
    val error: String? = null
)

class UploadViewModel(application: Application) : AndroidViewModel(application) {

    private val ocrService = OcrService(application.applicationContext)

    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    init {
        // Inicializa o OCR Service em segundo plano assim que o ViewModel é criado
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            ocrService.prepare()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(imageUri = uri, imageBitmap = null, extractedText = null, error = null) }
        processImageFromUri(uri)
    }

    fun onImageCaptured(bitmap: Bitmap) {
        _uiState.update { it.copy(imageBitmap = bitmap, imageUri = null, extractedText = null, error = null) }
        processImage(bitmap)
    }

    private fun processImageFromUri(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(getApplication<Application>().contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(getApplication<Application>().contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true 
                    }
                }
                processImage(bitmap)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Erro ao carregar imagem: ${e.message}") }
            }
        }
    }

    private fun processImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Garante que a imagem esteja no formato ARGB_8888, exigido pelo Tesseract
            val argbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            
            val text = ocrService.extractText(argbBitmap)
            _uiState.update { 
                it.copy(
                    isLoading = false, 
                    extractedText = text,
                    imageBitmap = argbBitmap // Atualiza com o bitmap processado
                ) 
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        ocrService.stop()
        ocrService.recycle()
    }
    
    // Factory para criar o ViewModel com Application
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
                return UploadViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
