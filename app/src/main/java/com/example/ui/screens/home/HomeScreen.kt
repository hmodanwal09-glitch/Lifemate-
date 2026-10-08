package com.example.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.local.entities.User
import com.example.ui.components.LifeMateTopBar
import com.example.viewmodel.AppViewModel

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateTasks: () -> Unit,
    onNavigateNotes: () -> Unit,
    onNavigateReminders: () -> Unit,
    onNavigateCalendar: () -> Unit,
    onNavigateExpenses: () -> Unit,
    onNavigateFamily: () -> Unit,
    onNavigateShopping: () -> Unit,
    onNavigateAI: () -> Unit,
    onNavigateProfile: () -> Unit,
    onNavigateChildStudy: () -> Unit,
    onNavigateQuiz: () -> Unit,
    onNavigateEmergency: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    val pendingTasks by viewModel.pendingTasks.collectAsState()
    val pendingReminders by viewModel.pendingReminders.collectAsState()
    val medicineReminders by viewModel.medicineReminders.collectAsState()
    val totalExpenses by viewModel.totalExpenseAmount.collectAsState()
    val shoppingItems by viewModel.shoppingItems.collectAsState()
    val studyPlans by viewModel.studyPlans.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()
    val emergencyContacts by viewModel.emergencyContacts.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiMessage()
        }
    }

    val user = currentUser ?: User(name = "Family User", email = "user@lifemate.com", password = "", userType = "ADULT")

    Scaffold(
        topBar = {
            LifeMateTopBar(
                title = "LifeMate",
                userMode = user.userType,
                isDarkMode = isDarkMode,
                onProfileClick = onNavigateProfile,
                onToggleDarkMode = { viewModel.toggleDarkMode() }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* already on home */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    modifier = Modifier.testTag("nav_home_tab")
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateTasks,
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Tasks") },
                    label = { Text("Tasks") },
                    modifier = Modifier.testTag("nav_tasks_tab")
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateReminders,
                    icon = { Icon(Icons.Default.NotificationsActive, contentDescription = "Reminders") },
                    label = { Text("Reminders") },
                    modifier = Modifier.testTag("nav_reminders_tab")
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateAI,
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI") },
                    label = { Text("AI") },
                    modifier = Modifier.testTag("nav_ai_tab")
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateFamily,
                    icon = { Icon(Icons.Default.Diversity1, contentDescription = "Family") },
                    label = { Text("Family") },
                    modifier = Modifier.testTag("nav_family_tab")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            when (user.userType.uppercase()) {
                "CHILD" -> {
                    ChildDashboard(
                        user = user,
                        pendingHomework = pendingTasks,
                        studyPlans = studyPlans,
                        onNavigateTasks = onNavigateTasks,
                        onNavigateStudy = onNavigateChildStudy,
                        onNavigateQuiz = onNavigateQuiz,
                        onNavigateAI = onNavigateAI,
                        onNavigateReminders = onNavigateReminders,
                        onToggleTask = { viewModel.toggleTask(it) }
                    )
                }

                "SENIOR" -> {
                    SeniorDashboard(
                        user = user,
                        medicineReminders = medicineReminders,
                        appointmentReminders = pendingReminders.filter { it.reminderType == "APPOINTMENT" },
                        emergencyContacts = emergencyContacts,
                        familyContacts = familyMembers,
                        onNavigateReminders = onNavigateReminders,
                        onNavigateEmergency = onNavigateEmergency,
                        onNavigateFamily = onNavigateFamily,
                        onNavigateAI = onNavigateAI,
                        onToggleReminder = { viewModel.toggleReminder(it) },
                        onVoiceQuery = { query -> viewModel.sendAIMessage(query) }
                    )
                }

                else -> { // ADULT MODE
                    AdultDashboard(
                        user = user,
                        pendingTasks = pendingTasks,
                        pendingReminders = pendingReminders,
                        shoppingItems = shoppingItems,
                        totalExpenses = totalExpenses,
                        onNavigateTasks = onNavigateTasks,
                        onNavigateNotes = onNavigateNotes,
                        onNavigateReminders = onNavigateReminders,
                        onNavigateCalendar = onNavigateCalendar,
                        onNavigateExpenses = onNavigateExpenses,
                        onNavigateFamily = onNavigateFamily,
                        onNavigateShopping = onNavigateShopping,
                        onNavigateAI = onNavigateAI
                    )
                }
            }
        }
    }
}
