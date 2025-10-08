package com.attendify.app.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendify.app.ui.auth.LoginViewModel

/**
 * Student Dashboard Screen
 * Shows enrolled courses, QR scanner access, and attendance history
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val authState by loginViewModel.authState.collectAsState()
    val user = authState.user
    
    val enrolledCourses by studentViewModel.enrolledCourses.collectAsState()
    val attendanceRecords by studentViewModel.attendanceRecords.collectAsState()
    val isLoading by studentViewModel.isLoading.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Student Dashboard")
                        user?.let {
                            Text(
                                text = "${it.firstName} ${it.lastName}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        loginViewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Navigate to QR Scanner */ },
                icon = { Icon(Icons.Default.QrCodeScanner, "Scan QR") },
                text = { Text("Scan QR Code") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "My Courses",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Book,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Enrolled Courses",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isLoading) "Loading..." else "${enrolledCourses.size} courses",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Attendance Records",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isLoading) "Loading..." else "${attendanceRecords.size} records",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Features",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                FeatureCard(
                    icon = Icons.Default.QrCodeScanner,
                    title = "QR Code Scanning",
                    description = "Scan QR codes to mark attendance in sessions"
                )
            }
            
            item {
                FeatureCard(
                    icon = Icons.Default.History,
                    title = "Attendance History",
                    description = "View your attendance records for all courses"
                )
            }
            
            item {
                FeatureCard(
                    icon = Icons.Default.Schedule,
                    title = "Session Schedule",
                    description = "See upcoming sessions for your courses"
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
