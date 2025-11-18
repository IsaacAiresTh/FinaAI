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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: UserEntity? = null,
    val userName: String = "Usuário", // Nome de exibição (primeiro nome)
    val error: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AuthRepository
    private val sessionManager: SessionManager

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AuthRepository(database.userDao())
        sessionManager = SessionManager(application.applicationContext)
        
        loadUserData()
    }

    private fun loadUserData() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getUserById(userId)
            
            if (result.isSuccess) {
                val user = result.getOrNull()
                val firstName = user?.name?.split(" ")?.firstOrNull() ?: "Usuário"
                
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        user = user,
                        userName = firstName
                    ) 
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Erro ao carregar dados") }
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
