package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ReminderItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderItem): Long

    @Update
    suspend fun updateReminder(reminder: ReminderItem)

    @Delete
    suspend fun deleteReminder(reminder: ReminderItem)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Long)

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: Long): ReminderItem?

    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY date ASC, time ASC")
    fun getRemindersByUser(userId: Long): Flow<List<ReminderItem>>

    @Query("SELECT * FROM reminders ORDER BY date ASC, time ASC")
    fun getAllReminders(): Flow<List<ReminderItem>>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND status = 'PENDING' ORDER BY date ASC, time ASC")
    fun getPendingReminders(userId: Long): Flow<List<ReminderItem>>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND reminderType = 'MEDICINE' ORDER BY date ASC, time ASC")
    fun getMedicineReminders(userId: Long): Flow<List<ReminderItem>>

    @Query("UPDATE reminders SET status = :status WHERE id = :id")
    suspend fun updateReminderStatus(id: Long, status: String)
}
