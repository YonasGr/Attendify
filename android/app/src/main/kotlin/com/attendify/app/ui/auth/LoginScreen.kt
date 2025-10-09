package com.attendify.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendify.app.data.model.User
import com.attendify.app.utils.BiometricAuthManager
import com.attendify.app.utils.findActivity

/**
 * Login screen with local authentication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (User) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current
    val biometricAuthManager = remember { BiometricAuthManager(context) }
    val isBiometricAvailable = remember { biometricAuthManager.isBiometricAuthAvailable() }

    // Navigate on successful login
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess((loginState as LoginState.Success).user)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendify") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to Attendify",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "University Attendance System",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Username Input
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                placeholder = { Text("Enter your username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = loginState !is LoginState.Loading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                enabled = loginState !is LoginState.Loading
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (username.isNotBlank() && password.isNotBlank()) {
                        viewModel.login(username, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = username.isNotBlank() && password.isNotBlank() && loginState !is LoginState.Loading
            ) {
                if (loginState is LoginState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login", style = MaterialTheme.typography.titleMedium)
                }
            }

            if (isBiometricAvailable) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        biometricAuthManager.showBiometricPrompt(
                            activity = context.findActivity(),
                            onSuccess = {
                                viewModel.loginWithBiometrics()
                            },
                            onError = { _, _ ->
                                // Handle error
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = "Biometric Login")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Login with Biometrics")
                }
            }
            
            // Error message
            if (loginState is LoginState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Default Accounts:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Admin: username: admin, password: admin123\n" +
                                "Instructor: username: instructor, password: instructor123\n" +
                                "Student: username: student, password: student123",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
