package com.attendify.app.ui.instructor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendify.app.data.model.Course
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create Session Screen for Instructors
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSessionScreen(
    viewModel: InstructorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onSessionCreated: () -> Unit
) {
    var sessionTitle by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCourseSelector by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCreating by remember { mutableStateOf(false) }

    val courses by viewModel.courses.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Create Session",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
            // Information card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Create a new attendance session for your course. Students will use QR codes to mark their attendance.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Session Title
            OutlinedTextField(
                value = sessionTitle,
                onValueChange = { sessionTitle = it },
                label = { Text("Session Title *") },
                placeholder = { Text("e.g., Introduction to Programming - Lecture 1") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Title, "Title")
                },
                enabled = !isCreating,
                shape = RoundedCornerShape(12.dp)
            )

            // Course Selection
            OutlinedButton(
                onClick = { showCourseSelector = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCreating,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.School, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedCourse?.let { "${it.code} - ${it.name}" } ?: "Select Course *",
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            // Date Selection
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCreating,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedDate?.let { dateFormatter.format(Date(it)) } ?: "Select Date *",
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            // Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Start Time *") },
                    placeholder = { Text("09:00") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.AccessTime, "Start")
                    },
                    enabled = !isCreating,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("End Time *") },
                    placeholder = { Text("10:30") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.AccessTime, "End")
                    },
                    enabled = !isCreating,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Active Status
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Active Session",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Students can scan QR code when active",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        enabled = !isCreating
                    )
                }
            }

            // Error Message
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Create Button
            Button(
                onClick = {
                    // Validate inputs
                    when {
                        sessionTitle.isBlank() -> errorMessage = "Please enter a session title"
                        selectedCourse == null -> errorMessage = "Please select a course"
                        selectedDate == null -> errorMessage = "Please select a date"
                        startTime.isBlank() -> errorMessage = "Please enter start time"
                        endTime.isBlank() -> errorMessage = "Please enter end time"
                        !isValidTimeFormat(startTime) -> errorMessage = "Invalid start time format (use HH:mm)"
                        !isValidTimeFormat(endTime) -> errorMessage = "Invalid end time format (use HH:mm)"
                        else -> {
                            errorMessage = null
                            isCreating = true
                            viewModel.createSession(
                                courseId = selectedCourse!!.id,
                                title = sessionTitle,
                                scheduledDate = selectedDate!!,
                                startTime = startTime,
                                endTime = endTime,
                                isActive = isActive,
                                onSuccess = {
                                    onSessionCreated()
                                },
                                onError = { error ->
                                    errorMessage = error
                                    isCreating = false
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isCreating,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Creating Session...")
                } else {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Create Session",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCreating,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
        }

        // Course Selector Dialog
        if (showCourseSelector) {
            AlertDialog(
                onDismissRequest = { showCourseSelector = false },
                title = { Text("Select Course") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        courses.forEach { course ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    selectedCourse = course
                                    showCourseSelector = false
                                }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = course.code,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = course.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCourseSelector = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedDate = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

private fun isValidTimeFormat(time: String): Boolean {
    return time.matches(Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$"))
}
