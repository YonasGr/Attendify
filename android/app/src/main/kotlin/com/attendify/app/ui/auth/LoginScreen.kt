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
    
    // Check if user has biometric enabled
    val isBiometricEnabledForUser by viewModel.isBiometricEnabledForUser.collectAsState()

    // Navigate on successful login
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess((loginState as LoginState.Success).user)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendify", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App branding section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to Attendify",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "📚 University Attendance Management System",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Login form card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Username Input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        placeholder = { Text("Enter your username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = loginState !is LoginState.Loading,
                        shape = MaterialTheme.shapes.medium
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
                        enabled = loginState !is LoginState.Loading,
                        shape = MaterialTheme.shapes.medium
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Login Button
                    Button(
                        onClick = {
                            if (username.isNotBlank() && password.isNotBlank()) {
                                viewModel.login(username, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = username.isNotBlank() && password.isNotBlank() && loginState !is LoginState.Loading,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (loginState is LoginState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Sign In",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Biometric login option (only show if user has enabled it)
                    if (isBiometricAvailable && isBiometricEnabledForUser) {
                        Spacer(modifier = Modifier.height(12.dp))
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
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = "Biometric Login")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign In with Biometrics")
                        }
                    }
                    
                    // Error message
                    if (loginState is LoginState.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = (loginState as LoginState.Error).message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Demo credentials card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📝 Demo Accounts",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "👤 Admin: admin / admin123\n" +
                                "🧑‍🏫 Instructor: instructor / instructor123\n" +
                                "🎓 Student: student / student123",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
