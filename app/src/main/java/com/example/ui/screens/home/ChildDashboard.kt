package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.StudyPlanItem
import com.example.data.local.entities.TaskItem
import com.example.data.local.entities.User
import com.example.ui.theme.AiPurple
import com.example.ui.theme.FamilyPink
import com.example.ui.theme.NotesViolet
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.TasksGreen

@Composable
fun ChildDashboard(
    user: User,
    pendingHomework: List<TaskItem>,
    studyPlans: List<StudyPlanItem>,
    onNavigateTasks: () -> Unit,
    onNavigateStudy: () -> Unit,
    onNavigateQuiz: () -> Unit,
    onNavigateAI: () -> Unit,
    onNavigateReminders: () -> Unit,
    onToggleTask: (TaskItem) -> Unit
) {
    val completedPlansCount = studyPlans.count { it.isCompleted }
    val totalPlansCount = studyPlans.size
    val progress = if (totalPlansCount > 0) completedPlansCount.toFloat() / totalPlansCount else 0.5f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = FamilyPink.copy(alpha = 0.12f)
            )
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(FamilyPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Hi, ${user.name}! 🌟",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ready to learn and have fun today?",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Study Progress Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateStudy),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📊 Today's Learning Progress",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "$completedPlansCount / $totalPlansCount Goals",
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryIndigo,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = PrimaryIndigo,
                    trackColor = PrimaryIndigo.copy(alpha = 0.15f)
                )
            }
        }

        // 4 Grid Feature Cards for Child
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ChildFeatureTile(
                title = "Study Planner",
                subtitle = "Day plans & subjects",
                icon = Icons.Default.MenuBook,
                color = NotesViolet,
                modifier = Modifier
                    .weight(1f)
                    .testTag("child_study_planner_tile"),
                onClick = onNavigateStudy
            )
            ChildFeatureTile(
                title = "Fun Quizzes",
                subtitle = "Practice & earn stars",
                icon = Icons.Default.Quiz,
                color = TasksGreen,
                modifier = Modifier
                    .weight(1f)
                    .testTag("child_quiz_tile"),
                onClick = onNavigateQuiz
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ChildFeatureTile(
                title = "Homework List",
                subtitle = "${pendingHomework.size} assignments",
                icon = Icons.Default.Task,
                color = FamilyPink,
                modifier = Modifier
                    .weight(1f)
                    .testTag("child_homework_tile"),
                onClick = onNavigateTasks
            )
            ChildFeatureTile(
                title = "AI Study Tutor",
                subtitle = "Ask tough questions",
                icon = Icons.Default.AutoAwesome,
                color = AiPurple,
                modifier = Modifier
                    .weight(1f)
                    .testTag("child_ai_tutor_tile"),
                onClick = onNavigateAI
            )
        }

        // Active Homework Snippet
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📝 Pending Homework",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "View All",
                        color = PrimaryIndigo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onNavigateTasks)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (pendingHomework.isEmpty()) {
                    Text(
                        text = "🎉 Awesome job! All your homework is completed!",
                        fontSize = 13.sp,
                        color = TasksGreen,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    pendingHomework.take(3).forEach { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Complete task",
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { onToggleTask(task) }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                if (task.dueDate.isNotBlank()) {
                                    Text(
                                        text = "Due: ${task.dueDate} • ${task.category}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
private fun ChildFeatureTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(color, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
