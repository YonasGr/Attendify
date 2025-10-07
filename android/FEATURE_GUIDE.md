# Feature Implementation Guide

This guide shows how to implement common features in the Attendify Android app, following the established architecture patterns.

## Table of Contents
1. [Adding a New Screen](#adding-a-new-screen)
2. [Implementing Data Loading](#implementing-data-loading)
3. [Adding QR Code Scanner](#adding-qr-code-scanner)
4. [Creating List Screens](#creating-list-screens)
5. [Form Input Screens](#form-input-screens)
6. [Real-time Updates](#real-time-updates)
7. [Error Handling](#error-handling)
8. [Common Patterns](#common-patterns)

---

## Adding a New Screen

### Example: Course Details Screen

**Step 1: Define the Model (if needed)**

Already done in `Models.kt`, but if you need a new model:

```kotlin
// data/model/Models.kt
data class CourseDetails(
    @SerializedName("course")
    val course: Course,
    
    @SerializedName("sessions")
    val sessions: List<Session>,
    
    @SerializedName("students")
    val students: List<User>
)
```

**Step 2: Add API Endpoint**

```kotlin
// data/api/AttendifyApiService.kt
@GET("courses/{id}/details")
suspend fun getCourseDetails(
    @Path("id") courseId: String
): Response<CourseDetails>
```

**Step 3: Add Repository Method**

```kotlin
// data/repository/AttendifyRepository.kt
fun getCourseDetails(courseId: String): Flow<Resource<CourseDetails>> = flow {
    emit(Resource.Loading())
    try {
        val response = api.getCourseDetails(courseId)
        emit(handleResponse(response))
    } catch (e: Exception) {
        emit(Resource.Error(e.message ?: "Failed to fetch course details"))
    }
}
```

**Step 4: Create ViewModel**

```kotlin
// ui/course/CourseDetailsViewModel.kt
@HiltViewModel
class CourseDetailsViewModel @Inject constructor(
    private val repository: AttendifyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val courseId: String = savedStateHandle["courseId"] ?: ""
    
    private val _courseDetails = MutableStateFlow<Resource<CourseDetails>?>(null)
    val courseDetails: StateFlow<Resource<CourseDetails>?> = _courseDetails.asStateFlow()
    
    init {
        loadCourseDetails()
    }
    
    private fun loadCourseDetails() {
        viewModelScope.launch {
            repository.getCourseDetails(courseId).collect { result ->
                _courseDetails.value = result
            }
        }
    }
    
    fun refresh() {
        loadCourseDetails()
    }
}
```

**Step 5: Create Composable Screen**

```kotlin
// ui/course/CourseDetailsScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailsScreen(
    courseId: String,
    viewModel: CourseDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val courseDetails by viewModel.courseDetails.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = courseDetails) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Resource.Success -> {
                    CourseDetailsContent(state.data!!)
                }
                is Resource.Error -> {
                    ErrorMessage(
                        message = state.message ?: "Failed to load",
                        onRetry = { viewModel.refresh() }
                    )
                }
                null -> {
                    // Initial state
                }
            }
        }
    }
}

@Composable
private fun CourseDetailsContent(details: CourseDetails) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = details.course.name,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        
        item {
            Text(
                text = details.course.description ?: "No description",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Add more UI elements
    }
}
```

**Step 6: Add Navigation**

```kotlin
// ui/Navigation.kt
sealed class Screen(val route: String) {
    // ... existing screens
    
    object CourseDetails : Screen("course_details/{courseId}") {
        fun createRoute(courseId: String) = "course_details/$courseId"
    }
}

// ui/AttendifyApp.kt
composable(
    route = Screen.CourseDetails.route,
    arguments = listOf(navArgument("courseId") { type = NavType.StringType })
) { backStackEntry ->
    val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
    CourseDetailsScreen(
        courseId = courseId,
        onNavigateBack = { navController.navigateUp() }
    )
}
```

---

## Implementing Data Loading

### Pattern: List of Courses for Student

**ViewModel:**

```kotlin
@HiltViewModel
class StudentViewModel @Inject constructor(
    private val repository: AttendifyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _courses = MutableStateFlow<Resource<List<Course>>?>(null)
    val courses: StateFlow<Resource<List<Course>>?> = _courses.asStateFlow()
    
    fun loadCourses() {
        viewModelScope.launch {
            val userId = authRepository.getUser().first()?.id ?: return@launch
            
            // Get enrollments first
            repository.getEnrollmentsByStudent(userId).collect { enrollmentResult ->
                if (enrollmentResult is Resource.Success) {
                    val courseIds = enrollmentResult.data?.map { it.courseId } ?: emptyList()
                    
                    // Then get courses
                    repository.getAllCourses().collect { coursesResult ->
                        if (coursesResult is Resource.Success) {
                            val enrolledCourses = coursesResult.data?.filter { 
                                it.id in courseIds 
                            }
                            _courses.value = Resource.Success(enrolledCourses ?: emptyList())
                        } else {
                            _courses.value = coursesResult
                        }
                    }
                } else {
                    _courses.value = Resource.Error("Failed to load enrollments")
                }
            }
        }
    }
}
```

**UI:**

```kotlin
@Composable
fun CoursesListScreen(viewModel: StudentViewModel = hiltViewModel()) {
    val courses by viewModel.courses.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadCourses()
    }
    
    when (val state = courses) {
        is Resource.Loading -> LoadingIndicator()
        is Resource.Success -> CoursesList(state.data!!)
        is Resource.Error -> ErrorView(state.message)
        null -> EmptyView()
    }
}
```

---

## Adding QR Code Scanner

### Implementation with ZXing

**Step 1: Create Scanner Composable**

```kotlin
// ui/components/QRCodeScanner.kt
@Composable
fun QRCodeScannerScreen(
    onQRCodeScanned: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }
    
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
    
    if (hasCameraPermission) {
        AndroidView(
            factory = { context ->
                val previewView = PreviewView(context)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build()
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    
                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context)
                    ) { imageProxy ->
                        // QR code detection logic
                        val qrCode = detectQRCode(imageProxy)
                        if (qrCode != null) {
                            onQRCodeScanned(qrCode)
                            imageProxy.close()
                        }
                    }
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                    } catch (e: Exception) {
                        // Handle error
                    }
                }, ContextCompat.getMainExecutor(context))
                
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        // Permission denied UI
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Camera permission required")
            Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                Text("Grant Permission")
            }
        }
    }
}
```

**Step 2: Integrate with Attendance**

```kotlin
// ui/student/StudentViewModel.kt
fun checkInWithQRCode(qrCode: String) {
    viewModelScope.launch {
        repository.checkIn(qrCode).collect { result ->
            when (result) {
                is Resource.Success -> {
                    // Show success message
                    _attendanceState.value = "Check-in successful!"
                }
                is Resource.Error -> {
                    _attendanceState.value = "Error: ${result.message}"
                }
                is Resource.Loading -> {
                    _attendanceState.value = "Checking in..."
                }
            }
        }
    }
}
```

---

## Creating List Screens

### Pattern: Lazy Column with Items

```kotlin
@Composable
fun SessionsList(
    sessions: List<Session>,
    onSessionClick: (Session) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = sessions,
            key = { it.id }
        ) { session ->
            SessionCard(
                session = session,
                onClick = { onSessionClick(session) }
            )
        }
    }
}

@Composable
fun SessionCard(
    session: Session,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = session.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Date: ${session.scheduledDate}",
                style = MaterialTheme.typography.bodySmall
            )
            // Add more details
        }
    }
}
```

---

## Form Input Screens

### Pattern: Create Session Form

```kotlin
@Composable
fun CreateSessionScreen(
    courseId: String,
    viewModel: InstructorViewModel = hiltViewModel(),
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    
    val createState by viewModel.createSessionState.collectAsState()
    
    LaunchedEffect(createState) {
        if (createState is Resource.Success) {
            onSuccess()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Session Title") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Add time pickers
        
        Button(
            onClick = {
                viewModel.createSession(
                    courseId = courseId,
                    title = title,
                    scheduledDate = date,
                    startTime = startTime,
                    endTime = endTime
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && date.isNotBlank() &&
                     createState !is Resource.Loading
        ) {
            if (createState is Resource.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Create Session")
            }
        }
        
        if (createState is Resource.Error) {
            Text(
                text = createState.message ?: "Error",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
```

---

## Real-time Updates

### Pattern: Polling for Updates

```kotlin
@HiltViewModel
class SessionAttendanceViewModel @Inject constructor(
    private val repository: AttendifyRepository
) : ViewModel() {
    
    private val _attendance = MutableStateFlow<Resource<List<AttendanceRecord>>?>(null)
    val attendance: StateFlow<Resource<List<AttendanceRecord>>?> = _attendance.asStateFlow()
    
    fun startPolling(sessionId: String) {
        viewModelScope.launch {
            while (isActive) {
                repository.getAttendanceBySession(sessionId).collect { result ->
                    _attendance.value = result
                }
                delay(5000) // Poll every 5 seconds
            }
        }
    }
}
```

---

## Error Handling

### Pattern: Reusable Error Component

```kotlin
@Composable
fun ErrorMessage(
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
```

---

## Common Patterns

### 1. Loading State

```kotlin
@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
```

### 2. Empty State

```kotlin
@Composable
fun EmptyState(
    message: String,
    icon: ImageVector = Icons.Default.Info
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
```

### 3. Confirmation Dialog

```kotlin
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
```

### 4. Pull to Refresh

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshableList(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )
    
    Box(
        modifier = Modifier.pullRefresh(pullRefreshState)
    ) {
        content()
        
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
```

---

## Best Practices

1. **Always use StateFlow for UI state**
2. **Collect flows in LaunchedEffect or collectAsState**
3. **Handle all Resource states (Loading, Success, Error)**
4. **Use meaningful error messages**
5. **Add loading indicators for long operations**
6. **Provide retry mechanisms**
7. **Use proper coroutine scopes (viewModelScope)**
8. **Cancel ongoing operations when appropriate**
9. **Test edge cases (no data, errors, etc.)**
10. **Follow Material 3 design guidelines**

---

## Testing Your Implementation

### ViewModel Test Template

```kotlin
@Test
fun `loadCourses should emit success when API succeeds`() = runTest {
    // Arrange
    val mockCourses = listOf(/* test data */)
    coEvery { repository.getAllCourses() } returns flowOf(
        Resource.Success(mockCourses)
    )
    
    // Act
    viewModel.loadCourses()
    
    // Assert
    assertEquals(Resource.Success(mockCourses), viewModel.courses.value)
}
```

This guide should help you implement new features consistently with the existing architecture!
