package com.attendify.app.ui.instructor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendify.app.data.model.Session
import com.attendify.app.utils.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    session: Session,
    viewModel: InstructorViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val attendanceResource by viewModel.getAttendanceForSession(session.id).collectAsState()
    val qrCodeBitmap by viewModel.qrCodeBitmap.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(session.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.generateQrCodeForSession(session) }) {
                        Icon(Icons.Default.QrCode2, contentDescription = "Generate QR Code")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Scanned Students", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }

            when (val resource = attendanceResource) {
                is Resource.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is Resource.Error -> {
                    item {
                        Text(
                            text = resource.message ?: "Failed to load attendance.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is Resource.Success -> {
                    val attendanceRecords = resource.data
                    if (attendanceRecords.isNullOrEmpty()) {
                        item {
                            Text(
                                "No students have scanned in yet.",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    } else {
                        items(attendanceRecords) { record ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        // In a real app, you'd resolve the studentId to a name
                                        Text(
                                            text = "Student ID: ${record.studentId}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Checked in at: ${dateFormatter.format(Date(record.checkedInAt))}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    qrCodeBitmap?.let { bmp ->
        AlertDialog(
            onDismissRequest = { viewModel.clearQrCode() },
            title = { Text("Scan for Attendance") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(300.dp)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Scan this code to mark your attendance.", style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.clearQrCode() }) {
                    Text("Close")
                }
            }
        )
    }
}
