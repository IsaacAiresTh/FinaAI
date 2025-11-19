package com.example.finai.features.upload.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finai.core.ai.GeminiService
import com.example.finai.core.database.database.AppDatabase
import com.example.finai.core.database.entities.ExpenseEntity
import com.example.finai.core.ocr.OcrService
import com.example.finai.core.session.SessionManager
import com.example.finai.features.expense.data.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

data class UploadUiState(
    val imageBitmap: Bitmap? = null,
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val extractedText: String? = null,
    val analyzedData: String? = null,
    val error: String? = null,
    val isSaved: Boolean = false,
    val isManualInputRequired: Boolean = false, // Novo estado para indicar se precisa de input manual
    val pendingExpense: ExpenseEntity? = null // Armazena a despesa temporariamente enquanto aguarda o valor
)

class UploadViewModel(application: Application) : AndroidViewModel(application) {

    private val ocrService = OcrService(application.applicationContext)
    
    // TODO: Mantenha sua chave segura.
    private val geminiService = GeminiService("AIzaSyCF6A-MB0O1d74dFxJtmocv9FnQ4o-0tDk")
    
    private val expenseRepository: ExpenseRepository
    private val sessionManager: SessionManager

    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        expenseRepository = ExpenseRepository(database.expenseDao())
        sessionManager = SessionManager(application)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            ocrService.prepare()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(imageUri = uri, imageBitmap = null, extractedText = null, analyzedData = null, error = null, isSaved = false, isManualInputRequired = false, pendingExpense = null) }
        processImageFromUri(uri)
    }

    fun onImageCaptured(bitmap: Bitmap) {
        _uiState.update { it.copy(imageBitmap = bitmap, imageUri = null, extractedText = null, analyzedData = null, error = null, isSaved = false, isManualInputRequired = false, pendingExpense = null) }
        processImage(bitmap)
    }

    fun onManualValueEntered(value: Double) {
        val pending = uiState.value.pendingExpense
        if (pending != null) {
            val updatedExpense = pending.copy(amount = value)
            // Salva a despesa com o valor corrigido
            viewModelScope.launch {
                 try {
                     expenseRepository.saveExpense(updatedExpense)
                     _uiState.update { it.copy(isLoading = false, isSaved = true, isManualInputRequired = false, pendingExpense = null) }
                 } catch (e: Exception) {
                     _uiState.update { it.copy(error = "Erro ao salvar despesa: ${e.message}") }
                 }
            }
        }
    }

    fun onManualInputCancelled() {
        _uiState.update { it.copy(isManualInputRequired = false, pendingExpense = null) }
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
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ -> decoder.isMutableRequired = true }
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
            
            // 1. OCR
            val argbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val ocrText = ocrService.extractText(argbBitmap)
            
            if (ocrText.isBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "Não foi possível extrair texto da imagem.") }
                return@launch
            }
            
            _uiState.update { it.copy(extractedText = ocrText) }

            // 2. Gemini
            val analysisResult = geminiService.analyzeFinancialText(ocrText)
            _uiState.update { it.copy(analyzedData = analysisResult) }

            // 3. Parse e Salvar no SQLite
            processAnalysisResult(analysisResult)
        }
    }

    private suspend fun processAnalysisResult(jsonString: String) {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            _uiState.update { it.copy(isLoading = false, error = "Usuário não logado. Não foi possível salvar.") }
            return
        }

        try {
            // Limpeza básica caso o Gemini retorne Markdown code blocks
            val cleanJson = jsonString.replace("```json", "").replace("```", "").trim()
            val jsonObject = JSONObject(cleanJson)

            // Tratamento de data: DD/MM/AAAA -> YYYY-MM-DD
            val rawDate = jsonObject.optString("data", "")
            val formattedDate = convertDateToIso(rawDate)
            
            val amount = jsonObject.optDouble("valor", 0.0)

            val expense = ExpenseEntity(
                userId = userId,
                type = jsonObject.optString("tipo", "Outro"),
                amount = amount,
                date = formattedDate, // Salva YYYY-MM-DD
                establishment = jsonObject.optString("estabelecimento", "Desconhecido"),
                description = jsonObject.optString("descricao", "Sem descrição")
            )

            // Verifica se o valor é 0.0 ou inválido
            if (amount <= 0.0) {
                 _uiState.update { 
                     it.copy(
                         isLoading = false, 
                         isManualInputRequired = true, 
                         pendingExpense = expense 
                     ) 
                 }
            } else {
                expenseRepository.saveExpense(expense)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            }

        } catch (e: Exception) {
            Log.e("UploadViewModel", "Erro ao salvar JSON", e)
            _uiState.update { it.copy(isLoading = false, error = "Erro ao processar dados da IA: ${e.message}") }
        }
    }

    private fun convertDateToIso(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsed = inputFormat.parse(date)
            outputFormat.format(parsed!!)
        } catch (e: Exception) {
            // Se falhar ou for vazia, usa a data de hoje
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            outputFormat.format(java.util.Date())
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
