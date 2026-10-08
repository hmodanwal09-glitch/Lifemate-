package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ai.AIRepository
import com.example.ai.AISuggestedReminder
import com.example.ai.ChatMessage
import com.example.ai.DefaultAIService
import com.example.data.local.dao.CategoryExpenseTotal
import com.example.data.local.database.AppDatabase
import com.example.data.local.entities.ExpenseItem
import com.example.data.local.entities.FamilyMember
import com.example.data.local.entities.NoteItem
import com.example.data.local.entities.QuizQuestion
import com.example.data.local.entities.ReminderItem
import com.example.data.local.entities.ShoppingItem
import com.example.data.local.entities.StudyPlanItem
import com.example.data.local.entities.TaskItem
import com.example.data.local.entities.User
import com.example.data.repository.AuthRepository
import com.example.data.repository.ChildStudyRepository
import com.example.data.repository.ExpenseRepository
import com.example.data.repository.FamilyRepository
import com.example.data.repository.NoteRepository
import com.example.data.repository.ReminderRepository
import com.example.data.repository.TaskRepository
import com.example.notifications.NotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel(
    application: Application,
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository,
    private val noteRepository: NoteRepository,
    private val reminderRepository: ReminderRepository,
    private val expenseRepository: ExpenseRepository,
    private val familyRepository: FamilyRepository,
    private val childStudyRepository: ChildStudyRepository,
    private val aiRepository: AIRepository
) : AndroidViewModel(application) {

    // Current User & Mode
    val currentUser: StateFlow<User?> = authRepository.currentUser
    val allUsers: StateFlow<List<User>> = authRepository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dark Mode Toggle
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Search query for notes
    private val _noteSearchQuery = MutableStateFlow("")
    val noteSearchQuery: StateFlow<String> = _noteSearchQuery.asStateFlow()

    fun setNoteSearchQuery(query: String) {
        _noteSearchQuery.value = query
    }

    // --- Reactive Database Streams (Automatically react to currentUser) ---

    val pendingTasks: StateFlow<List<TaskItem>> = currentUser.flatMapLatest { user ->
        if (user != null) taskRepository.getPendingTasks(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTasks: StateFlow<List<TaskItem>> = currentUser.flatMapLatest { user ->
        if (user != null) taskRepository.getCompletedTasks(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val familyTasks: StateFlow<List<TaskItem>> = taskRepository.getFamilyTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteItem>> = currentUser.flatMapLatest { user ->
        if (user != null) {
            _noteSearchQuery.flatMapLatest { query ->
                if (query.isBlank()) noteRepository.getNotesByUser(user.id)
                else noteRepository.searchNotes(user.id, query)
            }
        } else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReminders: StateFlow<List<ReminderItem>> = currentUser.flatMapLatest { user ->
        if (user != null) reminderRepository.getRemindersByUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingReminders: StateFlow<List<ReminderItem>> = currentUser.flatMapLatest { user ->
        if (user != null) reminderRepository.getPendingReminders(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val medicineReminders: StateFlow<List<ReminderItem>> = currentUser.flatMapLatest { user ->
        if (user != null) reminderRepository.getMedicineReminders(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseItem>> = currentUser.flatMapLatest { user ->
        if (user != null) expenseRepository.getExpensesByUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categoryExpenseTotals: StateFlow<List<CategoryExpenseTotal>> = currentUser.flatMapLatest { user ->
        if (user != null) expenseRepository.getCategoryTotals(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalExpenseAmount: StateFlow<Double> = currentUser.flatMapLatest { user ->
        if (user != null) expenseRepository.getTotalAmount(user.id) else flowOf(0.0)
    }.flatMapLatest { flowOf(it ?: 0.0) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val familyMembers: StateFlow<List<FamilyMember>> = currentUser.flatMapLatest { user ->
        if (user != null) familyRepository.getFamilyMembers(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val emergencyContacts: StateFlow<List<FamilyMember>> = currentUser.flatMapLatest { user ->
        if (user != null) familyRepository.getEmergencyContacts(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shoppingItems: StateFlow<List<ShoppingItem>> = currentUser.flatMapLatest { user ->
        if (user != null) familyRepository.getShoppingItems(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyPlans: StateFlow<List<StudyPlanItem>> = currentUser.flatMapLatest { user ->
        if (user != null) childStudyRepository.getStudyPlans(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizQuestions: StateFlow<List<QuizQuestion>> = childStudyRepository.getAllQuizQuestions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Messages & Loading
    val aiMessages: StateFlow<List<ChatMessage>> = aiRepository.messages
    val isAiLoading: StateFlow<Boolean> = aiRepository.isLoading

    // UI Feedback Banner / Snackbar message
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun showMessage(msg: String) {
        _uiMessage.value = msg
    }

    // --- Authentication Actions ---

    fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.login(email, pass)
            result.onSuccess {
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, pass: String, userType: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.register(name, email, pass, userType)
            result.onSuccess {
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Registration failed")
            }
        }
    }

    fun quickDemoLogin(type: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val existing = allUsers.value.find { it.userType.equals(type, ignoreCase = true) }
            if (existing != null) {
                authRepository.setCurrentUser(existing)
                onSuccess()
            } else {
                // Register standard demo user
                val demoName = when (type) {
                    "CHILD" -> "Leo Miller"
                    "SENIOR" -> "Grandpa Robert"
                    else -> "Sarah Miller"
                }
                val demoEmail = "${type.lowercase()}@lifemate.com"
                authRepository.register(demoName, demoEmail, "Password123", type).onSuccess {
                    onSuccess()
                }
            }
        }
    }

    fun switchUserMode(newType: String) {
        viewModelScope.launch {
            authRepository.switchUserType(newType)
            showMessage("Switched to $newType Mode")
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        authRepository.logout()
        onLoggedOut()
    }

    // --- Task Actions ---

    fun createTask(
        title: String,
        description: String,
        priority: String,
        dueDate: String,
        category: String,
        isFamilyShared: Boolean = false
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val task = TaskItem(
                title = title.trim(),
                description = description.trim(),
                priority = priority,
                dueDate = dueDate,
                category = category,
                isFamilyShared = isFamilyShared,
                userId = user.id
            )
            taskRepository.insertTask(task)
            showMessage("Task added: ${task.title}")
        }
    }

    fun toggleTask(task: TaskItem) {
        viewModelScope.launch {
            taskRepository.toggleTaskStatus(task)
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            showMessage("Task deleted")
        }
    }

    // --- Note Actions ---

    fun createOrUpdateNote(id: Long, title: String, content: String, colorTag: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            if (id == 0L) {
                noteRepository.insertNote(
                    NoteItem(
                        title = title.trim(),
                        content = content.trim(),
                        colorTag = colorTag,
                        userId = user.id
                    )
                )
                showMessage("Note saved")
            } else {
                noteRepository.updateNote(
                    NoteItem(
                        id = id,
                        title = title.trim(),
                        content = content.trim(),
                        colorTag = colorTag,
                        userId = user.id
                    )
                )
                showMessage("Note updated")
            }
        }
    }

    fun deleteNote(note: NoteItem) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
            showMessage("Note deleted")
        }
    }

    // --- Reminder Actions ---

    fun createReminder(
        title: String,
        date: String,
        time: String,
        type: String = "GENERAL",
        dosageOrDetails: String = "",
        triggerNotificationTest: Boolean = true
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val reminder = ReminderItem(
                title = title.trim(),
                date = date,
                time = time,
                reminderType = type,
                dosageOrDetails = dosageOrDetails.trim(),
                userId = user.id
            )
            val id = reminderRepository.insertReminder(reminder)
            showMessage("Reminder set for $date at $time")

            if (triggerNotificationTest) {
                val notified = NotificationHelper.showReminderNotification(
                    context = getApplication(),
                    notificationId = id.toInt(),
                    title = reminder.title,
                    message = if (dosageOrDetails.isNotBlank()) dosageOrDetails else "Scheduled for $date at $time",
                    reminderType = type
                )
                if (notified) {
                    showMessage("Reminder saved & Notification posted!")
                }
            }
        }
    }

    fun toggleReminder(reminder: ReminderItem) {
        viewModelScope.launch {
            reminderRepository.toggleReminderStatus(reminder)
        }
    }

    fun deleteReminder(reminder: ReminderItem) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)
            showMessage("Reminder deleted")
        }
    }

    // --- Expense Actions ---

    fun createExpense(amount: Double, category: String, date: String, description: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val item = ExpenseItem(
                amount = amount,
                category = category,
                date = date,
                description = description.trim(),
                userId = user.id
            )
            expenseRepository.insertExpense(item)
            showMessage("Expense of $$amount added")
        }
    }

    fun deleteExpense(expense: ExpenseItem) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
            showMessage("Expense removed")
        }
    }

    // --- Family Actions ---

    fun createFamilyMember(
        name: String,
        relation: String,
        phone: String,
        email: String,
        isEmergency: Boolean,
        notes: String
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val member = FamilyMember(
                name = name.trim(),
                relation = relation.trim(),
                phone = phone.trim(),
                email = email.trim(),
                isEmergencyContact = isEmergency,
                notes = notes.trim(),
                userId = user.id
            )
            familyRepository.insertFamilyMember(member)
            showMessage("Family contact added: $name")
        }
    }

    fun deleteFamilyMember(member: FamilyMember) {
        viewModelScope.launch {
            familyRepository.deleteFamilyMember(member)
            showMessage("Contact removed")
        }
    }

    fun addShoppingItem(title: String, category: String = "Groceries", quantity: String = "1") {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            familyRepository.insertShoppingItem(
                ShoppingItem(
                    title = title.trim(),
                    category = category,
                    quantity = quantity,
                    userId = user.id
                )
            )
        }
    }

    fun toggleShoppingItem(item: ShoppingItem) {
        viewModelScope.launch {
            familyRepository.toggleShoppingItem(item)
        }
    }

    fun deleteShoppingItem(item: ShoppingItem) {
        viewModelScope.launch {
            familyRepository.deleteShoppingItem(item)
        }
    }

    // --- Child Study & Quiz Actions ---

    fun createStudyPlan(subject: String, topic: String, date: String, durationMinutes: Int, notes: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            childStudyRepository.insertStudyPlan(
                StudyPlanItem(
                    subject = subject.trim(),
                    topic = topic.trim(),
                    targetDate = date,
                    durationMinutes = durationMinutes,
                    notes = notes.trim(),
                    userId = user.id
                )
            )
            showMessage("Study goal scheduled for $subject")
        }
    }

    fun toggleStudyPlan(plan: StudyPlanItem) {
        viewModelScope.launch {
            childStudyRepository.toggleStudyPlan(plan)
        }
    }

    fun deleteStudyPlan(plan: StudyPlanItem) {
        viewModelScope.launch {
            childStudyRepository.deleteStudyPlan(plan)
        }
    }

    // --- AI Assistant Actions ---

    fun sendAIMessage(prompt: String) {
        val mode = currentUser.value?.userType ?: "ADULT"
        viewModelScope.launch {
            aiRepository.sendMessage(prompt, mode)
        }
    }

    fun clearAIChat() {
        aiRepository.clearHistory()
    }

    fun addReminderFromAI(suggested: AISuggestedReminder) {
        createReminder(
            title = suggested.title,
            date = suggested.date,
            time = suggested.time,
            type = suggested.type,
            dosageOrDetails = "Suggested by LifeMate AI Assistant",
            triggerNotificationTest = true
        )
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = AppDatabase.getDatabase(application, kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO))
                    val authRepo = AuthRepository(db.userDao())
                    val taskRepo = TaskRepository(db.taskDao())
                    val noteRepo = NoteRepository(db.noteDao())
                    val reminderRepo = ReminderRepository(db.reminderDao())
                    val expenseRepo = ExpenseRepository(db.expenseDao())
                    val familyRepo = FamilyRepository(db.familyDao(), db.shoppingDao())
                    val childStudyRepo = ChildStudyRepository(db.childStudyDao())
                    val aiService = DefaultAIService()
                    val aiRepo = AIRepository(aiService)

                    return AppViewModel(
                        application,
                        authRepo,
                        taskRepo,
                        noteRepo,
                        reminderRepo,
                        expenseRepo,
                        familyRepo,
                        childStudyRepo,
                        aiRepo
                    ) as T
                }
            }
        }
    }
}
