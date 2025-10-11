package com.attendify.app.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendify.app.data.model.User
import com.attendify.app.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onCreateUser: () -> Unit
) {
    val users by viewModel.users.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState is Resource.Loading

    var selectedFilter by remember { mutableStateOf("all") }

    val filteredUsers = when (selectedFilter) {
        "students" -> users.filter { it.role == "student" }
        "instructors" -> users.filter { it.role == "instructor" }
        "admins" -> users.filter { it.role == "admin" }
        else -> users
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateUser) {
                Icon(Icons.Default.Add, "Create User")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = selectedFilter == "all", onClick = { selectedFilter = "all" }, label = { Text("All") })
                FilterChip(selected = selectedFilter == "students", onClick = { selectedFilter = "students" }, label = { Text("Students") })
                FilterChip(selected = selectedFilter == "instructors", onClick = { selectedFilter = "instructors" }, label = { Text("Instructors") })
                FilterChip(selected = selectedFilter == "admins", onClick = { selectedFilter = "admins" }, label = { Text("Admins") })
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredUsers) { user ->
                        UserCard(user = user)
                    }
                }
            }
        }
    }
}

@Composable
private fun UserCard(user: User) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                when (user.role) {
                    "student" -> Icons.Default.Person
                    "instructor" -> Icons.Default.School
                    "admin" -> Icons.Default.AdminPanelSettings
                    else -> Icons.Default.Person
                },
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${user.firstName ?: ""} ${user.lastName ?: ""}".trim().ifBlank { user.username },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.email ?: user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { /* TODO: Implement user editing */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit User")
            }
        }
    }
}
