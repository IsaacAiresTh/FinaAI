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
    val isManualInputRequired: Boolean = false,
    val pendingExpense: ExpenseEntity? = null
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
            
            // Atualiza a string de exibição com o novo valor
            val newAnalysis = """
                Estabelecimento: ${updatedExpense.establishment}
                Valor: R$ %.2f
                Data: ${convertIsoToDisplay(updatedExpense.date)}
                Tipo: ${updatedExpense.type}
                Descrição: ${updatedExpense.description}
            """.trimIndent().format(updatedExpense.amount)

            // Atualiza o estado com a despesa pendente (pronta para confirmar)
            _uiState.update { 
                it.copy(
                    isManualInputRequired = false, 
                    pendingExpense = updatedExpense,
                    analyzedData = newAnalysis
                ) 
            }
        }
    }

    fun onManualInputCancelled() {
        _uiState.update { it.copy(isManualInputRequired = false, pendingExpense = null, analyzedData = null) }
    }

    // Nova função para persistir os dados apenas quando o usuário confirmar
    fun confirmExpense() {
        val expense = uiState.value.pendingExpense ?: return
        
        viewModelScope.launch {
             try {
                 expenseRepository.saveExpense(expense)
                 // Limpa o estado após salvar com sucesso
                 _uiState.update { 
                     it.copy(
                         isLoading = false, 
                         isSaved = true, 
                         pendingExpense = null, 
                         analyzedData = null,
                         extractedText = null,
                         imageUri = null,
                         imageBitmap = null
                     ) 
                 }
             } catch (e: Exception) {
                 _uiState.update { it.copy(error = "Erro ao salvar despesa: ${e.message}") }
             }
        }
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
            
            // 3. Parse e Preparação (sem salvar ainda)
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
            val cleanJson = jsonString.replace("```json", "").replace("```", "").trim()
            val jsonObject = JSONObject(cleanJson)

            val tipo = jsonObject.optString("tipo", "Outro")
            val valor = jsonObject.optDouble("valor", 0.0)
            val data = jsonObject.optString("data", "")
            val estabelecimento = jsonObject.optString("estabelecimento", "Desconhecido")
            val descricao = jsonObject.optString("descricao", "Sem descrição")

            val formattedAnalysis = """
                Estabelecimento: $estabelecimento
                Valor: R$ %.2f
                Data: $data
                Tipo: $tipo
                Descrição: $descricao
            """.trimIndent().format(valor)

            _uiState.update { it.copy(analyzedData = formattedAnalysis) }

            val formattedDate = convertDateToIso(data)

            val expense = ExpenseEntity(
                userId = userId,
                type = tipo,
                amount = valor,
                date = formattedDate,
                establishment = estabelecimento,
                description = descricao
            )

            if (valor <= 0.0) {
                 _uiState.update { 
                     it.copy(
                         isLoading = false, 
                         isManualInputRequired = true, 
                         pendingExpense = expense 
                     ) 
                 }
            } else {
                // Não salva automaticamente. Apenas define como pendente de confirmação.
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isManualInputRequired = false, 
                        pendingExpense = expense 
                    ) 
                }
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
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            outputFormat.format(java.util.Date())
        }
    }
    
    private fun convertIsoToDisplay(dateIso: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val parsed = inputFormat.parse(dateIso)
            outputFormat.format(parsed!!)
        } catch (e: Exception) {
            dateIso
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
