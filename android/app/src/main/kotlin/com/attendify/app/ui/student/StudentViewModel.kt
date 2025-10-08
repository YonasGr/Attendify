package com.attendify.app.ui.student

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Student Dashboard and related screens
 */
@HiltViewModel
class StudentViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val courseRepository: CourseRepository,
    private val sessionRepository: SessionRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _enrolledCourses = MutableStateFlow<List<Course>>(emptyList())
    val enrolledCourses: StateFlow<List<Course>> = _enrolledCourses.asStateFlow()

    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = _attendanceRecords.asStateFlow()

    private val _upcomingSessions = MutableStateFlow<List<Session>>(emptyList())
    val upcomingSessions: StateFlow<List<Session>> = _upcomingSessions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _checkInMessage = MutableStateFlow<String?>(null)
    val checkInMessage: StateFlow<String?> = _checkInMessage.asStateFlow()

    init {
        loadStudentData()
    }

    fun loadStudentData() {
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

                // Load enrollments
                enrollmentRepository.getEnrollmentsByStudent(user.id).collect { enrollments ->
                    val courseIds = enrollments.map { it.courseId }
                    
                    // Load enrolled courses
                    courseRepository.getAllCourses().collect { allCourses ->
                        _enrolledCourses.value = allCourses
                            .filter { it.id in courseIds }
                            .map { it.toModel() }
                    }
                }

                // Load attendance records
                attendanceRepository.getAttendanceByStudent(user.id).collect { records ->
                    _attendanceRecords.value = records.map { it.toModel() }
                }

                // Load upcoming sessions from enrolled courses
                val courseIds = _enrolledCourses.value.map { it.id }
                sessionRepository.getAllSessions().collect { allSessions ->
                    _upcomingSessions.value = allSessions
                        .filter { it.courseId in courseIds && it.isActive }
                        .sortedBy { it.scheduledDate }
                        .map { it.toModel() }
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load data"
                _isLoading.value = false
            }
        }
    }

    fun checkInWithQRCode(qrCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _checkInMessage.value = null
            
            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _checkInMessage.value = "User not found"
                    _isLoading.value = false
                    return@launch
                }

                // Find session by QR code
                val session = sessionRepository.getSessionByQrCode(qrCode)
                if (session == null) {
                    _checkInMessage.value = "Invalid QR code"
                    _isLoading.value = false
                    return@launch
                }

                if (!session.isActive) {
                    _checkInMessage.value = "This session is not active"
                    _isLoading.value = false
                    return@launch
                }

                // Check if student is enrolled in the course
                val enrollments = enrollmentRepository.getEnrollmentsByStudent(user.id).first()
                val isEnrolled = enrollments.any { it.courseId == session.courseId }
                
                if (!isEnrolled) {
                    _checkInMessage.value = "You are not enrolled in this course"
                    _isLoading.value = false
                    return@launch
                }

                // Check in
                val result = attendanceRepository.checkIn(session.id, user.id)
                if (result.isSuccess) {
                    _checkInMessage.value = "Attendance marked successfully!"
                    loadStudentData() // Refresh data
                } else {
                    _checkInMessage.value = result.exceptionOrNull()?.message ?: "Failed to mark attendance"
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _checkInMessage.value = e.message ?: "Failed to mark attendance"
                _isLoading.value = false
            }
        }
    }

    fun clearCheckInMessage() {
        _checkInMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
