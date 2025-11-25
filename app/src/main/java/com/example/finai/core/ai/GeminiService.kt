package com.example.finai.core.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService(private val apiKey: String) {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    suspend fun analyzeFinancialText(ocrText: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            Você é um assistente financeiro especializado em extração de dados de documentos fiscais e bancários brasileiros.
            
            Abaixo está um texto bruto extraído via OCR de uma imagem. Sua tarefa é analisar este texto e extrair as seguintes informações em formato JSON:
            
            1. "tipo": Tipo do documento (ex: "Boleto", "Nota Fiscal", "Recibo", "Cupom Fiscal", "Transferência", "Outro").
            2. "valor pago": O valor total monetário encontrado (formato numérico float, ex: 150.00).
            3. "data": A data principal do documento (vencimento ou emissão) no formato DD/MM/AAAA, caso nao seja encontrado retorne a data atual.
            4. "estabelecimento": Nome da empresa, loja ou pessoa beneficiária.
            5. "descricao": Uma breve descrição de 5 a 10 palavras sobre o que é esse gasto.
            
            Se alguma informação não for encontrada ou estiver ilegível, retorne null para aquele campo.
            Não adicione formatação Markdown (como ```json) na resposta, retorne apenas o JSON puro.
            
            Texto do OCR:
            "$ocrText"
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(prompt)
            return@withContext response.text ?: "{}"
        } catch (e: Exception) {
            e.printStackTrace()
            // Retorna o erro no formato JSON para ser exibido ou tratado
            return@withContext "{ \"error\": \"${e.message}\" }"
        }
    }
}
