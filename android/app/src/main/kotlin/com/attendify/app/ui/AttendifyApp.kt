package com.attendify.app.ui

// Force recompile by adding a comment.

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.attendify.app.ui.admin.AdminDashboardScreen
import com.attendify.app.ui.admin.CreateUserScreen
import com.attendify.app.ui.admin.EnrollmentManagementScreen
import com.attendify.app.ui.admin.UserManagementScreen
import com.attendify.app.ui.auth.LoginScreen
import com.attendify.app.ui.auth.LoginViewModel
import com.attendify.app.ui.instructor.*
import com.attendify.app.ui.settings.SettingsScreen
import com.attendify.app.ui.student.*
import com.attendify.app.utils.Resource

/**
 * Main composable that sets up navigation and authentication flow
 */
@Composable
fun AttendifyApp() {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val authState by loginViewModel.authState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val state = authState) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                val navController = rememberNavController()
                AttendifyNavHost(navController, Screen.Login.route)
            }
            is Resource.Success -> {
                val user = state.data
                val startDestination = if (user != null) {
                    when (user.role) {
                        "student" -> Screen.StudentDashboard.route
                        "instructor" -> Screen.InstructorDashboard.route
                        "admin" -> Screen.AdminDashboard.route
                        else -> Screen.Login.route
                    }
                } else {
                    Screen.Login.route
                }
                val navController = rememberNavController()
                AttendifyNavHost(navController, startDestination)
            }
        }
    }
}

@Composable
private fun AttendifyNavHost(
    navController: NavHostController,
    startDestination: String
) {
    val loginViewModel: LoginViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen()
        }

        composable(Screen.StudentDashboard.route) {
            StudentDashboardScreen(
                onLogout = { loginViewModel.logout() },
                onNavigateToQRScanner = { navController.navigate(Screen.QRScanner.route) },
                onNavigateToCourses = { navController.navigate(Screen.StudentCourses.route) },
                onNavigateToAttendance = { navController.navigate(Screen.AttendanceHistory.route) },
                onNavigateToUpcomingSessions = { navController.navigate(Screen.UpcomingSessions.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.QRScanner.route) {
            QRCodeScannerScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable(Screen.StudentCourses.route) {
            StudentCoursesScreen(
                onNavigateBack = { navController.navigateUp() },
                onCourseClick = { /* TODO: Navigate to Course Details */ }
            )
        }

        composable(Screen.UpcomingSessions.route) {
            UpcomingSessionsScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable(Screen.AttendanceHistory.route) {
            AttendanceHistoryScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable(Screen.InstructorDashboard.route) {
            InstructorDashboardScreen(
                onLogout = { loginViewModel.logout() },
                onNavigateToCourses = { navController.navigate(Screen.InstructorCourses.route) },
                onNavigateToSessions = { navController.navigate(Screen.Sessions.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.InstructorCourses.route) {
            InstructorCoursesScreen(
                onNavigateBack = { navController.navigateUp() },
                onCourseClick = { /* TODO: Navigate to Course Details */ },
                onCreateCourse = { navController.navigate(Screen.CreateCourse.route) }
            )
        }

        composable(Screen.CreateCourse.route) {
            CreateCourseScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable(Screen.Sessions.route) {
            SessionsScreen(
                onNavigateBack = { navController.navigateUp() },
                onSessionClick = { session ->
                    // In a real app, you would pass the session object or ID to a detail screen
                },
                onCreateSession = { navController.navigate(Screen.CreateSession.route) }
            )
        }

        composable(Screen.CreateSession.route) {
            CreateSessionScreen(
                onNavigateBack = { navController.navigateUp() },
                onSessionCreated = { navController.navigateUp() }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogout = { loginViewModel.logout() },
                onNavigateToUsers = { navController.navigate(Screen.UserManagement.route) },
                onNavigateToCourses = { navController.navigate(Screen.InstructorCourses.route) },
                onNavigateToEnrollments = { navController.navigate(Screen.EnrollmentManagement.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.UserManagement.route) {
            UserManagementScreen(
                onNavigateBack = { navController.navigateUp() },
                onCreateUser = { navController.navigate(Screen.CreateUser.route) }
            )
        }

        composable(Screen.CreateUser.route) {
            CreateUserScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable(Screen.EnrollmentManagement.route) {
            EnrollmentManagementScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.navigateUp() })
        }
    }
}
