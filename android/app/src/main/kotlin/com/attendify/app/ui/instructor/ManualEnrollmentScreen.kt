package com.attendify.app.ui.instructor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Manual Enrollment Screen for Instructors
 * Allows manual enrollment of students (e.g., for missed QR scans)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEnrollmentScreen(
    viewModel: EnrollmentViewModel = hiltViewModel(),
    courseId: String,
    onNavigateBack: () -> Unit
) {
    val allStudents by viewModel.allStudents.collectAsState()
    val enrolledStudents by viewModel.enrolledStudents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    
    LaunchedEffect(courseId) {
        viewModel.loadStudentsForCourse(courseId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enroll Students") },
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
        ) {
            // Message display
            message?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (msg.startsWith("Error")) 
                            MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            if (msg.startsWith("Error")) Icons.Default.Error else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (msg.startsWith("Error"))
                                MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (msg.startsWith("Error"))
                                MaterialTheme.colorScheme.onErrorContainer
                            else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val enrolledIds = enrolledStudents.map { it.id }.toSet()
                val unenrolledStudents = allStudents.filter { it.id !in enrolledIds }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    
                    // Enrolled students section
                    if (enrolledStudents.isNotEmpty()) {
                        item {
                            Text(
                                text = "Enrolled (${enrolledStudents.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(enrolledStudents) { student ->
                            StudentEnrollmentCard(
                                studentName = "${student.firstName} ${student.lastName}",
                                studentId = student.studentId ?: "N/A",
                                department = student.department ?: "N/A",
                                isEnrolled = true,
                                onToggleEnrollment = {
                                    viewModel.unenrollStudent(courseId, student.id)
                                }
                            )
                        }
                    }
                    
                    // Available students section
                    if (unenrolledStudents.isNotEmpty()) {
                        item {
                            Text(
                                text = "Available Students (${unenrolledStudents.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(unenrolledStudents) { student ->
                            StudentEnrollmentCard(
                                studentName = "${student.firstName} ${student.lastName}",
                                studentId = student.studentId ?: "N/A",
                                department = student.department ?: "N/A",
                                isEnrolled = false,
                                onToggleEnrollment = {
                                    viewModel.enrollStudent(courseId, student.id)
                                }
                            )
                        }
                    }
                    
                    if (allStudents.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "No students available",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun StudentEnrollmentCard(
    studentName: String,
    studentId: String,
    department: String,
    isEnrolled: Boolean,
    onToggleEnrollment: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = studentName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: $studentId • $department",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isEnrolled) {
                IconButton(
                    onClick = onToggleEnrollment,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.RemoveCircle, contentDescription = "Unenroll")
                }
            } else {
                IconButton(
                    onClick = onToggleEnrollment,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Enroll")
                }
            }
        }
    }
}
