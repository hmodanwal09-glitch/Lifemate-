package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val colorTag: String = "#8B5CF6", // Hex string
    val createdAt: Long = System.currentTimeMillis(),
    val userId: Long = 0
) {
    @get:Ignore
    val noteId: Long
        get() = id
}

typealias Note = NoteItem
