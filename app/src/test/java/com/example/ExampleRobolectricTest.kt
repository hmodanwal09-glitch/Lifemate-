package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ai.DefaultAIService
import com.example.data.local.database.AppDatabase
import com.example.data.local.entities.ExpenseItem
import com.example.data.local.entities.NoteItem
import com.example.data.local.entities.ReminderItem
import com.example.data.local.entities.TaskItem
import com.example.data.local.entities.User
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("LifeMate", appName)
    }

    @Test
    fun `test password hashing is deterministic and non-plain`() {
        val hash1 = AuthRepository.hashPassword("Password123")
        val hash2 = AuthRepository.hashPassword("Password123")
        assertEquals(hash1, hash2)
        assertTrue(hash1.length >= 64)
    }

    @Test
    fun `test ai service handles reminder command`() = runBlocking {
        val aiService = DefaultAIService()
        val response = aiService.askAssistant("Remind me to study Java tomorrow at 7 PM", "ADULT")
        assertNotNull(response.suggestedReminder)
        assertEquals("07:00 PM", response.suggestedReminder?.time)
    }

    @Test
    fun `test ai service handles education question`() = runBlocking {
        val aiService = DefaultAIService()
        val response = aiService.askAssistant("Explain photosynthesis in simple language", "CHILD")
        assertTrue(response.answer.contains("Photosynthesis", ignoreCase = true))
    }

    @Test
    fun `test User CRUD operations`() = runBlocking {
        val userDao = db.userDao()
        val user = User(
            name = "Test User",
            email = "test@lifemate.com",
            password = "hash123",
            userType = "ADULT"
        )
        val id = userDao.insertUser(user)
        assertTrue(id > 0)

        val retrieved = userDao.getUserByEmail("test@lifemate.com")
        assertNotNull(retrieved)
        assertEquals("Test User", retrieved?.name)
        assertEquals("hash123", retrieved?.password)
        assertEquals("ADULT", retrieved?.userType)
        assertEquals(id, retrieved?.userId)

        val loginUser = userDao.login("test@lifemate.com", "hash123")
        assertNotNull(loginUser)

        val updated = retrieved!!.copy(name = "Updated Name")
        userDao.updateUser(updated)
        assertEquals("Updated Name", userDao.getUserByIdSync(id)?.name)

        userDao.deleteUserById(id)
        assertNull(userDao.getUserByEmail("test@lifemate.com"))
    }

    @Test
    fun `test Task CRUD operations`() = runBlocking {
        val taskDao = db.taskDao()
        val task = TaskItem(
            title = "Math homework",
            description = "Solve problems 1 to 10",
            priority = "HIGH",
            status = "PENDING",
            dueDate = "2026-10-15",
            category = "Homework",
            userId = 1L
        )
        val id = taskDao.insertTask(task)
        assertTrue(id > 0)

        val retrieved = taskDao.getTaskById(id)
        assertNotNull(retrieved)
        assertEquals("Math homework", retrieved?.title)
        assertEquals("2026-10-15", retrieved?.date)

        taskDao.updateTaskStatus(id, "COMPLETED")
        val completed = taskDao.getTaskById(id)
        assertEquals("COMPLETED", completed?.status)

        taskDao.deleteTaskById(id)
        assertNull(taskDao.getTaskById(id))
    }

    @Test
    fun `test Note CRUD operations`() = runBlocking {
        val noteDao = db.noteDao()
        val note = NoteItem(
            title = "Grocery list",
            content = "Apples, Milk, Bread",
            colorTag = "#8B5CF6",
            userId = 1L
        )
        val id = noteDao.insertNote(note)
        assertTrue(id > 0)

        val retrieved = noteDao.getNoteById(id)
        assertNotNull(retrieved)
        assertEquals("Grocery list", retrieved?.title)

        val searchResult = noteDao.searchNotes(1L, "Apples").first()
        assertEquals(1, searchResult.size)

        noteDao.deleteNoteById(id)
        assertNull(noteDao.getNoteById(id))
    }

    @Test
    fun `test Reminder CRUD operations`() = runBlocking {
        val reminderDao = db.reminderDao()
        val reminder = ReminderItem(
            title = "Blood Pressure Pill",
            date = "2026-10-09",
            time = "08:00 AM",
            status = "PENDING",
            reminderType = "MEDICINE",
            dosageOrDetails = "1 pill with water",
            userId = 1L
        )
        val id = reminderDao.insertReminder(reminder)
        assertTrue(id > 0)

        val retrieved = reminderDao.getReminderById(id)
        assertNotNull(retrieved)
        assertEquals("Blood Pressure Pill", retrieved?.title)

        val meds = reminderDao.getMedicineReminders(1L).first()
        assertEquals(1, meds.size)

        reminderDao.updateReminderStatus(id, "COMPLETED")
        assertEquals("COMPLETED", reminderDao.getReminderById(id)?.status)

        reminderDao.deleteReminderById(id)
        assertNull(reminderDao.getReminderById(id))
    }

    @Test
    fun `test Expense CRUD operations`() = runBlocking {
        val expenseDao = db.expenseDao()
        val expense = ExpenseItem(
            amount = 54.50,
            category = "Food",
            date = "2026-10-08",
            description = "Organic groceries",
            userId = 1L
        )
        val id = expenseDao.insertExpense(expense)
        assertTrue(id > 0)

        val retrieved = expenseDao.getExpenseById(id)
        assertNotNull(retrieved)
        assertEquals(54.50, retrieved?.amount ?: 0.0, 0.01)

        val total = expenseDao.getTotalExpenseAmount(1L).first()
        assertEquals(54.50, total ?: 0.0, 0.01)

        val categoryTotals = expenseDao.getCategoryTotals(1L).first()
        assertEquals(1, categoryTotals.size)
        assertEquals("Food", categoryTotals[0].category)

        expenseDao.deleteExpenseById(id)
        assertNull(expenseDao.getExpenseById(id))
    }
}
