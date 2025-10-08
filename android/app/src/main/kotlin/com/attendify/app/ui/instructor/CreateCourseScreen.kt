package com.attendify.app.ui.instructor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Calendar

/**
 * Create Course Screen for Instructors
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseScreen(
    viewModel: InstructorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var courseCode by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf("") }
    var courseDescription by remember { mutableStateOf("") }
    var selectedSemester by remember { mutableStateOf("Fall") }
    var year by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR).toString()) }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Navigate back on success
    LaunchedEffect(successMessage) {
        successMessage?.let {
            kotlinx.coroutines.delay(1000)
            viewModel.clearSuccessMessage()
            onNavigateBack()
        }
    }
    
    val semesters = listOf("Fall", "Spring", "Summer", "Winter")
    var expandedSemester by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Course") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Course Information",
                style = MaterialTheme.typography.titleLarge
            )
            
            OutlinedTextField(
                value = courseCode,
                onValueChange = { courseCode = it },
                label = { Text("Course Code *") },
                placeholder = { Text("e.g., CS101") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = courseName,
                onValueChange = { courseName = it },
                label = { Text("Course Name *") },
                placeholder = { Text("e.g., Introduction to Programming") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = courseDescription,
                onValueChange = { courseDescription = it },
                label = { Text("Description") },
                placeholder = { Text("Brief course description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            ExposedDropdownMenuBox(
                expanded = expandedSemester,
                onExpandedChange = { expandedSemester = it }
            ) {
                OutlinedTextField(
                    value = selectedSemester,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Semester *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSemester) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedSemester,
                    onDismissRequest = { expandedSemester = false }
                ) {
                    semesters.forEach { semester ->
                        DropdownMenuItem(
                            text = { Text(semester) },
                            onClick = {
                                selectedSemester = semester
                                expandedSemester = false
                            }
                        )
                    }
                }
            }
            
            OutlinedTextField(
                value = year,
                onValueChange = { if (it.length <= 4) year = it },
                label = { Text("Year *") },
                placeholder = { Text("e.g., 2024") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            successMessage?.let { success ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = success,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    if (courseCode.isBlank() || courseName.isBlank() || year.isBlank()) {
                        return@Button
                    }
                    
                    val yearInt = year.toIntOrNull()
                    if (yearInt == null) {
                        return@Button
                    }
                    
                    viewModel.createCourse(
                        code = courseCode.trim(),
                        name = courseName.trim(),
                        description = courseDescription.trim().takeIf { it.isNotBlank() },
                        semester = selectedSemester,
                        year = yearInt
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && courseCode.isNotBlank() && 
                         courseName.isNotBlank() && year.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Course")
                }
            }
        }
    }
}
