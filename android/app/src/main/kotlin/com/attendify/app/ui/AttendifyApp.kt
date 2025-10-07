package com.attendify.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.attendify.app.ui.auth.LoginScreen
import com.attendify.app.ui.auth.LoginViewModel
import com.attendify.app.ui.student.StudentDashboardScreen
import com.attendify.app.ui.instructor.InstructorDashboardScreen
import com.attendify.app.ui.admin.AdminDashboardScreen

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
                }
            )
        }
        
        composable(Screen.InstructorDashboard.route) {
            InstructorDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
