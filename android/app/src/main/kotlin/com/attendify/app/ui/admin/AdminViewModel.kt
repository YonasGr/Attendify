package com.attendify.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendify.app.data.model.Course
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
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Admin Dashboard and related screens
 */
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val sessionRepository: SessionRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _userCount = MutableStateFlow(0)
    val userCount: StateFlow<Int> = _userCount.asStateFlow()

    private val _courseCount = MutableStateFlow(0)
    val courseCount: StateFlow<Int> = _courseCount.asStateFlow()

    private val _sessionCount = MutableStateFlow(0)
    val sessionCount: StateFlow<Int> = _sessionCount.asStateFlow()

    private val _attendanceCount = MutableStateFlow(0)
    val attendanceCount: StateFlow<Int> = _attendanceCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadAdminData()
    }

    fun loadAdminData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // Load users
                userRepository.getAllUsers().collect { users ->
                    _users.value = users.map { it.toModel() }
                    _userCount.value = users.size
                }

                // Load courses
                courseRepository.getAllCourses().collect { courses ->
                    _courses.value = courses.map { it.toModel() }
                }

                // Load course count
                courseRepository.getCourseCount().collect { count ->
                    _courseCount.value = count
                }

                // Load session count (all sessions)
                sessionRepository.getAllSessions().collect { sessions ->
                    _sessionCount.value = sessions.size
                }

                // Load attendance count (all records)
                attendanceRepository.getAllAttendanceRecords().collect { records ->
                    _attendanceCount.value = records.size
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load data"
                _isLoading.value = false
            }
        }
    }

    fun createUser(
        username: String,
        password: String,
        email: String?,
        firstName: String?,
        lastName: String?,
        role: String,
        studentId: String?,
        department: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            
            try {
                val result = userRepository.createUser(
                    username = username,
                    password = password,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    role = role,
                    studentId = studentId,
                    department = department
                )

                if (result.isSuccess) {
                    _successMessage.value = "User created successfully"
                    loadAdminData() // Refresh data
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to create user"
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to create user"
                _isLoading.value = false
            }
        }
    }

    fun enrollStudent(studentId: String, courseId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            
            try {
                val result = enrollmentRepository.enrollStudent(studentId, courseId)

                if (result.isSuccess) {
                    _successMessage.value = "Student enrolled successfully"
                    loadAdminData() // Refresh data
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to enroll student"
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to enroll student"
                _isLoading.value = false
            }
        }
    }

    fun getStudents(): List<User> {
        return _users.value.filter { it.role == "student" }
    }

    fun getInstructors(): List<User> {
        return _users.value.filter { it.role == "instructor" }
    }

    fun getAdmins(): List<User> {
        return _users.value.filter { it.role == "admin" }
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
