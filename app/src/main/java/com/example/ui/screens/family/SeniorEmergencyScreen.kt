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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.FamilyMember
import com.example.ui.components.EmptyState
import com.example.ui.components.LifeMateTopBar
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SeniorBlue
import com.example.ui.theme.SeniorRed
import com.example.viewmodel.AppViewModel

@Composable
fun SeniorEmergencyScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val emergencyContacts by viewModel.emergencyContacts.collectAsState()
    val allFamilyMembers by viewModel.familyMembers.collectAsState()

    fun dialNumber(number: String) {
        val cleanNumber = number.replace("[^0-9+]".toRegex(), "")
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$cleanNumber")
        }
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            LifeMateTopBar(
                title = "Emergency Contacts",
                userMode = "SENIOR",
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Giant Call 911 / Medical Emergency Button
            Button(
                onClick = { dialNumber("911") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .testTag("dial_911_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SeniorRed),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "CALL 911 IMMEDIATELY",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Press to open emergency phone dialer",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Text(
                text = "Primary Emergency Contacts",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            val displayList = if (emergencyContacts.isNotEmpty()) emergencyContacts else allFamilyMembers

            if (displayList.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Emergency,
                    title = "No Emergency Contacts",
                    message = "Add contacts in the Family section with 'Emergency Contact' selected for one-tap calling here."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayList, key = { it.id }) { contact ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { dialNumber(contact.phone) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(18.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(SeniorBlue.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (contact.relation.contains("Doctor", ignoreCase = true)) Icons.Default.LocalHospital else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = SeniorBlue,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = contact.name,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${contact.relation} • ${contact.phone}",
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (contact.notes.isNotBlank()) {
                                        Text(
                                            text = contact.notes,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                Button(
                                    onClick = { dialNumber(contact.phone) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SeniorBlue),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Call")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("CALL", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
