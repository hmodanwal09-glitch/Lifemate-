package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ai.AIAssistantScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.SignupScreen
import com.example.ui.screens.auth.SplashScreen
import com.example.ui.screens.auth.UserTypeSelectionScreen
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.child.ChildStudyScreen
import com.example.ui.screens.child.QuizScreen
import com.example.ui.screens.expenses.ExpensesScreen
import com.example.ui.screens.family.FamilyScreen
import com.example.ui.screens.family.SeniorEmergencyScreen
import com.example.ui.screens.family.ShoppingScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notes.NotesScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.reminders.RemindersScreen
import com.example.ui.screens.tasks.TasksScreen
import com.example.viewmodel.AppViewModel

@Composable
fun LifeMateNavGraph(
    viewModel: AppViewModel,
    navController: NavHostController = rememberNavController()
) {
    val currentUser by viewModel.currentUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                isUserLoggedIn = currentUser != null,
                onNavigateNext = { destination ->
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateSignup = {
                    navController.navigate(Screen.Signup.route)
                }
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                viewModel = viewModel,
                onSignupSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.UserTypeSelection.route) {
            UserTypeSelectionScreen(
                viewModel = viewModel,
                onModeSelected = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.UserTypeSelection.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateTasks = { navController.navigate(Screen.Tasks.route) },
                onNavigateNotes = { navController.navigate(Screen.Notes.route) },
                onNavigateReminders = { navController.navigate(Screen.Reminders.route) },
                onNavigateCalendar = { navController.navigate(Screen.Calendar.route) },
                onNavigateExpenses = { navController.navigate(Screen.Expenses.route) },
                onNavigateFamily = { navController.navigate(Screen.Family.route) },
                onNavigateShopping = { navController.navigate(Screen.Shopping.route) },
                onNavigateAI = { navController.navigate(Screen.AIAssistant.route) },
                onNavigateProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateChildStudy = { navController.navigate(Screen.ChildStudy.route) },
                onNavigateQuiz = { navController.navigate(Screen.Quiz.route) },
                onNavigateEmergency = { navController.navigate(Screen.SeniorEmergency.route) }
            )
        }

        composable(Screen.Tasks.route) {
            TasksScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Notes.route) {
            NotesScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Reminders.route) {
            RemindersScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Expenses.route) {
            ExpensesScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Family.route) {
            FamilyScreen(
                viewModel = viewModel,
                onNavigateShopping = { navController.navigate(Screen.Shopping.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Shopping.route) {
            ShoppingScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AIAssistant.route) {
            AIAssistantScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ChildStudy.route) {
            ChildStudyScreen(
                viewModel = viewModel,
                onNavigateQuiz = { navController.navigate(Screen.Quiz.route) },
                onNavigateAI = { navController.navigate(Screen.AIAssistant.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Quiz.route) {
            QuizScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SeniorEmergency.route) {
            SeniorEmergencyScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onNavigateModeSelect = { navController.navigate(Screen.UserTypeSelection.route) },
                onLoggedOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
