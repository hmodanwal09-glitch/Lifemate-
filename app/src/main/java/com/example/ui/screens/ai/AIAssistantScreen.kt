package com.example.ui.screens.ai

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.ChatMessage
import com.example.ui.components.LifeMateTopBar
import com.example.ui.components.VoiceInputDialog
import com.example.ui.theme.AiPurple
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.RemindersOrange
import com.example.viewmodel.AppViewModel

@Composable
fun AIAssistantScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val messages by viewModel.aiMessages.collectAsState()
    val isLoading by viewModel.isAiLoading.collectAsState()

    var inputPrompt by remember { mutableStateOf("") }
    var showVoiceDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val samplePrompts = listOf(
        "Explain photosynthesis in simple language",
        "Create a 7-day Java study plan",
        "Remind me to study Java tomorrow at 7 PM",
        "Give 5 healthy tips for seniors",
        "Create a family dinner meal routine"
    )

    if (showVoiceDialog) {
        VoiceInputDialog(
            title = "Ask AI Assistant",
            promptHint = "Ask questions, study plans, or reminders...",
            onTextCaptured = { text ->
                inputPrompt = text
                viewModel.sendAIMessage(text)
            },
            onDismiss = { showVoiceDialog = false }
        )
    }

    Scaffold(
        topBar = {
            LifeMateTopBar(
                title = "AI Assistant",
                userMode = currentUser?.userType,
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Quick Sample Prompt Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(samplePrompts) { prompt ->
                    SuggestionChip(
                        onClick = {
                            inputPrompt = prompt
                            viewModel.sendAIMessage(prompt)
                        },
                        label = { Text(prompt, fontSize = 12.sp) }
                    )
                }
            }

            // Chat Messages Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        onAddReminder = { suggested ->
                            viewModel.addReminderFromAI(suggested)
                        }
                    )
                }

                if (isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = AiPurple
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "LifeMate AI is thinking...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Input Bar with Voice & Send
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showVoiceDialog = true },
                        modifier = Modifier.testTag("ai_mic_button")
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Voice Dictation",
                            tint = AiPurple
                        )
                    }

                    OutlinedTextField(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        placeholder = { Text("Ask anything or dictate a reminder...") },
                        maxLines = 4,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_prompt_input"),
                        shape = RoundedCornerShape(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputPrompt.isNotBlank() && !isLoading) {
                                val text = inputPrompt.trim()
                                inputPrompt = ""
                                viewModel.sendAIMessage(text)
                            }
                        },
                        enabled = inputPrompt.isNotBlank() && !isLoading,
                        modifier = Modifier.testTag("ai_send_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputPrompt.isNotBlank() && !isLoading) AiPurple else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessage,
    onAddReminder: (com.example.ai.AISuggestedReminder) -> Unit
) {
    val isUser = message.isFromUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(AiPurple, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (isUser) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(14.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            // If AI detected a reminder command, render 1-Tap "Add to Reminders" button!
            if (message.suggestedReminder != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onAddReminder(message.suggestedReminder) },
                    colors = ButtonDefaults.buttonColors(containerColor = RemindersOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("ai_add_reminder_button")
                ) {
                    Icon(
                        Icons.Default.AddAlert,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add to Reminders (${message.suggestedReminder.date} at ${message.suggestedReminder.time})",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
