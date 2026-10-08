package com.example.ui.screens.family

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.FamilyMember
import com.example.data.local.entities.TaskItem
import com.example.ui.components.EmptyState
import com.example.ui.components.LifeMateTopBar
import com.example.ui.theme.FamilyPink
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SeniorRed
import com.example.ui.theme.TasksGreen
import com.example.viewmodel.AppViewModel

@Composable
fun FamilyScreen(
    viewModel: AppViewModel,
    onNavigateShopping: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()
    val familyTasks by viewModel.familyTasks.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Members & Contacts, 1: Shared Family Tasks
    var showAddMemberDialog by remember { mutableStateOf(false) }

    // Dialog state
    var memberName by remember { mutableStateOf("") }
    var memberRelation by remember { mutableStateOf("") }
    var memberPhone by remember { mutableStateOf("") }
    var memberEmail by remember { mutableStateOf("") }
    var isEmergencyContact by remember { mutableStateOf(false) }
    var memberNotes by remember { mutableStateOf("") }

    if (showAddMemberDialog) {
        AlertDialog(
            onDismissRequest = { showAddMemberDialog = false },
            title = { Text("Add Family Member", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = memberName,
                        onValueChange = { memberName = it },
                        label = { Text("Name *") },
                        placeholder = { Text("e.g. Grandma Rose") },
                        modifier = Modifier.fillMaxWidth().testTag("family_member_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = memberRelation,
                        onValueChange = { memberRelation = it },
                        label = { Text("Relationship / Role") },
                        placeholder = { Text("e.g. Grandmother, Caregiver, Son") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = memberPhone,
                        onValueChange = { memberPhone = it },
                        label = { Text("Phone Number") },
                        placeholder = { Text("e.g. +1 555-0192") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = memberEmail,
                        onValueChange = { memberEmail = it },
                        label = { Text("Email (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isEmergencyContact,
                            onCheckedChange = { isEmergencyContact = it }
                        )
                        Text("Mark as Emergency Contact", fontSize = 13.sp)
                    }

                    OutlinedTextField(
                        value = memberNotes,
                        onValueChange = { memberNotes = it },
                        label = { Text("Notes (e.g. availability, allergies)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (memberName.isNotBlank()) {
                            viewModel.createFamilyMember(
                                name = memberName,
                                relation = memberRelation.ifBlank { "Family" },
                                phone = memberPhone,
                                email = memberEmail,
                                isEmergency = isEmergencyContact,
                                notes = memberNotes
                            )
                            memberName = ""
                            memberRelation = ""
                            memberPhone = ""
                            memberEmail = ""
                            memberNotes = ""
                            showAddMemberDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_family_member_button")
                ) {
                    Text("Save Contact")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMemberDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            LifeMateTopBar(
                title = "Family Coordination",
                userMode = currentUser?.userType,
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMemberDialog = true },
                containerColor = FamilyPink,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_family_member_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Family Member")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Quick Shortcut Banner to Shared Shopping List
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable(onClick = onNavigateShopping),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryIndigo.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = PrimaryIndigo)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Shared Family Shopping List", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Coordinate grocery runs and supplies together", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("Open →", fontWeight = FontWeight.Bold, color = PrimaryIndigo, fontSize = 13.sp)
                }
            }

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Family Members (${familyMembers.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Shared Tasks (${familyTasks.size})") }
                )
            }

            if (selectedTab == 0) {
                if (familyMembers.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Diversity1,
                        title = "No Family Members Added",
                        message = "Keep family contact numbers, emergency doctors, and caregivers together in one place.",
                        actionLabel = "Add Family Member",
                        onActionClick = { showAddMemberDialog = true }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(familyMembers, key = { it.id }) { member ->
                            FamilyMemberCard(
                                member = member,
                                onCall = {
                                    if (member.phone.isNotBlank()) {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${member.phone.replace("[^0-9+]".toRegex(), "")}")
                                        }
                                        context.startActivity(intent)
                                    } else {
                                        viewModel.showMessage("No phone number saved for ${member.name}")
                                    }
                                },
                                onDelete = { viewModel.deleteFamilyMember(member) }
                            )
                        }
                    }
                }
            } else {
                // Shared Family Tasks
                if (familyTasks.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.CheckCircle,
                        title = "No Shared Family Tasks",
                        message = "When creating tasks in the To-Do section, check 'Share with all family members' to coordinate here."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(familyTasks, key = { it.id }) { task ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TasksGreen)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(task.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        if (task.description.isNotBlank()) {
                                            Text(task.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text("Due: ${task.dueDate} • Shared Task", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                    Checkbox(
                                        checked = task.status == "COMPLETED",
                                        onCheckedChange = { viewModel.toggleTask(task) }
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
private fun FamilyMemberCard(
    member: FamilyMember,
    onCall: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (member.isEmergencyContact) SeniorRed.copy(alpha = 0.15f) else FamilyPink.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (member.isEmergencyContact) Icons.Default.Emergency else Icons.Default.Person,
                    contentDescription = null,
                    tint = if (member.isEmergencyContact) SeniorRed else FamilyPink,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = member.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (member.isEmergencyContact) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(SeniorRed, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("SOS", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = "${member.relation} • ${member.phone}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (member.notes.isNotBlank()) {
                    Text(
                        text = member.notes,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Row {
                IconButton(onClick = onCall) {
                    Icon(
                        Icons.Default.Call,
                        contentDescription = "Call Contact",
                        tint = TasksGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
