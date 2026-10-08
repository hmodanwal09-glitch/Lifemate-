package com.example.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyState
import com.example.ui.components.LifeMateTopBar
import com.example.ui.theme.CalendarCyan
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.RemindersOrange
import com.example.ui.theme.TasksGreen
import com.example.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val pendingTasks by viewModel.pendingTasks.collectAsState()
    val allReminders by viewModel.allReminders.collectAsState()

    var calendarMonth by remember {
        mutableStateOf(Calendar.getInstance())
    }
    var selectedDateString by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }

    var showAddEventDialog by remember { mutableStateOf(false) }
    var eventTitle by remember { mutableStateOf("") }
    var eventType by remember { mutableStateOf("TASK") } // "TASK" or "REMINDER"
    var eventTime by remember { mutableStateOf("10:00 AM") }

    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    // Agenda for selected date
    val dayTasks = pendingTasks.filter { it.dueDate == selectedDateString }
    val dayReminders = allReminders.filter { it.date == selectedDateString }

    if (showAddEventDialog) {
        AlertDialog(
            onDismissRequest = { showAddEventDialog = false },
            title = { Text("Add Event on $selectedDateString", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text("Event / Task Title *") },
                        placeholder = { Text("e.g. Science Class or Family Dinner") },
                        modifier = Modifier.fillMaxWidth().testTag("calendar_event_title_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { eventType = "TASK" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = if (eventType == "TASK") androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = TasksGreen)
                            else androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Task")
                        }
                        Button(
                            onClick = { eventType = "REMINDER" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = if (eventType == "REMINDER") androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = RemindersOrange)
                            else androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Reminder")
                        }
                    }

                    if (eventType == "REMINDER") {
                        OutlinedTextField(
                            value = eventTime,
                            onValueChange = { eventTime = it },
                            label = { Text("Time (e.g. 10:00 AM)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (eventTitle.isNotBlank()) {
                            if (eventType == "TASK") {
                                viewModel.createTask(
                                    title = eventTitle,
                                    description = "Scheduled from Calendar",
                                    priority = "MEDIUM",
                                    dueDate = selectedDateString,
                                    category = "Calendar"
                                )
                            } else {
                                viewModel.createReminder(
                                    title = eventTitle,
                                    date = selectedDateString,
                                    time = eventTime,
                                    type = "GENERAL"
                                )
                            }
                            eventTitle = ""
                            showAddEventDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_calendar_event_button")
                ) {
                    Text("Save to Calendar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEventDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            LifeMateTopBar(
                title = "Family Calendar",
                userMode = currentUser?.userType,
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddEventDialog = true },
                containerColor = CalendarCyan,
                contentColor = Color.White,
                modifier = Modifier.testTag("calendar_add_event_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Calendar Event")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Month navigation header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val prev = calendarMonth.clone() as Calendar
                    prev.add(Calendar.MONTH, -1)
                    calendarMonth = prev
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                }

                Text(
                    text = monthFormat.format(calendarMonth.time),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = {
                    val next = calendarMonth.clone() as Calendar
                    next.add(Calendar.MONTH, 1)
                    calendarMonth = next
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Days of week header
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Days Grid (Calculates accurate monthly matrix)
            MonthGrid(
                calendar = calendarMonth,
                selectedDate = selectedDateString,
                onDateSelected = { selectedDateString = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Agenda for Selected Day
            Text(
                text = "Agenda for $selectedDateString",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (dayTasks.isEmpty() && dayReminders.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Event,
                    title = "No Events Scheduled",
                    message = "Tap the '+' button below to schedule tasks or reminders for this date.",
                    actionLabel = "Add Event",
                    onActionClick = { showAddEventDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dayTasks) { task ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = TasksGreen.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TasksGreen)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = "Task: ${task.title}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Priority: ${task.priority} • ${task.category}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    items(dayReminders) { reminder ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = RemindersOrange.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = RemindersOrange)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = "Reminder: ${reminder.title}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Time: ${reminder.time} • ${reminder.reminderType}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthGrid(
    calendar: Calendar,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val cal = calendar.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, 1)

    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0-based for Sun..Sat
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1

    val totalCells = ((firstDayOfWeek + maxDays + 6) / 7) * 7

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (week in 0 until (totalCells / 7)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0..6) {
                    val cellIndex = week * 7 + dayOfWeek
                    val dayNumber = cellIndex - firstDayOfWeek + 1

                    if (dayNumber in 1..maxDays) {
                        val dateString = String.format("%04d-%02d-%02d", year, month, dayNumber)
                        val isSelected = dateString == selectedDate

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .background(
                                    if (isSelected) PrimaryIndigo else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onDateSelected(dateString) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$dayNumber",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
