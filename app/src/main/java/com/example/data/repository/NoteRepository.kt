package com.example.data.repository

import com.example.data.local.dao.NoteDao
import com.example.data.local.entities.NoteItem
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    fun getNotesByUser(userId: Long): Flow<List<NoteItem>> = noteDao.getNotesByUser(userId)
    fun searchNotes(userId: Long, query: String): Flow<List<NoteItem>> = noteDao.searchNotes(userId, query)
    suspend fun getNoteById(id: Long): NoteItem? = noteDao.getNoteById(id)
    suspend fun insertNote(note: NoteItem): Long = noteDao.insertNote(note)
    suspend fun updateNote(note: NoteItem) = noteDao.updateNote(note)
    suspend fun deleteNote(note: NoteItem) = noteDao.deleteNote(note)
}
