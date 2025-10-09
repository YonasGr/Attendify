package com.attendify.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendify.app.utils.BiometricAuthManager
import com.attendify.app.utils.findActivity

/**
 * Settings Screen for user preferences
 * Includes biometric authentication setup
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val biometricAuthManager = remember { BiometricAuthManager(context) }
    val isBiometricAvailable = remember { biometricAuthManager.isBiometricAuthAvailable() }
    
    val currentUser by viewModel.currentUser.collectAsState()
    val settingsState by viewModel.settingsState.collectAsState()
    
    var showBiometricDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Divider()
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentUser?.let { "${it.firstName} ${it.lastName}" } ?: "User",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = currentUser?.email ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Security section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Security",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Divider()
                    
                    // Biometric authentication toggle
                    if (isBiometricAvailable) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Biometric Login",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Use fingerprint or face to sign in",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = currentUser?.biometricEnabled ?: false,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        showBiometricDialog = true
                                    } else {
                                        viewModel.setBiometricEnabled(false)
                                    }
                                }
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Biometric Login",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "Not available on this device",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            // Show success/error messages
            if (settingsState is SettingsState.Success) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = (settingsState as SettingsState.Success).message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            if (settingsState is SettingsState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = (settingsState as SettingsState.Error).message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
    
    // Biometric authentication dialog
    if (showBiometricDialog) {
        LaunchedEffect(Unit) {
            biometricAuthManager.showBiometricPrompt(
                activity = context.findActivity(),
                onSuccess = {
                    viewModel.setBiometricEnabled(true)
                    showBiometricDialog = false
                },
                onError = { _, _ ->
                    showBiometricDialog = false
                    viewModel.setError("Failed to enable biometric authentication")
                }
            )
        }
    }
}
