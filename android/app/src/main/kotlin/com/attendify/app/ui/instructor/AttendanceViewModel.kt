package com.attendify.app.ui.instructor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendify.app.data.model.AttendanceRecord
import com.attendify.app.data.model.Session
import com.attendify.app.data.model.User
import com.attendify.app.data.model.toModel
import com.attendify.app.data.repository.AttendanceRepository
import com.attendify.app.data.repository.CourseRepository
import com.attendify.app.data.repository.EnrollmentRepository
import com.attendify.app.data.repository.SessionRepository
import com.attendify.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing attendance operations
 */
@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val courseRepository: CourseRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val userRepository: UserRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {
    
    private val _currentSession = MutableStateFlow<Session?>(null)
    val currentSession: StateFlow<Session?> = _currentSession.asStateFlow()
    
    private val _enrolledStudents = MutableStateFlow<List<User>>(emptyList())
    val enrolledStudents: StateFlow<List<User>> = _enrolledStudents.asStateFlow()
    
    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = _attendanceRecords.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()
    
    fun loadSessionAttendance(sessionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            
            try {
                // Load session
                val session = sessionRepository.getSessionById(sessionId).first()
                _currentSession.value = session?.toModel()
                
                if (session == null) {
                    _message.value = "Error: Session not found"
                    _isLoading.value = false
                    return@launch
                }
                
                // Load enrolled students for this session's course
                val enrollments = enrollmentRepository.getEnrollmentsByCourse(session.courseId).first()
                val studentIds = enrollments.map { it.studentId }
                
                userRepository.getUsersByRole("student").collect { allStudents ->
                    _enrolledStudents.value = allStudents
                        .filter { it.id in studentIds }
                        .map { it.toModel() }
                }
                
                // Load attendance records for this session
                attendanceRepository.getAttendanceBySession(sessionId).collect { records ->
                    _attendanceRecords.value = records.map { it.toModel() }
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun markAttendance(sessionId: String, studentId: String) {
        viewModelScope.launch {
            _message.value = null
            
            try {
                val result = attendanceRepository.checkIn(sessionId, studentId)
                if (result.isSuccess) {
                    _message.value = "Attendance marked successfully"
                    loadSessionAttendance(sessionId) // Refresh
                } else {
                    _message.value = "Error: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }
    
    fun removeAttendance(sessionId: String, studentId: String) {
        viewModelScope.launch {
            _message.value = null
            
            try {
                val result = attendanceRepository.removeAttendance(sessionId, studentId)
                if (result.isSuccess) {
                    _message.value = "Attendance removed successfully"
                    loadSessionAttendance(sessionId) // Refresh
                } else {
                    _message.value = "Error: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }
    
    fun clearMessage() {
        _message.value = null
    }
}
