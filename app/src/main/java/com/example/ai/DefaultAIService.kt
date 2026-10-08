package com.example.ai

import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Replaceable AI implementation for LifeMate.
 * Capable of responding to educational queries, generating day-wise study plans,
 * parsing reminder commands, and providing family assistant guidance.
 */
class DefaultAIService : AIService {

    override suspend fun askAssistant(prompt: String, userMode: String): AIResponse {
        // Simulate thoughtful AI reasoning latency
        delay(600)

        val cleanPrompt = prompt.trim()
        val lower = cleanPrompt.lowercase(Locale.ROOT)

        // 1. Check for reminder-creation commands
        if (lower.startsWith("remind me") || lower.contains("remind me to") || lower.contains("set a reminder")) {
            return handleReminderCommand(cleanPrompt)
        }

        // 2. Check for study plan requests
        if (lower.contains("study plan") || lower.contains("study schedule") || (lower.contains("day") && lower.contains("plan"))) {
            return handleStudyPlanRequest(cleanPrompt, userMode)
        }

        // 3. Check for specific school/educational concepts
        if (lower.contains("photosynthesis")) {
            return AIResponse(
                answer = """
                    🌿 **Photosynthesis Explained Simply!**
                    
                    Think of a plant leaf as a tiny solar-powered kitchen:
                    
                    1. **Ingredients:**
                       - **Sunlight** from the sky ☀️
                       - **Water (H₂O)** sucked up by roots from the soil 💧
                       - **Carbon Dioxide (CO₂)** breathed in from the air 🍃
                       
                    2. **The Cooking Process:**
                       Using green chlorophyll like a mini solar panel, the plant converts sunlight + water + carbon dioxide into **Glucose** (plant food/sugar).
                       
                    3. **The Bonus for Us:**
                       The plant breathes out fresh **Oxygen (O₂)** that humans and animals breathe to stay alive!
                    
                    Formula: Sunlight + 6CO₂ + 6H₂O ➔ Glucose + 6O₂ ✨
                """.trimIndent()
            )
        }

        if (lower.contains("gravity") || lower.contains("gravitational")) {
            return AIResponse(
                answer = """
                    🌍 **What is Gravity?**
                    
                    Gravity is an invisible pulling force that pulls objects toward each other.
                    
                    • **Why we stay on Earth:** Earth is huge and has a lot of mass, so its gravity keeps our feet firmly on the ground instead of floating into space!
                    • **Fun Fact:** The Moon is smaller than Earth, so if you jumped on the Moon, you could jump 6 times higher!
                """.trimIndent()
            )
        }

        if (lower.contains("java") || lower.contains("kotlin") || lower.contains("coding")) {
            return AIResponse(
                answer = """
                    💻 **Programming & Software Tips**
                    
                    • **Kotlin:** Modern, concise language for Android. Features null-safety, coroutines, and clean syntax!
                    • **Java:** Robust object-oriented foundation running on JVM across millions of enterprise systems.
                    • **Best Practice:** Practice coding small functions daily, use meaningful variable names, and break big problems into bite-sized logic.
                """.trimIndent()
            )
        }

        // 4. Mode-specific tailored guidance
        when (userMode.uppercase(Locale.ROOT)) {
            "CHILD" -> {
                if (lower.contains("homework") || lower.contains("math") || lower.contains("science") || lower.contains("history")) {
                    return AIResponse(
                        answer = """
                            🎒 **Study Buddy Helper**
                            
                            Here is a great technique to tackle this:
                            1. Break down the question into 3 main parts.
                            2. Write down what you already know first.
                            3. Use colorful diagrams or flashcards for tricky terms.
                            4. Take a 5-minute stretch break every 25 minutes (Pomodoro technique)!
                            
                            Would you like me to quiz you on this topic or set a study reminder?
                        """.trimIndent()
                    )
                }
                return AIResponse(
                    answer = """
                        ⭐ **Hi Leo! I'm your LifeMate Study Assistant!**
                        
                        I can help you:
                        • Understand tough homework questions in simple words
                        • Create a step-by-step study timetable
                        • Explain science experiments and math tricks
                        • Set fun reminders for your tests!
                        
                        Try asking: "Explain photosynthesis in simple language" or "Create a 5-day math review plan"!
                    """.trimIndent()
                )
            }

            "SENIOR" -> {
                if (lower.contains("medicine") || lower.contains("pill") || lower.contains("health") || lower.contains("doctor")) {
                    return AIResponse(
                        answer = """
                            💊 **Health & Medication Guidance**
                            
                            • Always take prescription tablets with a full glass of water.
                            • Keep all medicines in their original labeled boxes or daily pill organizers.
                            • Write down any unusual symptoms to show Dr. Davis at your next appointment.
                            • If you feel dizzy or unwell, press the RED Emergency Contact button on your home screen right away.
                        """.trimIndent()
                    )
                }
                return AIResponse(
                    answer = """
                        🌟 **Hello! I'm your LifeMate Senior Care Assistant.**
                        
                        I am here to help keep your day peaceful and organized:
                        • Medicine timing and dosage checks
                        • Doctor appointment reminders
                        • Easy voice dictation for family messages
                        • Tips for healthy walking, hydration, and gentle stretches.
                        
                        Speak or type any question anytime!
                    """.trimIndent()
                )
            }

            else -> { // ADULT MODE
                if (lower.contains("budget") || lower.contains("expense") || lower.contains("money")) {
                    return AIResponse(
                        answer = """
                            📊 **Family Budget & Expense Tip**
                            
                            • Use the 50/30/20 principle: 50% Essentials (Groceries, Utilities), 30% Family Lifestyle/Recreation, 20% Emergency Savings & Investments.
                            • LifeMate's Expense tracker categorizes your spending automatically. Check the category pie breakdown regularly to catch leakage!
                        """.trimIndent()
                    )
                }

                if (lower.contains("routine") || lower.contains("daily plan") || lower.contains("morning")) {
                    return AIResponse(
                        answer = """
                            ☀️ **Optimized Family Daily Routine**
                            
                            • 07:00 AM - Morning stretch, hydration & breakfast
                            • 08:30 AM - Review high-priority tasks in LifeMate
                            • 12:30 PM - Healthy lunch & quick family check-in
                            • 05:30 PM - Wind down workday & review family grocery list
                            • 07:30 PM - Family dinner & conversation
                            • 10:00 PM - Digital wind-down and restful sleep
                        """.trimIndent()
                    )
                }

                return AIResponse(
                    answer = """
                        ✨ **LifeMate AI Family Assistant**
                        
                        I can help you streamline family life:
                        • Organize to-do items and coordinate family events
                        • Set smart reminders: *"Remind me to call the pediatrician tomorrow at 3 PM"*
                        • Draft meal plans, grocery checklists, and study roadmaps
                        • Summarize complex notes or articles into quick bullet points.
                        
                        How can I assist your family today?
                    """.trimIndent()
                )
            }
        }
    }

