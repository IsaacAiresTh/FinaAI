package com.example.finai.features.expense.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finai.core.database.database.AppDatabase
import com.example.finai.core.database.entities.ExpenseEntity
import com.example.finai.core.session.SessionManager
import com.example.finai.features.expense.data.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

data class MonthlyTrend(
    val monthYear: String, // Ex: "07/25"
    val amount: Double,
    val isIncrease: Boolean, // Se aumentou em relação ao mês anterior
    val description: String = ""
)

data class TrendingUiState(
    val isLoading: Boolean = false,
    val monthlyTrends: List<MonthlyTrend> = emptyList(),
    val error: String? = null
)

class TrendingViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository: ExpenseRepository
    private val sessionManager: SessionManager

    private val _uiState = MutableStateFlow(TrendingUiState())
    val uiState: StateFlow<TrendingUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        expenseRepository = ExpenseRepository(database.expenseDao())
        sessionManager = SessionManager(application)
        
        loadTrendingData()
    }

    private fun loadTrendingData() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            _uiState.update { it.copy(error = "Usuário não logado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Busca todas as despesas ordenadas por data (ascendente no DAO, mas aqui vamos agrupar)
            val allExpenses = expenseRepository.getAllExpensesList(userId)
            
            if (allExpenses.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, monthlyTrends = emptyList()) }
                return@launch
            }

            // Agrupa por mês/ano
            // Formato da chave: "yyyy-MM" para ordenação correta
            val dateFormatKey = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            // Formato de exibição: "MM/yy"
            val dateFormatDisplay = SimpleDateFormat("MM/yy", Locale.getDefault())

            val groupedExpenses = allExpenses.groupBy { 
                try {
                    dateFormatKey.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)!!)
                } catch (e: Exception) {
                    "Unknown"
                }
            }.toSortedMap(compareByDescending { it }) // Ordena do mês mais recente para o mais antigo

            val trends = mutableListOf<MonthlyTrend>()
            val monthKeys = groupedExpenses.keys.toList() // Lista de chaves ordenadas (mais recente primeiro)

            // Itera sobre os meses para calcular a tendência comparando com o mês anterior (que está 'depois' na lista sortedDescending ou precisamos buscar pela lógica)
            // Para comparar corretamente, vamos usar a lista ordenada
            
            for (i in monthKeys.indices) {
                val currentMonthKey = monthKeys[i]
                val currentExpenses = groupedExpenses[currentMonthKey] ?: emptyList()
                val currentTotal = currentExpenses.sumOf { it.amount }
                
                // Tenta pegar o mês anterior (que seria o próximo na lista cronológica, mas aqui estamos invertidos?)
                // Espere, se ordenamos DESC (2025-11, 2025-10), o "mês anterior" cronológico é o índice i+1.
                
                var isIncrease = false
                if (i < monthKeys.size - 1) {
                    val prevMonthKey = monthKeys[i + 1]
                    val prevExpenses = groupedExpenses[prevMonthKey] ?: emptyList()
                    val prevTotal = prevExpenses.sumOf { it.amount }
                    
                    isIncrease = currentTotal > prevTotal
                } else {
                    // Se não tem mês anterior (é o primeiro registro), assumimos true ou false
                    isIncrease = true 
                }

                // Formata a data para exibição
                val displayDate = try {
                    dateFormatDisplay.format(dateFormatKey.parse(currentMonthKey)!!)
                } catch (e: Exception) {
                    currentMonthKey
                }
                
                // Descrição simples baseada nos dados
                val topCategory = currentExpenses.groupBy { it.type }
                    .maxByOrNull { it.value.size }?.key ?: "Gastos diversos"
                
                val description = "Neste mês, a categoria com mais gastos foi '$topCategory'. Total de ${currentExpenses.size} registros processados."

                trends.add(
                    MonthlyTrend(
                        monthYear = displayDate,
                        amount = currentTotal,
                        isIncrease = isIncrease,
                        description = description
                    )
                )
            }

            _uiState.update { 
                it.copy(
                    isLoading = false, 
                    monthlyTrends = trends
                ) 
            }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TrendingViewModel::class.java)) {
                return TrendingViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
