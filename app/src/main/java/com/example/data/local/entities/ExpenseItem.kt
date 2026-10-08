package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String, // "Food", "Travel", "Medicine", "Shopping", "Education", "Other"
    val date: String,     // YYYY-MM-DD
    val description: String = "",
    val userId: Long = 0
) {
    @get:Ignore
    val expenseId: Long
        get() = id
}

typealias Expense = ExpenseItem
