package com.attendify.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendify.app.data.model.User
import com.attendify.app.utils.Resource

/**
 * Login screen composable
 * For MVP, uses a simple session token input
 * In production, integrate with Replit Auth or OAuth provider
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (User) -> Unit
) {
    var sessionToken by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()
    
    // Navigate on successful login
    LaunchedEffect(loginState) {
        if (loginState is Resource.Success) {
            loginState?.data?.let { user ->
                onLoginSuccess(user)
            }
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
            
            // Session Token Input (MVP approach)
            OutlinedTextField(
                value = sessionToken,
                onValueChange = { sessionToken = it },
                label = { Text("Session Token") },
                placeholder = { Text("Enter your session token") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                enabled = loginState !is Resource.Loading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Note: Get your session token from the web app after logging in",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (sessionToken.isNotBlank()) {
                        viewModel.login(sessionToken)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = sessionToken.isNotBlank() && loginState !is Resource.Loading
            ) {
                if (loginState is Resource.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login", style = MaterialTheme.typography.titleMedium)
                }
            }
            
            // Error message
            if (loginState is Resource.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = loginState?.message ?: "Login failed",
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
                        text = "How to get your session token:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Login to Attendify web app\n" +
                                "2. Open browser dev tools (F12)\n" +
                                "3. Go to Application/Storage → Cookies\n" +
                                "4. Copy the session cookie value\n" +
                                "5. Paste it here",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
