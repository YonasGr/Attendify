package com.attendify.app.ui.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendify.app.R
import com.attendify.app.ui.auth.LoginViewModel
import com.attendify.app.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onNavigateToQRScanner: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToUpcomingSessions: () -> Unit
) {
    val authState by loginViewModel.authState.collectAsState()
    val user = (authState as? Resource.Success)?.data
    val enrolledCourses by studentViewModel.enrolledCourses.collectAsState()
    val attendanceRecords by studentViewModel.attendanceRecords.collectAsState()
    val uiState by studentViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Dashboard") },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToQRScanner,
                icon = { Icon(Icons.Default.QrCodeScanner, "Scan QR Code") },
                text = { Text("Scan Code") },
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
                user?.let {
                    Text(
                        text = "Welcome, ${it.firstName ?: "Student"}!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Here is your learning overview.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Book,
                        title = "Courses",
                        value = if (uiState is Resource.Loading) "-" else enrolledCourses.size.toString(),
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        title = "Records",
                        value = if (uiState is Resource.Loading) "-" else attendanceRecords.size.toString(),
                    )
                }
            }

            item { SectionHeader("My Learning") }

            item {
                ManagementCard(
                    icon = Icons.Default.School,
                    title = "My Courses",
                    description = "View your enrolled courses and grades.",
                    onClick = onNavigateToCourses
                )
            }
            item {
                ManagementCard(
                    icon = Icons.Default.CalendarToday,
                    title = "Upcoming Sessions",
                    description = "See all your upcoming classes for the week.",
                    onClick = onNavigateToUpcomingSessions
                )
            }
            item {
                ManagementCard(
                    icon = Icons.Default.History,
                    title = "Attendance History",
                    description = "Review your attendance record for all courses.",
                    onClick = onNavigateToAttendance
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, icon: ImageVector, title: String, value: String) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ManagementCard(icon: ImageVector, title: String, description: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(48.dp)) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
