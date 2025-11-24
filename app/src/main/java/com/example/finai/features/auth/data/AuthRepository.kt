package com.example.finai.features.auth.data

import com.example.finai.core.database.dao.UserDao
import com.example.finai.core.database.entities.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AuthRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: UserEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (userDao.getUserByEmail(user.email) != null) {
                return@withContext Result.failure(Exception("Email já cadastrado."))
            }
            userDao.insertUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.login(email, password)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Email ou senha incorretos."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserById(id: Int): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUserById(id)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Usuário não encontrado."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: UserEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            userDao.updateUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserFlow(id: Int): Flow<UserEntity?> {
        return userDao.getUserFlow(id)
    }
}
