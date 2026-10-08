package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: String,                   // YYYY-MM-DD
    val time: String,                   // HH:MM AM/PM
    val status: String = "PENDING",     // "PENDING", "COMPLETED"
    val reminderType: String = "GENERAL", // "GENERAL", "MEDICINE", "STUDY", "APPOINTMENT"
    val dosageOrDetails: String = "",
    val userId: Long = 0
) {
    @get:Ignore
    val reminderId: Long
        get() = id
}

typealias Reminder = ReminderItem
