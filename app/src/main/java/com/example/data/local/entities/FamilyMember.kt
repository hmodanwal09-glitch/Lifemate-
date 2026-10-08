package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_members")
data class FamilyMember(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val relation: String, // e.g., "Parent", "Child", "Grandparent", "Doctor", "Caregiver"
    val phone: String,
    val email: String = "",
    val isEmergencyContact: Boolean = false,
    val notes: String = "",
    val userId: Long = 0
)
