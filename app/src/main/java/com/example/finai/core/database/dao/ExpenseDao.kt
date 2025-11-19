package com.example.finai.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finai.core.database.entities.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    // Lista todas as despesas do usuário ordenadas por data decrescente (para TableScreen)
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getExpenses(userId: Int): Flow<List<ExpenseEntity>>

    // Soma o valor total gasto em um mês específico
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND strftime('%m/%Y', date) = :monthYear")
    suspend fun getTotalForMonth(userId: Int, monthYear: String): Double?
    
    // Para a TrendingScreen, vamos buscar tudo e agrupar no Repository/ViewModel por simplicidade no SQLite,
    // ou podemos fazer uma query mais complexa se necessário.
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date ASC")
    suspend fun getAllExpenses(userId: Int): List<ExpenseEntity>
}
