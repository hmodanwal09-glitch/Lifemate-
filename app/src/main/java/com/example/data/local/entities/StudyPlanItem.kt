package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_plans")
data class StudyPlanItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val topic: String,
    val targetDate: String,
    val durationMinutes: Int = 45,
    val isCompleted: Boolean = false,
    val notes: String = "",
    val userId: Long = 0
)
