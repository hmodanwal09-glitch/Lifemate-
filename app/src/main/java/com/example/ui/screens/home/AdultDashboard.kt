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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ReminderItem
import com.example.data.local.entities.ShoppingItem
import com.example.data.local.entities.TaskItem
import com.example.data.local.entities.User
import com.example.ui.theme.AiPurple
import com.example.ui.theme.CalendarCyan
import com.example.ui.theme.ExpensesAmber
import com.example.ui.theme.FamilyPink
import com.example.ui.theme.NotesViolet
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.RemindersOrange
import com.example.ui.theme.TasksGreen

@Composable
fun AdultDashboard(
    user: User,
    pendingTasks: List<TaskItem>,
    pendingReminders: List<ReminderItem>,
    shoppingItems: List<ShoppingItem>,
    totalExpenses: Double,
    onNavigateTasks: () -> Unit,
    onNavigateNotes: () -> Unit,
    onNavigateReminders: () -> Unit,
    onNavigateCalendar: () -> Unit,
    onNavigateExpenses: () -> Unit,
    onNavigateFamily: () -> Unit,
    onNavigateShopping: () -> Unit,
    onNavigateAI: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Assistant Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateAI)
                .testTag("adult_ai_assistant_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AiPurple.copy(alpha = 0.12f)),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(AiPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LifeMate AI Assistant",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Plan your week, set smart reminders & summarize notes",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Overview Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdultMetricCard(
                title = "Pending Tasks",
                value = "${pendingTasks.size}",
                subtext = "items to do",
                icon = Icons.Default.FormatListNumbered,
                color = TasksGreen,
                modifier = Modifier
                    .weight(1f)
                    .testTag("adult_metric_tasks"),
                onClick = onNavigateTasks
            )

            AdultMetricCard(
                title = "Total Expenses",
                value = "$${String.format("%.2f", totalExpenses)}",
                subtext = "tracked this month",
                icon = Icons.Default.AttachMoney,
                color = ExpensesAmber,
                modifier = Modifier
                    .weight(1f)
                    .testTag("adult_metric_expenses"),
                onClick = onNavigateExpenses
            )
        }

        // Main Feature Cards (2x3 Grid)
        Text(
            text = "Everyday Tools",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdultToolTile(
                title = "Tasks",
                subtitle = "To-Do list & priorities",
                icon = Icons.Default.CheckCircle,
                color = TasksGreen,
                modifier = Modifier.weight(1f),
                onClick = onNavigateTasks
            )
            AdultToolTile(
                title = "Reminders",
                subtitle = "${pendingReminders.size} active alerts",
                icon = Icons.Default.NotificationsActive,
                color = RemindersOrange,
                modifier = Modifier.weight(1f),
                onClick = onNavigateReminders
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdultToolTile(
                title = "Family Hub",
                subtitle = "Shared contacts & tasks",
                icon = Icons.Default.Diversity1,
                color = FamilyPink,
                modifier = Modifier.weight(1f),
                onClick = onNavigateFamily
            )
            AdultToolTile(
                title = "Notes",
                subtitle = "Ideas & checklists",
                icon = Icons.Default.NoteAlt,
                color = NotesViolet,
                modifier = Modifier.weight(1f),
                onClick = onNavigateNotes
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdultToolTile(
                title = "Calendar",
                subtitle = "Events & appointments",
                icon = Icons.Default.CalendarMonth,
                color = CalendarCyan,
                modifier = Modifier.weight(1f),
                onClick = onNavigateCalendar
            )
            AdultToolTile(
                title = "Shopping List",
                subtitle = "${shoppingItems.count { !it.isPurchased }} items needed",
                icon = Icons.Default.ShoppingCart,
                color = ExpensesAmber,
                modifier = Modifier.weight(1f),
                onClick = onNavigateShopping
            )
        }
    }
}

@Composable
private fun AdultMetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtext,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AdultToolTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(98.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
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
