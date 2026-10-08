package com.example.ai

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedReminder: AISuggestedReminder? = null
)

class AIRepository(private val aiService: AIService) {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Hello! I am your LifeMate Assistant 🤖. How can I help you, your children, or elders today? Ask me for study plans, concept explanations, reminders, or daily advice!",
                isFromUser = false
            )
        )
    )
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    suspend fun sendMessage(userPrompt: String, userMode: String) {
        val trimmed = userPrompt.trim()
        if (trimmed.isEmpty()) return

        // Append user message
        val userMsg = ChatMessage(text = trimmed, isFromUser = true)
        _messages.value = _messages.value + userMsg

        _isLoading.value = true
        try {
            val response = aiService.askAssistant(trimmed, userMode)
            val botMsg = ChatMessage(
                text = response.answer,
                isFromUser = false,
                suggestedReminder = response.suggestedReminder
            )
            _messages.value = _messages.value + botMsg
        } catch (e: Exception) {
            val errorMsg = ChatMessage(
                text = "Sorry, I had trouble processing that request: ${e.localizedMessage ?: "Unknown error"}. Please try again.",
                isFromUser = false
            )
            _messages.value = _messages.value + errorMsg
        } finally {
            _isLoading.value = false
        }
    }

    fun clearHistory() {
        _messages.value = listOf(
            ChatMessage(
                text = "Chat history refreshed. How can I assist you?",
                isFromUser = false
            )
        )
    }
}
