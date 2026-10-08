package com.example.data.repository

import com.example.data.local.dao.ReminderDao
import com.example.data.local.entities.ReminderItem
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {
    fun getRemindersByUser(userId: Long): Flow<List<ReminderItem>> = reminderDao.getRemindersByUser(userId)
    fun getPendingReminders(userId: Long): Flow<List<ReminderItem>> = reminderDao.getPendingReminders(userId)
    fun getMedicineReminders(userId: Long): Flow<List<ReminderItem>> = reminderDao.getMedicineReminders(userId)
    suspend fun getReminderById(id: Long): ReminderItem? = reminderDao.getReminderById(id)
    suspend fun insertReminder(reminder: ReminderItem): Long = reminderDao.insertReminder(reminder)
    suspend fun updateReminder(reminder: ReminderItem) = reminderDao.updateReminder(reminder)
    suspend fun deleteReminder(reminder: ReminderItem) = reminderDao.deleteReminder(reminder)
    suspend fun toggleReminderStatus(reminder: ReminderItem) {
        val newStatus = if (reminder.status == "COMPLETED") "PENDING" else "COMPLETED"
        reminderDao.updateReminderStatus(reminder.id, newStatus)
    }
}
