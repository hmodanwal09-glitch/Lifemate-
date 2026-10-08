package com.example.ui.screens.child

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.StudyPlanItem
import com.example.ui.components.EmptyState
import com.example.ui.components.LifeMateTopBar
import com.example.ui.theme.AiPurple
import com.example.ui.theme.FamilyPink
import com.example.ui.theme.NotesViolet
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.TasksGreen
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChildStudyScreen(
    viewModel: AppViewModel,
    onNavigateQuiz: () -> Unit,
    onNavigateAI: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val studyPlans by viewModel.studyPlans.collectAsState()

    var showAddPlanDialog by remember { mutableStateOf(false) }

    // Dialog state
    var subject by remember { mutableStateOf("Mathematics") }
    var topic by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var durationMinutes by remember { mutableIntStateOf(45) }
    var notes by remember { mutableStateOf("") }

    // Interactive Pomodoro Timer (25 mins)
    var isTimerRunning by remember { mutableStateOf(false) }
    var timeLeftSeconds by remember { mutableIntStateOf(25 * 60) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timeLeftSeconds > 0) {
            delay(1000)
            timeLeftSeconds--
            if (timeLeftSeconds == 0) {
                isTimerRunning = false
                viewModel.showMessage("🎉 25-minute Pomodoro study sprint completed! Great job!")
            }
        }
    }

    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val formattedTimer = String.format("%02d:%02d", minutes, seconds)

    if (showAddPlanDialog) {
        AlertDialog(
            onDismissRequest = { showAddPlanDialog = false },
            title = { Text("Add Study Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject *") },
                        placeholder = { Text("e.g. Science, Math, History") },
                        modifier = Modifier.fillMaxWidth().testTag("study_subject_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topic / Chapter *") },
                        placeholder = { Text("e.g. Photosynthesis or Fractions") },
                        modifier = Modifier.fillMaxWidth().testTag("study_topic_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = targetDate,
                        onValueChange = { targetDate = it },
                        label = { Text("Target Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (Page numbers, practice tests)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (subject.isNotBlank() && topic.isNotBlank()) {
                            viewModel.createStudyPlan(
                                subject = subject,
                                topic = topic,
                                date = targetDate,
                                durationMinutes = durationMinutes,
                                notes = notes
                            )
                            topic = ""
                            notes = ""
                            showAddPlanDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_study_plan_button")
                ) {
                    Text("Save Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPlanDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            LifeMateTopBar(
                title = "Study Planner",
                userMode = "CHILD",
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddPlanDialog = true },
                containerColor = NotesViolet,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_study_plan_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Study Goal")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Study Sprint (Pomodoro) Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryIndigo.copy(alpha = 0.12f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = PrimaryIndigo)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Focus Study Sprint", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formattedTimer,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryIndigo
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { isTimerRunning = !isTimerRunning },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) Color(0xFFEF4444) else TasksGreen
                            )
                        ) {
                            Icon(if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isTimerRunning) "Pause" else "Start")
                        }

                        Button(
                            onClick = {
                                isTimerRunning = false
                                timeLeftSeconds = 25 * 60
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text("Reset", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Quick Links: Practice Quizzes & AI Tutor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onNavigateQuiz),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = TasksGreen.copy(alpha = 0.12f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Quiz, contentDescription = null, tint = TasksGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Take Quiz", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Practice test", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onNavigateAI),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AiPurple.copy(alpha = 0.12f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AiPurple)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("AI Tutor", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Ask questions", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Study Plans List
            Text(
                text = "Study Timetable & Topics",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (studyPlans.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.MenuBook,
                    title = "No Study Goals Scheduled",
                    message = "Add your subject topics, revision notes, and exam dates to create your study roadmap!",
                    actionLabel = "Add Study Goal",
                    onActionClick = { showAddPlanDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(studyPlans, key = { it.id }) { plan ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleStudyPlan(plan) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (plan.isCompleted)
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else
                                    MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(if (plan.isCompleted) 0.dp else 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = plan.isCompleted,
                                    onCheckedChange = { viewModel.toggleStudyPlan(plan) }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${plan.subject}: ${plan.topic}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        textDecoration = if (plan.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                        color = if (plan.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (plan.notes.isNotBlank()) {
                                        Text(plan.notes, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(
                                        text = "Target: ${plan.targetDate} • ${plan.durationMinutes} mins",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteStudyPlan(plan) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
