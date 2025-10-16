package com.attendify.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendify.app.data.model.Course
import com.attendify.app.data.model.Session
import com.attendify.app.data.model.User
import com.attendify.app.data.repository.NetworkRepository
import com.attendify.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Admin Dashboard and related screens, with improved performance and stability.
 */
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions: StateFlow<List<Session>> = _sessions.asStateFlow()

    private val _uiState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val uiState: StateFlow<Resource<Unit>> = _uiState.asStateFlow()

    init {
        loadAdminDashboardData()
    }

    fun loadAdminDashboardData() {
        viewModelScope.launch {
            _uiState.value = Resource.Loading()
            val usersJob = launch { networkRepository.getAllUsers().collectLatest { _users.value = it.data ?: emptyList() } }
            val coursesJob = launch { networkRepository.getAllCourses().collectLatest { _courses.value = it.data ?: emptyList() } }
            val sessionsJob = launch { networkRepository.getAllSessions().collectLatest { _sessions.value = it.data ?: emptyList() } }

            usersJob.join()
            coursesJob.join()
            sessionsJob.join()

            _uiState.value = Resource.Success(Unit)
        }
    }

    fun createUser(
        username: String, password: String, email: String?, firstName: String?, lastName: String?,
        role: String, studentId: String?, department: String?
    ) {
        viewModelScope.launch {
            _uiState.value = Resource.Loading()
            networkRepository.createUser(
                username, password, email, firstName, lastName, role, studentId, department
            ).collectLatest { result ->
                _uiState.value = result.map { Unit } // Map result to Resource<Unit>
                if (result is Resource.Success) {
                    loadAdminDashboardData() // Refresh data on success
                }
            }
        }
    }

    fun enrollStudent(studentId: String, courseId: String) {
        viewModelScope.launch {
            _uiState.value = Resource.Loading()
            networkRepository.createEnrollment(courseId, studentId).collectLatest { result ->
                _uiState.value = result.map { Unit } // Map result to Resource<Unit>
                if (result is Resource.Success) {
                    // Optionally refresh some data
                }
            }
        }
    }

    fun getStudents(): List<User> = _users.value.filter { it.role.equals("student", ignoreCase = true) }

    fun getInstructors(): List<User> = _users.value.filter { it.role.equals("instructor", ignoreCase = true) }

    fun getAdmins(): List<User> = _users.value.filter { it.role.equals("admin", ignoreCase = true) }
}
