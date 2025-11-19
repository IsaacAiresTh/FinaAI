package com.example.finai.core.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finai.core.database.dao.ExpenseDao
import com.example.finai.core.database.dao.UserDao
import com.example.finai.core.database.entities.ExpenseEntity
import com.example.finai.core.database.entities.UserEntity

@Database(entities = [UserEntity::class, ExpenseEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finai_database"
                )
                .fallbackToDestructiveMigration() // Para simplificar o desenvolvimento, recria o banco se mudar a versão
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
