package com.example.ai

data class AISuggestedReminder(
    val title: String,
    val date: String,
    val time: String,
    val type: String = "GENERAL"
)

data class AIResponse(
    val answer: String,
    val suggestedReminder: AISuggestedReminder? = null
)

interface AIService {
    suspend fun askAssistant(prompt: String, userMode: String): AIResponse
}
