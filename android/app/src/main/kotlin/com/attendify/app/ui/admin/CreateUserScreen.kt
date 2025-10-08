package com.attendify.app.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Create User Screen for Admins
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("student") }
    var studentId by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Navigate back on success
    LaunchedEffect(successMessage) {
        successMessage?.let {
            kotlinx.coroutines.delay(1000)
            viewModel.clearSuccessMessage()
            onNavigateBack()
        }
    }
    
    val roles = listOf("student", "instructor", "admin")
    var expandedRole by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create User") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "User Information",
                style = MaterialTheme.typography.titleLarge
            )
            
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username *") },
                placeholder = { Text("Username for login") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password *") },
                placeholder = { Text("Minimum 6 characters") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("user@example.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            ExposedDropdownMenuBox(
                expanded = expandedRole,
                onExpandedChange = { expandedRole = it }
            ) {
                OutlinedTextField(
                    value = selectedRole.replaceFirstChar { it.uppercase() },
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Role *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedRole,
                    onDismissRequest = { expandedRole = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                selectedRole = role
                                expandedRole = false
                            }
                        )
                    }
                }
            }
            
            if (selectedRole == "student") {
                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Student ID") },
                    placeholder = { Text("e.g., STU001") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            if (selectedRole == "student" || selectedRole == "instructor") {
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department") },
                    placeholder = { Text("e.g., Computer Science") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            successMessage?.let { success ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = success,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        return@Button
                    }
                    
                    if (password.length < 6) {
                        return@Button
                    }
                    
                    viewModel.createUser(
                        username = username.trim(),
                        password = password,
                        email = email.trim().takeIf { it.isNotBlank() },
                        firstName = firstName.trim().takeIf { it.isNotBlank() },
                        lastName = lastName.trim().takeIf { it.isNotBlank() },
                        role = selectedRole,
                        studentId = if (selectedRole == "student") studentId.trim().takeIf { it.isNotBlank() } else null,
                        department = if (selectedRole != "admin") department.trim().takeIf { it.isNotBlank() } else null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && username.isNotBlank() && 
                         password.isNotBlank() && password.length >= 6
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create User")
                }
            }
        }
    }
}
