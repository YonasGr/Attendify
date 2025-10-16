package com.attendify.app.ui.instructor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendify.app.ui.auth.LoginViewModel
import com.attendify.app.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorDashboardScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    instructorViewModel: InstructorViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToSessions: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onMenuClick: () -> Unit
) {
    val authState by loginViewModel.authState.collectAsState()
    val user = (authState as? Resource.Success)?.data
    val courses by instructorViewModel.courses.collectAsState()
    val sessions by instructorViewModel.sessions.collectAsState()
    val uiState by instructorViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Instructor Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCourses,
                icon = { Icon(Icons.Default.Add, "Add Course") },
                text = { Text("New Course") },
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        user?.let {
                            Text(
                                text = "Welcome, ${it.firstName ?: "Instructor"}!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ready to manage your courses?",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.School,
                        title = "Active Courses",
                        value = if (uiState is Resource.Loading) "-" else courses.size.toString(),
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Group,
                        title = "Total Students",
                        value = if (uiState is Resource.Loading) "-" else sessions.sumOf { it.attendees.size }.toString(),
                    )
                }
            }

            item { SectionHeader("Management") }

            item {
                ManagementCard(
                    icon = Icons.Default.School,
                    title = "My Courses",
                    description = "Manage your courses and view student enrollment.",
                    onClick = onNavigateToCourses
                )
            }
            item {
                ManagementCard(
                    icon = Icons.Default.Event,
                    title = "Sessions",
                    description = "Create and manage attendance sessions for your courses.",
                    onClick = onNavigateToSessions
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
