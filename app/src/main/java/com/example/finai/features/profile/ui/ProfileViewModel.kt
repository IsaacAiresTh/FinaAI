package com.example.finai.features.profile.ui

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

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: UserEntity? = null,
    val error: String? = null,
    val updateSuccess: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AuthRepository
    private val sessionManager: SessionManager

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AuthRepository(database.userDao())
        sessionManager = SessionManager(application.applicationContext)
        
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            _uiState.update { it.copy(error = "Usuário não logado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getUserById(userId)
            
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, user = result.getOrNull()) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Erro ao carregar perfil") }
            }
        }
    }
    
    fun updateUserFinancials(salary: Double, limit: Double) {
         val currentUser = _uiState.value.user ?: return
         val updatedUser = currentUser.copy(salary = salary, spendingLimit = limit)
         
         viewModelScope.launch {
             _uiState.update { it.copy(isLoading = true) }
             val result = repository.updateUser(updatedUser)
             if (result.isSuccess) {
                 _uiState.update { it.copy(isLoading = false, user = updatedUser, updateSuccess = true) }
             } else {
                 _uiState.update { it.copy(isLoading = false, error = "Erro ao atualizar dados") }
             }
         }
    }
    
    fun resetUpdateSuccess() {
        _uiState.update { it.copy(updateSuccess = false) }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
