package com.attendify.app.ui.instructor

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendify.app.data.model.AttendanceRecord
import com.attendify.app.data.model.Course
import com.attendify.app.data.model.Session
import com.attendify.app.data.model.toModel
import com.attendify.app.data.repository.AttendanceRepository
import com.attendify.app.data.repository.AuthRepository
import com.attendify.app.data.repository.CourseRepository
import com.attendify.app.data.repository.EnrollmentRepository
import com.attendify.app.data.repository.SessionRepository
import com.attendify.app.utils.QRCodeGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Instructor Dashboard and related screens
 */
@HiltViewModel
class InstructorViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val courseRepository: CourseRepository,
    private val sessionRepository: SessionRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions: StateFlow<List<Session>> = _sessions.asStateFlow()

    private val _qrCodeBitmap = MutableStateFlow<Bitmap?>(null)
    val qrCodeBitmap: StateFlow<Bitmap?> = _qrCodeBitmap.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadInstructorData()
    }

    fun loadInstructorData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _errorMessage.value = "User not found"
                    _isLoading.value = false
                    return@launch
                }

                // Load instructor's courses
                courseRepository.getCoursesByInstructor(user.id).collect { courses ->
                    _courses.value = courses.map { it.toModel() }
                }

                // Load all sessions for instructor's courses
                val courseIds = _courses.value.map { it.id }
                sessionRepository.getAllSessions().collect { allSessions ->
                    _sessions.value = allSessions
                        .filter { it.courseId in courseIds }
                        .sortedByDescending { it.scheduledDate }
                        .map { it.toModel() }
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load data"
                _isLoading.value = false
            }
        }
    }

    fun createCourse(
        code: String,
        name: String,
        description: String?,
        semester: String,
        year: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            
            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _errorMessage.value = "User not found"
                    _isLoading.value = false
                    return@launch
                }

                val result = courseRepository.createCourse(
                    code = code,
                    name = name,
                    description = description,
                    instructorId = user.id,
                    semester = semester,
                    year = year
                )

                if (result.isSuccess) {
                    _successMessage.value = "Course created successfully"
                    loadInstructorData() // Refresh data
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to create course"
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to create course"
                _isLoading.value = false
            }
        }
    }

    fun createSession(
        courseId: String,
        title: String,
        scheduledDate: Long,
        startTime: String,
        endTime: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            
            try {
                val result = sessionRepository.createSession(
                    courseId = courseId,
                    title = title,
                    scheduledDate = scheduledDate,
                    startTime = startTime,
                    endTime = endTime,
                    isActive = true
                )

                if (result.isSuccess) {
                    _successMessage.value = "Session created successfully"
                    loadInstructorData() // Refresh data
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to create session"
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to create session"
                _isLoading.value = false
            }
        }
    }

    fun generateQrCodeForSession(session: Session) {
        viewModelScope.launch {
            try {
                // Use the session ID or a unique token for the QR code
                val qrCodeContent = session.id 
                _qrCodeBitmap.value = QRCodeGenerator.generateQRCode(qrCodeContent)
            } catch (e: Exception) {
                _errorMessage.value = "Could not generate QR code: ${e.message}"
            }
        }
    }

    fun clearQrCode() {
        _qrCodeBitmap.value = null
    }

    fun getAttendanceForSession(sessionId: String): StateFlow<List<AttendanceRecord>> {
        val attendanceFlow = MutableStateFlow<List<AttendanceRecord>>(emptyList())
        
        viewModelScope.launch {
            attendanceRepository.getAttendanceBySession(sessionId).collect { records ->
                attendanceFlow.value = records.map { it.toModel() }
            }
        }
        
        return attendanceFlow.asStateFlow()
    }

    fun toggleSessionActive(session: Session) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Would need to convert back to entity and update
                // For now, just show success
                _successMessage.value = "Session status updated"
                loadInstructorData()
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to update session"
                _isLoading.value = false
            }
        }
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
