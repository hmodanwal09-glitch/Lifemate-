package com.example.data.repository

import com.example.data.local.dao.TaskDao
import com.example.data.local.entities.TaskItem
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasksByUser(userId: Long): Flow<List<TaskItem>> = taskDao.getTasksByUser(userId)
    fun getPendingTasks(userId: Long): Flow<List<TaskItem>> = taskDao.getPendingTasks(userId)
    fun getCompletedTasks(userId: Long): Flow<List<TaskItem>> = taskDao.getCompletedTasks(userId)
    fun getFamilyTasks(): Flow<List<TaskItem>> = taskDao.getFamilyTasks()

    suspend fun getTaskById(id: Long): TaskItem? = taskDao.getTaskById(id)
    suspend fun insertTask(task: TaskItem): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: TaskItem) = taskDao.updateTask(task)
    suspend fun deleteTask(task: TaskItem) = taskDao.deleteTask(task)
    suspend fun toggleTaskStatus(task: TaskItem) {
        val newStatus = if (task.status == "COMPLETED") "PENDING" else "COMPLETED"
        taskDao.updateTaskStatus(task.id, newStatus)
    }
}
