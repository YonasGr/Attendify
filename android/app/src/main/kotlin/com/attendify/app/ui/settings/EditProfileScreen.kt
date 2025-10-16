package com.attendify.app.ui.settings

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val settingsState by viewModel.settingsState.collectAsState()
    
    var firstName by remember { mutableStateOf(user?.firstName ?: "") }
    var lastName by remember { mutableStateOf(user?.lastName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Update fields when user data loads
    LaunchedEffect(user) {
        user?.let {
            firstName = it.firstName ?: ""
            lastName = it.lastName ?: ""
            email = it.email ?: ""
        }
    }

    // Navigate back on success
    LaunchedEffect(settingsState) {
        if (settingsState is SettingsState.Success) {
            kotlinx.coroutines.delay(1500)
            onNavigateBack()
        }
    }

    val isLoading = settingsState is SettingsState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
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
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("New Password (optional)") },
                placeholder = { Text("Leave blank to keep current password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                enabled = !isLoading,
                isError = password.isNotBlank() && password != confirmPassword
            )

            if (password.isNotBlank() && password != confirmPassword) {
                Text(
                    text = "Passwords do not match",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            when (settingsState) {
                is SettingsState.Success -> {
                    Text(
                        text = (settingsState as SettingsState.Success).message,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                is SettingsState.Error -> {
                    Text(
                        text = (settingsState as SettingsState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> {}
            }

            Button(
                onClick = {
                    if (password.isNotBlank() && password != confirmPassword) {
                        viewModel.setError("Passwords do not match")
                        return@Button
                    }
                    
                    viewModel.updateProfile(
                        firstName = firstName.trim(),
                        lastName = lastName.trim(),
                        email = email.trim(),
                        password = password.takeIf { it.isNotBlank() } ?: ""
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Changes")
                }
            }
        }
    }
}
