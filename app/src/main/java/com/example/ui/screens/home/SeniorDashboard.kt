package com.example.ui.screens.home

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.FamilyMember
import com.example.data.local.entities.ReminderItem
import com.example.data.local.entities.User
import com.example.ui.components.VoiceInputDialog
import com.example.ui.theme.AiPurple
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SeniorBlue
import com.example.ui.theme.SeniorGreen
import com.example.ui.theme.SeniorRed
import com.example.ui.theme.SeniorYellow

@Composable
fun SeniorDashboard(
    user: User,
    medicineReminders: List<ReminderItem>,
    appointmentReminders: List<ReminderItem>,
    emergencyContacts: List<FamilyMember>,
    familyContacts: List<FamilyMember>,
    onNavigateReminders: () -> Unit,
    onNavigateEmergency: () -> Unit,
    onNavigateFamily: () -> Unit,
    onNavigateAI: () -> Unit,
    onToggleReminder: (ReminderItem) -> Unit,
    onVoiceQuery: (String) -> Unit
) {
    val context = LocalContext.current
    var showVoiceDialog by remember { mutableStateOf(false) }

    if (showVoiceDialog) {
        VoiceInputDialog(
            title = "Voice Assistant",
            promptHint = "Ask for medicine help, reminders, or family assistance...",
            onTextCaptured = { text ->
                onVoiceQuery(text)
                onNavigateAI()
            },
            onDismiss = { showVoiceDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Red Emergency Alert Button (High Priority & Very Large)
        Button(
            onClick = onNavigateEmergency,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .testTag("senior_emergency_sos_button"),
            colors = ButtonDefaults.buttonColors(containerColor = SeniorRed),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Emergency SOS",
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "EMERGENCY SOS & CONTACTS",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }

        // Prominent Voice Button for Seniors
        Button(
            onClick = { showVoiceDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .testTag("senior_voice_assistant_button"),
            colors = ButtonDefaults.buttonColors(containerColor = SeniorBlue),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Assistant",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "TAP TO SPEAK (VOICE HELPER)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Today's Medicines Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateReminders),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SeniorGreen.copy(alpha = 0.12f)),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = SeniorGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Medicine Reminders",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (medicineReminders.isEmpty()) {
                    Text(
                        text = "No pending medicine reminders for today! Everything is on track.",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    medicineReminders.take(3).forEach { med ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (med.status == "COMPLETED") Icons.Default.CheckCircle else Icons.Default.Alarm,
                                    contentDescription = "Toggle status",
                                    tint = if (med.status == "COMPLETED") SeniorGreen else SeniorYellow,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clickable { onToggleReminder(med) }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = med.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Time: ${med.time}" + if (med.dosageOrDetails.isNotBlank()) " • ${med.dosageOrDetails}" else "",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Two Large Action Tiles: Family Calling & Doctor Visits
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SeniorTile(
                title = "Family Call",
                subtitle = "${familyContacts.size} contacts",
                icon = Icons.Default.Call,
                backgroundColor = PrimaryIndigo,
                modifier = Modifier
                    .weight(1f)
                    .testTag("senior_family_contacts_tile"),
                onClick = onNavigateFamily
            )

            SeniorTile(
                title = "Doctor Visits",
                subtitle = "${appointmentReminders.size} scheduled",
                icon = Icons.Default.EventNote,
                backgroundColor = SeniorYellow,
                modifier = Modifier
                    .weight(1f)
                    .testTag("senior_appointments_tile"),
                onClick = onNavigateReminders
            )
        }
    }
}

@Composable
private fun SeniorTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(backgroundColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
