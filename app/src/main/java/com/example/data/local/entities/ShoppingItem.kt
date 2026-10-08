package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val isPurchased: Boolean = false,
    val category: String = "Groceries",
    val quantity: String = "1",
    val userId: Long = 0
)
