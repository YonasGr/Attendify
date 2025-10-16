package com.attendify.app.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.attendify.app.data.model.User

@Composable
fun AppDrawer(
    user: User?,
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val context = LocalContext.current
    
    fun sendFeedbackEmail(subject: String, bodyTemplate: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("yonasgirma222@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, bodyTemplate)
            }
            
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open email app", Toast.LENGTH_SHORT).show()
        }
    }
    
    ModalDrawerSheet {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user?.profileImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = user?.let { "${it.firstName} ${it.lastName}" } ?: "User",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user?.email ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            NavigationDrawerItem(
                label = { Text("Settings") },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                selected = false,
                onClick = onSettingsClick
            )
            NavigationDrawerItem(
                label = { Text("Edit Profile") },
                icon = { Icon(Icons.Default.Edit, contentDescription = "Edit Profile") },
                selected = false,
                onClick = onEditProfileClick
            )
            NavigationDrawerItem(
                label = { Text("Suggest a Feature") },
                icon = { Icon(Icons.Default.Help, contentDescription = "Suggest a Feature") },
                selected = false,
                onClick = {
                    val userName = user?.let { "${it.firstName} ${it.lastName}" } ?: "User"
                    val userEmail = user?.email ?: "No email"
                    sendFeedbackEmail(
                        subject = "Attendify - Feature Suggestion",
                        bodyTemplate = """
                            |Feature Suggestion:
                            |
                            |[Please describe your feature suggestion here]
                            |
                            |---
                            |User: $userName
                            |Email: $userEmail
                            |Role: ${user?.role ?: "Unknown"}
                        """.trimMargin()
                    )
                }
            )
            NavigationDrawerItem(
                label = { Text("Report a Bug") },
                icon = { Icon(Icons.Default.BugReport, contentDescription = "Report a Bug") },
                selected = false,
                onClick = {
                    val userName = user?.let { "${it.firstName} ${it.lastName}" } ?: "User"
                    val userEmail = user?.email ?: "No email"
                    sendFeedbackEmail(
                        subject = "Attendify - Bug Report",
                        bodyTemplate = """
                            |Bug Report:
                            |
                            |What happened?
                            |[Please describe the bug here]
                            |
                            |Steps to reproduce:
                            |1. 
                            |2. 
                            |3. 
                            |
                            |Expected behavior:
                            |[What did you expect to happen?]
                            |
                            |---
                            |User: $userName
                            |Email: $userEmail
                            |Role: ${user?.role ?: "Unknown"}
                        """.trimMargin()
                    )
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(modifier = Modifier.padding(16.dp)) {
            NavigationDrawerItem(
                label = { Text("Logout") },
                icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
                selected = false,
                onClick = onLogoutClick
            )
        }
    }
}
