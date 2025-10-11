package com.attendify.app.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendify.app.utils.Resource

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

    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState is Resource.Loading

    LaunchedEffect(uiState) {
        if (uiState is Resource.Success) {
            kotlinx.coroutines.delay(1500)
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
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
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = username.isBlank()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = password.length < 6
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
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
                onExpandedChange = { expandedRole = !expandedRole }
            ) {
                OutlinedTextField(
                    value = selectedRole.replaceFirstChar { it.uppercase() },
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Role") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
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
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            if (selectedRole == "student" || selectedRole == "instructor") {
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            if (uiState is Resource.Error) {
                Text(
                    text = (uiState as Resource.Error<Unit>).message ?: "An unknown error occurred",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
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
                enabled = !isLoading && username.isNotBlank() && password.length >= 6
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Create User")
                }
            }
        }
    }
}
