package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.TaskItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): TaskItem?

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY id DESC")
    fun getTasksByUser(userId: Long): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND status = 'PENDING' ORDER BY id DESC")
    fun getPendingTasks(userId: Long): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND status = 'COMPLETED' ORDER BY id DESC")
    fun getCompletedTasks(userId: Long): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE isFamilyShared = 1 ORDER BY id DESC")
    fun getFamilyTasks(): Flow<List<TaskItem>>

    @Query("UPDATE tasks SET status = :status WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: String)
}
