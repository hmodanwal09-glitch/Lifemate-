package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ExpenseItem
import kotlinx.coroutines.flow.Flow

data class CategoryExpenseTotal(
    val category: String,
    val totalAmount: Double
)

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseItem): Long

    @Update
    suspend fun updateExpense(expense: ExpenseItem)

    @Delete
    suspend fun deleteExpense(expense: ExpenseItem)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun getExpenseById(id: Long): ExpenseItem?

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC, id DESC")
    fun getExpensesByUser(userId: Long): Flow<List<ExpenseItem>>

    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC")
    fun getAllExpenses(): Flow<List<ExpenseItem>>

    @Query("SELECT category, SUM(amount) as totalAmount FROM expenses WHERE userId = :userId GROUP BY category")
    fun getCategoryTotals(userId: Long): Flow<List<CategoryExpenseTotal>>

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId")
    fun getTotalExpenseAmount(userId: Long): Flow<Double?>
}
