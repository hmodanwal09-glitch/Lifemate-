package com.example.data.repository

import com.example.data.local.dao.CategoryExpenseTotal
import com.example.data.local.dao.ExpenseDao
import com.example.data.local.entities.ExpenseItem
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    fun getExpensesByUser(userId: Long): Flow<List<ExpenseItem>> = expenseDao.getExpensesByUser(userId)
    fun getCategoryTotals(userId: Long): Flow<List<CategoryExpenseTotal>> = expenseDao.getCategoryTotals(userId)
    fun getTotalAmount(userId: Long): Flow<Double?> = expenseDao.getTotalExpenseAmount(userId)
    suspend fun insertExpense(expense: ExpenseItem): Long = expenseDao.insertExpense(expense)
    suspend fun updateExpense(expense: ExpenseItem) = expenseDao.updateExpense(expense)
    suspend fun deleteExpense(expense: ExpenseItem) = expenseDao.deleteExpense(expense)
}