    private fun handleReminderCommand(prompt: String): AIResponse {
        val lower = prompt.lowercase(Locale.ROOT)
        var title = "Scheduled reminder"
        val calendar = Calendar.getInstance()

        // Extract title
        val reminderPrefixes = listOf("remind me to", "remind me", "set a reminder to", "set a reminder for")
        for (prefix in reminderPrefixes) {
            val idx = lower.indexOf(prefix)
            if (idx != -1) {
                var extracted = prompt.substring(idx + prefix.length).trim()
                // Strip trailing time words if present
                val timeWords = listOf("tomorrow at", "tomorrow", "today at", "at 7 pm", "at 8 am", "at 9 am", "at 5 pm", "next monday")
                var cleanTitle = extracted
                for (tw in timeWords) {
                    val twIdx = cleanTitle.lowercase(Locale.ROOT).indexOf(tw)
                    if (twIdx != -1) {
                        cleanTitle = cleanTitle.substring(0, twIdx).trim()
                    }
                }
                if (cleanTitle.isNotEmpty()) {
                    title = cleanTitle.replaceFirstChar { it.uppercase() }
                }
                break
            }
        }

        // Determine date
        if (lower.contains("tomorrow")) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        } else if (lower.contains("next week")) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)

        // Determine time
        var formattedTime = "07:00 PM"
        when {
            lower.contains("7 pm") || lower.contains("7pm") || lower.contains("19:00") -> formattedTime = "07:00 PM"
            lower.contains("8 am") || lower.contains("8am") -> formattedTime = "08:00 AM"
            lower.contains("9 am") || lower.contains("9am") -> formattedTime = "09:00 AM"
            lower.contains("10 am") || lower.contains("10am") -> formattedTime = "10:00 AM"
            lower.contains("12 pm") || lower.contains("noon") -> formattedTime = "12:00 PM"
            lower.contains("3 pm") || lower.contains("3pm") -> formattedTime = "03:00 PM"
            lower.contains("5 pm") || lower.contains("5pm") -> formattedTime = "05:00 PM"
            lower.contains("8 pm") || lower.contains("8pm") -> formattedTime = "08:00 PM"
        }

        val type = if (lower.contains("medicine") || lower.contains("pill") || lower.contains("tablet")) "MEDICINE"
        else if (lower.contains("study") || lower.contains("homework") || lower.contains("exam")) "STUDY"
        else if (lower.contains("doctor") || lower.contains("dentist") || lower.contains("clinic")) "APPOINTMENT"
        else "GENERAL"

        val suggested = AISuggestedReminder(
            title = title,
            date = formattedDate,
            time = formattedTime,
            type = type
        )

        return AIResponse(
            answer = "⏰ I've prepared a reminder for you:\n\n📌 **$title**\n📅 Date: $formattedDate\n⏰ Time: $formattedTime\n🏷️ Type: $type\n\nTap **'Add to Reminders'** below to save it directly to your schedule!",
            suggestedReminder = suggested
        )
    }

    private fun handleStudyPlanRequest(prompt: String, userMode: String): AIResponse {
        val lower = prompt.lowercase(Locale.ROOT)
        val subject = when {
            lower.contains("java") -> "Java Programming"
            lower.contains("kotlin") -> "Kotlin & Android"
            lower.contains("math") -> "Mathematics"
            lower.contains("science") -> "Science"
            lower.contains("history") -> "History"
            else -> "Study Plan"
        }

        return AIResponse(
            answer = """
                📚 **Comprehensive 7-Day $subject Master Plan**
                
                • **Day 1: Fundamentals & Core Architecture**
                  Overview, syntax, variables, and setting up the workspace (45 mins).
                  
                • **Day 2: Control Flow & Logic**
                  Conditionals, loops, branches, and writing first test algorithms (45 mins).
                  
                • **Day 3: Data Structures & Collections**
                  Lists, arrays, maps, and iteration techniques (50 mins).
                  
                • **Day 4: Functions & Modularity**
                  Writing clean reusable methods, parameter passing, and scope (45 mins).
                  
                • **Day 5: Hands-on Mini Project**
                  Build a working command-line or interactive calculator (60 mins).
                  
                • **Day 6: Debugging & Edge Cases**
                  Exception handling, clean error messages, and optimization (40 mins).
                  
                • **Day 7: Final Comprehensive Review & Quiz**
                  Review summaries and complete the LifeMate practice quiz!
                  
                💡 *Tip: Study in focused 25-minute sprints with short stretch breaks.*
            """.trimIndent()
        )
    }
}
