package com.attendify.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.attendify.app.ui.auth.LoginScreen
import com.attendify.app.ui.auth.LoginViewModel
import com.attendify.app.ui.student.StudentDashboardScreen
import com.attendify.app.ui.student.QRCodeScannerScreen
import com.attendify.app.ui.student.StudentCoursesScreen
import com.attendify.app.ui.student.AttendanceHistoryScreen
import com.attendify.app.ui.instructor.InstructorDashboardScreen
import com.attendify.app.ui.instructor.InstructorCoursesScreen
import com.attendify.app.ui.instructor.CreateCourseScreen
import com.attendify.app.ui.instructor.SessionsScreen
import com.attendify.app.ui.instructor.QRCodeDisplayScreen
import com.attendify.app.ui.admin.AdminDashboardScreen
import com.attendify.app.ui.admin.UserManagementScreen
import com.attendify.app.ui.admin.CreateUserScreen

/**
 * Main composable that sets up navigation and authentication flow
 */
@Composable
fun AttendifyApp() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val authState by loginViewModel.authState.collectAsState()
    
    // Observe authentication state and navigate accordingly
    LaunchedEffect(authState) {
        when {
            authState.isAuthenticated && authState.user != null -> {
                val startDestination = when (authState.user?.role) {
                    "student" -> Screen.StudentDashboard.route
                    "instructor" -> Screen.InstructorDashboard.route
                    "admin" -> Screen.AdminDashboard.route
                    else -> Screen.Login.route
                }
                navController.navigate(startDestination) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            !authState.isLoading && !authState.isAuthenticated -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = if (authState.isAuthenticated) {
            when (authState.user?.role) {
                "student" -> Screen.StudentDashboard.route
                "instructor" -> Screen.InstructorDashboard.route
                "admin" -> Screen.AdminDashboard.route
                else -> Screen.Login.route
            }
        } else {
            Screen.Login.route
        }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { user ->
                    val destination = when (user.role) {
                        "student" -> Screen.StudentDashboard.route
                        "instructor" -> Screen.InstructorDashboard.route
                        "admin" -> Screen.AdminDashboard.route
                        else -> Screen.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.StudentDashboard.route) {
            StudentDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToQRScanner = {
                    navController.navigate(Screen.QRScanner.route)
                },
                onNavigateToCourses = {
                    navController.navigate(Screen.StudentCourses.route)
                },
                onNavigateToAttendance = {
                    navController.navigate(Screen.AttendanceHistory.route)
                }
            )
        }
        
        composable(Screen.QRScanner.route) {
            QRCodeScannerScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.StudentCourses.route) {
            StudentCoursesScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onCourseClick = { courseId ->
                    navController.navigate(Screen.CourseDetails.createRoute(courseId))
                }
            )
        }
        
        composable(Screen.AttendanceHistory.route) {
            AttendanceHistoryScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.InstructorDashboard.route) {
            InstructorDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToCourses = {
                    navController.navigate(Screen.InstructorCourses.route)
                },
                onNavigateToSessions = {
                    navController.navigate(Screen.Sessions.route)
                }
            )
        }
        
        composable(Screen.InstructorCourses.route) {
            InstructorCoursesScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onCourseClick = { courseId ->
                    navController.navigate(Screen.CourseDetails.createRoute(courseId))
                },
                onCreateCourse = {
                    navController.navigate(Screen.CreateCourse.route)
                }
            )
        }
        
        composable(Screen.CreateCourse.route) {
            CreateCourseScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Sessions.route) {
            SessionsScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onSessionClick = { session ->
                    navController.navigate(
                        Screen.QRCodeDisplay.createRoute(
                            session.id,
                            session.title,
                            session.qrCode
                        )
                    )
                },
                onCreateSession = {
                    // TODO: Create session screen
                }
            )
        }
        
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToUsers = {
                    navController.navigate(Screen.UserManagement.route)
                },
                onNavigateToCourses = {
                    navController.navigate(Screen.InstructorCourses.route)
                },
                onNavigateToEnrollments = {
                    // TODO: Enrollment management screen
                }
            )
        }
        
        composable(Screen.UserManagement.route) {
            UserManagementScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onCreateUser = {
                    navController.navigate(Screen.CreateUser.route)
                }
            )
        }
        
        composable(Screen.CreateUser.route) {
            CreateUserScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(
            route = Screen.QRCodeDisplay.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("sessionTitle") { type = NavType.StringType },
                navArgument("qrCode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            val sessionTitle = backStackEntry.arguments?.getString("sessionTitle") ?: ""
            val qrCode = backStackEntry.arguments?.getString("qrCode") ?: ""
            
            QRCodeDisplayScreen(
                sessionId = sessionId,
                sessionTitle = sessionTitle,
                qrCode = qrCode,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}
