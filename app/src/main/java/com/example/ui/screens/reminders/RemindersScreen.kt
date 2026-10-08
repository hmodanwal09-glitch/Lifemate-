package com.example.ui.screens.reminders

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.local.entities.ReminderItem
import com.example.notifications.NotificationHelper
import com.example.ui.components.EmptyState
import com.example.ui.components.LifeMateTopBar
import com.example.ui.components.VoiceInputDialog
import com.example.ui.theme.CalendarCyan
import com.example.ui.theme.FamilyPink
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.RemindersOrange
import com.example.ui.theme.SeniorGreen
import com.example.ui.theme.SeniorYellow
import com.example.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RemindersScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val allReminders by viewModel.allReminders.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, MEDICINE, STUDY, APPOINTMENT, GENERAL
    var showAddDialog by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }

    // Dialog state
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var time by remember { mutableStateOf("08:00 AM") }
    var type by remember { mutableStateOf("GENERAL") }
    var details by remember { mutableStateOf("") }

    // Permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.showMessage("Notification permission granted! Reminders will ring.")
        } else {
            viewModel.showMessage("Notification permission denied. Visual alerts will still show.")
        }
    }

    if (showVoiceDialog) {
        VoiceInputDialog(
            title = "Voice Reminder",
            promptHint = "e.g. Doctor appointment on Monday or Blood pressure medicine at 8 AM",
            onTextCaptured = { text ->
                title = text
                if (text.contains("medicine", ignoreCase = true) || text.contains("pill", ignoreCase = true)) {
                    type = "MEDICINE"
                } else if (text.contains("doctor", ignoreCase = true) || text.contains("clinic", ignoreCase = true)) {
                    type = "APPOINTMENT"
                } else if (text.contains("study", ignoreCase = true)) {
                    type = "STUDY"
                }
                showAddDialog = true
            },
            onDismiss = { showVoiceDialog = false }
        )
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Schedule Reminder", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Reminder Title *") },
                        placeholder = { Text("e.g. Heart Medicine or Dentist Checkup") },
                        modifier = Modifier.fillMaxWidth().testTag("reminder_title_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Time (e.g. 08:30 AM)") },
                        leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Type selection
                    Text("Reminder Type:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("GENERAL", "MEDICINE", "APPOINTMENT", "STUDY").forEach { t ->
                            val isSel = type == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSel) RemindersOrange.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { type = t }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (t) {
                                        "MEDICINE" -> "💊 Pill"
                                        "APPOINTMENT" -> "🏥 Doc"
                                        "STUDY" -> "📚 Study"
                                        else -> "⏰ Gen"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) RemindersOrange else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        label = { Text("Dosage / Details (Optional)") },
                        placeholder = { Text("e.g. Take 1 tablet with warm water") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            // Check permission on Android 13+
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                            ) {
                                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }

                            viewModel.createReminder(
                                title = title,
                                date = date,
                                time = time,
                                type = type,
                                dosageOrDetails = details,
                                triggerNotificationTest = true
                            )
                            title = ""
                            details = ""
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_reminder_button")
                ) {
                    Text("Save Reminder")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            LifeMateTopBar(
                title = "Timely Reminders",
                userMode = currentUser?.userType,
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FloatingActionButton(
                    onClick = { showVoiceDialog = true },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.testTag("voice_reminder_fab")
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice Reminder")
                }

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = RemindersOrange,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_reminder_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Reminder")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Notification Test Banner (Academic verification)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = RemindersOrange.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = RemindersOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Local Notification Engine",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Verify Android system tray alert:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                            ) {
                                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                NotificationHelper.showReminderNotification(
                                    context = context,
                                    notificationId = (100..999).random(),
                                    title = "LifeMate System Alert",
                                    message = "This confirms Android local notifications are active and working!",
                                    reminderType = "GENERAL"
                                )
                                viewModel.showMessage("Notification dispatched to status bar!")
                            }
                        },
                        modifier = Modifier.testTag("test_notification_button")
                    ) {
                        Text("Test Notification", fontSize = 11.sp)
                    }
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("All (${allReminders.size})") }
                )
                FilterChip(
                    selected = selectedFilter == "MEDICINE",
                    onClick = { selectedFilter = "MEDICINE" },
                    label = { Text("💊 Medicine") }
                )
                FilterChip(
                    selected = selectedFilter == "APPOINTMENT",
                    onClick = { selectedFilter = "APPOINTMENT" },
                    label = { Text("🏥 Doctor") }
                )
                FilterChip(
                    selected = selectedFilter == "STUDY",
                    onClick = { selectedFilter = "STUDY" },
                    label = { Text("📚 Study") }
                )
            }

            val filteredList = if (selectedFilter == "ALL") {
                allReminders
            } else {
                allReminders.filter { it.reminderType.equals(selectedFilter, ignoreCase = true) }
            }

            if (filteredList.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.NotificationsActive,
                    title = "No Reminders Found",
                    message = "Stay punctual for medicines, clinic visits, study blocks, and family events. Tap '+' to add a reminder!",
                    actionLabel = "Add Reminder",
                    onActionClick = { showAddDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { reminder ->
                        ReminderCardItem(
                            reminder = reminder,
                            onToggle = { viewModel.toggleReminder(reminder) },
                            onDelete = { viewModel.deleteReminder(reminder) },
                            onTriggerNow = {
                                NotificationHelper.showReminderNotification(
                                    context = context,
                                    notificationId = reminder.id.toInt(),
                                    title = reminder.title,
                                    message = if (reminder.dosageOrDetails.isNotBlank()) reminder.dosageOrDetails else "Scheduled for ${reminder.date} at ${reminder.time}",
                                    reminderType = reminder.reminderType
                                )
                                viewModel.showMessage("Notification triggered for ${reminder.title}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderCardItem(
    reminder: ReminderItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onTriggerNow: () -> Unit
) {
    val (typeColor, iconVector) = when (reminder.reminderType) {
        "MEDICINE" -> Pair(SeniorGreen, Icons.Default.LocalHospital)
        "APPOINTMENT" -> Pair(SeniorYellow, Icons.Default.EventNote)
        "STUDY" -> Pair(CalendarCyan, Icons.Default.School)
        else -> Pair(RemindersOrange, Icons.Default.NotificationsActive)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.status == "COMPLETED")
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (reminder.status == "COMPLETED") 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(typeColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textDecoration = if (reminder.status == "COMPLETED") TextDecoration.LineThrough else TextDecoration.None,
                    color = if (reminder.status == "COMPLETED") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )

                if (reminder.dosageOrDetails.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reminder.dosageOrDetails,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "📅 ${reminder.date}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "⏰ ${reminder.time}",
                        fontSize = 11.sp,
                        color = RemindersOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row {
                IconButton(onClick = onTriggerNow) {
                    Icon(
                        Icons.Default.NotificationsActive,
                        contentDescription = "Test Notification",
                        tint = RemindersOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Reminder",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
