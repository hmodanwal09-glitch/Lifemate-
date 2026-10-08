package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    val userType: String = "ADULT", // "CHILD", "ADULT", "SENIOR"
    val createdAt: Long = System.currentTimeMillis()
) {
    @get:Ignore
    val userId: Long
        get() = id

    @get:Ignore
    val passwordHash: String
        get() = password
}
