package com.example.finai.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String, // Nota: Em produção, nunca salve senhas em texto puro. Use hash (SHA-256/BCrypt).
    val cpf: String,
    val phone: String,
    val salary: Double = 0.0,
    val spendingLimit: Double = 0.0
)
