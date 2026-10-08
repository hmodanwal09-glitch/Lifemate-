package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.FamilyMember
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {
    @Query("SELECT * FROM family_members WHERE userId = :userId ORDER BY name ASC")
    fun getFamilyMembersByUser(userId: Long): Flow<List<FamilyMember>>

    @Query("SELECT * FROM family_members WHERE userId = :userId AND isEmergencyContact = 1 ORDER BY name ASC")
    fun getEmergencyContacts(userId: Long): Flow<List<FamilyMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(member: FamilyMember): Long

    @Update
    suspend fun updateFamilyMember(member: FamilyMember)

    @Delete
    suspend fun deleteFamilyMember(member: FamilyMember)
}
