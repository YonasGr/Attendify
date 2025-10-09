package com.attendify.app.ui.instructor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    session: Session,
    viewModel: InstructorViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val attendanceRecords by viewModel.getAttendanceForSession(session.id).collectAsState()
    val qrCodeBitmap by viewModel.qrCodeBitmap.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(session.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Scanned Students", style = MaterialTheme.typography.headlineSmall)
            }

            if (attendanceRecords.isEmpty()) {
                item {
                    Text("No students have scanned in yet.")
                }
            }

            items(attendanceRecords) {
                // You would typically look up the student's name here
                Text("Student ID: ${it.studentId} at ${it.checkedInAt}")
            }
        }
    }

    qrCodeBitmap?.let { bmp ->
        AlertDialog(
            onDismissRequest = { viewModel.clearQrCode() },
            title = { Text("Scan for Attendance") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(300.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Session ID: ${session.id}", style = MaterialTheme.typography.bodySmall)
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
