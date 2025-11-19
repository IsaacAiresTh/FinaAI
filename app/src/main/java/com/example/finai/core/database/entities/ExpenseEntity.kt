package com.example.finai.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int, // Vincula a despesa ao usuário logado
    val type: String, // Boleto, Nota Fiscal, etc.
    val amount: Double,
    val date: String, // Formato YYYY-MM-DD para ordenação correta
    val establishment: String,
    val description: String
)

// Classe auxiliar para o resultado da consulta de agrupamento (Trending)
data class MonthlyReport(
    val monthYear: String, // Ex: "07/2025"
    val totalAmount: Double
)
