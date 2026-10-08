package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: String = "MEDIUM", // "LOW", "MEDIUM", "HIGH"
    val status: String = "PENDING",   // "PENDING", "COMPLETED"
    val dueDate: String = "",         // YYYY-MM-DD
    val category: String = "General", // e.g. "Work", "Home", "Homework", "Family"
    val isFamilyShared: Boolean = false,
    val userId: Long = 0
) {
    @get:Ignore
    val taskId: Long
        get() = id

    @get:Ignore
    val date: String
        get() = dueDate
}

typealias Task = TaskItem
