package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object UserTypeSelection : Screen("user_type_selection")
    object Home : Screen("home")
    object Tasks : Screen("tasks")
    object Notes : Screen("notes")
    object Reminders : Screen("reminders")
    object Calendar : Screen("calendar")
    object Expenses : Screen("expenses")
    object Family : Screen("family")
    object AIAssistant : Screen("ai_assistant")
    object Profile : Screen("profile")
    object ChildStudy : Screen("child_study")
    object Quiz : Screen("quiz")
    object SeniorEmergency : Screen("senior_emergency")
    object Shopping : Screen("shopping")
}
