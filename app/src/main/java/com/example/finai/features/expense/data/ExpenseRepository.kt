package com.example.finai.features.expense.data

import com.example.finai.core.database.dao.ExpenseDao
import com.example.finai.core.database.entities.ExpenseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    suspend fun saveExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        expenseDao.insertExpense(expense)
    }

    fun getExpenses(userId: Int): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpenses(userId)
    }

    suspend fun getAllExpensesList(userId: Int): List<ExpenseEntity> = withContext(Dispatchers.IO) {
        return@withContext expenseDao.getAllExpenses(userId)
    }
}
