package com.example.finai.features.home.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finai.core.database.database.AppDatabase
import com.example.finai.core.database.entities.UserEntity
import com.example.finai.core.session.SessionManager
import com.example.finai.features.auth.data.AuthRepository
import com.example.finai.features.expense.data.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: UserEntity? = null,
    val userName: String = "Usuário",
    val totalSpent: Double = 0.0, // Novo campo para o total gasto
    val limit: Double = 0.0, // Limite vindo do user
    val error: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository
    private val expenseRepository: ExpenseRepository
    private val sessionManager: SessionManager

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        authRepository = AuthRepository(database.userDao())
        expenseRepository = ExpenseRepository(database.expenseDao())
        sessionManager = SessionManager(application.applicationContext)
        
        loadUserData()
        loadTotalSpent()
    }

    private fun loadUserData() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Observar mudanças no usuário para atualizar o limite em tempo real
            authRepository.getUserFlow(userId).collectLatest { user ->
                if (user != null) {
                    val firstName = user.name.split(" ").firstOrNull() ?: "Usuário"
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            user = user,
                            userName = firstName,
                            limit = user.spendingLimit
                        ) 
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Usuário não encontrado") }
                }
            }
        }
    }

    private fun loadTotalSpent() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            // Observa as despesas em tempo real
            expenseRepository.getExpenses(userId).collectLatest { expenses ->
                val total = expenses.sumOf { it.amount }
                _uiState.update { it.copy(totalSpent = total) }
            }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
