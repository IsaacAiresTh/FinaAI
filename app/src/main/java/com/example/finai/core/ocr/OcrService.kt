package com.example.finai.core.ocr

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.finai.R
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class OcrService(private val context: Context) {

    private val tess = TessBaseAPI()
    private val folderTessDataName: String = "tessdata"
    private val language = "por" // Definindo português como padrão

    // Variável para controlar se o serviço foi inicializado corretamente
    private var isInitialized = false

    /**
     * Prepara o Tesseract para uso. Deve ser chamado em uma coroutine (IO).
     * Verifica e copia os arquivos de dados necessários.
     */
    suspend fun prepare() = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext

        val pathDir = context.getExternalFilesDir(null)
        if (pathDir == null) {
            Log.e("OcrService", "External files directory not available.")
            return@withContext
        }

        val tessDataPath = File(pathDir, folderTessDataName)
        if (!tessDataPath.exists()) {
            if (!tessDataPath.mkdir()) {
                Log.e("OcrService", "Failed to create tessdata directory.")
                return@withContext
            }
        }

        // Copia o arquivo de linguagem se não existir
        // Certifique-se de ter o arquivo 'por.traineddata' em res/raw/por
        val trainFile = File(tessDataPath, "$language.traineddata")
        if (!trainFile.exists()) {
            try {
                copyFile(R.raw.por, trainFile)
            } catch (e: Exception) {
                Log.e("OcrService", "Error copying traineddata file", e)
                return@withContext
            }
        }

        // Inicializa a API do Tesseract
        try {
            val success = tess.init(pathDir.absolutePath, language)
            if (success) {
                isInitialized = true
                Log.i("OcrService", "Tesseract initialized successfully")
            } else {
                Log.e("OcrService", "Tesseract initialization failed")
            }
        } catch (e: Exception) {
            Log.e("OcrService", "Exception initializing Tesseract", e)
        }
    }

    private fun copyFile(resourceId: Int, destinationFile: File) {
        context.resources.openRawResource(resourceId).use { inputStream ->
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    /**
     * Processa uma imagem para extrair seu texto.
     * Deve ser chamado em uma coroutine.
     */
    suspend fun extractText(bitmap: Bitmap): String = withContext(Dispatchers.Default) {
        if (!isInitialized) {
            // Tenta inicializar se ainda não foi
            prepare()
            if (!isInitialized) return@withContext "Error: OCR Service not initialized."
        }

        return@withContext try {
            tess.setImage(bitmap)
            tess.utF8Text ?: ""
        } catch (e: Exception) {
            Log.e("OcrService", "Error extracting text", e)
            "Error extracting text: ${e.message}"
        }
    }

    /**
     * Libera recursos. Deve ser chamado quando o ViewModel for limpo.
     */
    fun stop() {
        try {
            tess.stop() // Para qualquer reconhecimento em andamento
        } catch (e: Exception) {
            Log.e("OcrService", "Error stopping Tesseract", e)
        }
    }
    
    fun recycle() {
        try {
            tess.recycle()
        } catch (e: Exception) {
            Log.e("OcrService", "Error recycling Tesseract", e)
        }
    }
}
