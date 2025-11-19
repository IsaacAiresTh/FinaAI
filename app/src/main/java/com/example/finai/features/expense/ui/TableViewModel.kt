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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TableUiState(
    val isLoading: Boolean = false,
    val expenses: List<ExpenseEntity> = emptyList(),
    val totalAmount: Double = 0.0,
    val error: String? = null
)

class TableViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository: ExpenseRepository
    private val sessionManager: SessionManager

    private val _uiState = MutableStateFlow(TableUiState())
    val uiState: StateFlow<TableUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        expenseRepository = ExpenseRepository(database.expenseDao())
        sessionManager = SessionManager(application)
        
        loadExpenses()
    }

    private fun loadExpenses() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            _uiState.update { it.copy(error = "Usuário não logado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Coleta o Flow do repositório para atualizações em tempo real
            expenseRepository.getExpenses(userId).collectLatest { list ->
                val total = list.sumOf { it.amount }
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        expenses = list,
                        totalAmount = total
                    )
                }
            }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TableViewModel::class.java)) {
                return TableViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
