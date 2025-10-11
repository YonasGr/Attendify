package com.attendify.app.ui.student

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendify.app.data.model.AttendanceRecord
import com.attendify.app.data.model.Course
import com.attendify.app.data.model.Session
import com.attendify.app.data.repository.AuthRepository
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
 * ViewModel for Student Dashboard and related screens, with improved performance and stability.
 */
@HiltViewModel
class StudentViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _enrolledCourses = MutableStateFlow<List<Course>>(emptyList())
    val enrolledCourses: StateFlow<List<Course>> = _enrolledCourses.asStateFlow()

    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = _attendanceRecords.asStateFlow()

    private val _upcomingSessions = MutableStateFlow<List<Pair<Session, Course?>>>(emptyList())
    val upcomingSessions: StateFlow<List<Pair<Session, Course?>>> = _upcomingSessions.asStateFlow()

    private val _uiState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val uiState: StateFlow<Resource<Unit>> = _uiState.asStateFlow()

    private val _qrActionStatus = MutableStateFlow<Resource<String>?>(null)
    val qrActionStatus: StateFlow<Resource<String>?> = _qrActionStatus.asStateFlow()

    init {
        loadStudentDashboardData()
    }

    fun loadStudentDashboardData() {
        viewModelScope.launch {
            _uiState.value = Resource.Loading()
            val user = authRepository.getCurrentUser()
            if (user == null) {
                _uiState.value = Resource.Error("User not authenticated.")
                return@launch
            }

            // Fetch enrolled courses and attendance in parallel
            val coursesJob = launch {
                networkRepository.getCoursesByStudent(user.id).collectLatest { resource ->
                    if (resource is Resource.Success) {
                        _enrolledCourses.value = resource.data ?: emptyList()
                        fetchUpcomingSessions(resource.data ?: emptyList())
                    }
                }
            }
            val attendanceJob = launch {
                networkRepository.getAttendanceByStudent(user.id).collectLatest { resource ->
                    if (resource is Resource.Success) {
                        _attendanceRecords.value = resource.data ?: emptyList()
                    }
                }
            }

            coursesJob.join()
            attendanceJob.join()
            _uiState.value = Resource.Success(Unit)
        }
    }

    private fun fetchUpcomingSessions(courses: List<Course>) {
        viewModelScope.launch {
            val activeSessions = mutableListOf<Pair<Session, Course?>>()
            courses.forEach { course ->
                networkRepository.getSessionsByCourse(course.id).collectLatest { sessionResource ->
                    if (sessionResource is Resource.Success) {
                        sessionResource.data?.filter { it.isActive }?.forEach {
                            activeSessions.add(it to course)
                        }
                    }
                }
            }
            _upcomingSessions.value = activeSessions.sortedBy { it.first.scheduledDate }
        }
    }

    fun handleScannedQRCode(qrCodeValue: String) {
        viewModelScope.launch {
            _qrActionStatus.value = Resource.Loading()
            val user = authRepository.getCurrentUser()
            if (user?.studentId == null) {
                _qrActionStatus.value = Resource.Error("User or Student ID not found.")
                return@launch
            }

            try {
                val uri = Uri.parse(qrCodeValue)
                if (uri.scheme != "attendify") {
                    _qrActionStatus.value = Resource.Error("Invalid QR code format.")
                    return@launch
                }

                when (uri.host) {
                    "enroll" -> {
                        val courseId = uri.getQueryParameter("courseId")
                        if (courseId == null) {
                            _qrActionStatus.value = Resource.Error("Course ID missing from QR code.")
                            return@launch
                        }
                        networkRepository.createEnrollment(courseId, user.studentId).collectLatest { result ->
                            _qrActionStatus.value = result.map { "Enrolled successfully in course!" }
                            if (result is Resource.Success) loadStudentDashboardData()
                        }
                    }
                    "checkin" -> {
                        val sessionId = uri.getQueryParameter("sessionId")
                        if (sessionId == null) {
                            _qrActionStatus.value = Resource.Error("Session ID missing from QR code.")
                            return@launch
                        }
                        networkRepository.markAttendance(sessionId, user.studentId, qrCodeValue).collectLatest { result ->
                            _qrActionStatus.value = result.map { "Checked in successfully!" }
                            if (result is Resource.Success) loadStudentDashboardData()
                        }
                    }
                    else -> {
                        _qrActionStatus.value = Resource.Error("Unsupported QR code action.")
                    }
                }
            } catch (e: Exception) {
                _qrActionStatus.value = Resource.Error(e.message ?: "Failed to process QR code.")
            }
        }
    }

    fun clearQrActionStatus() {
        _qrActionStatus.value = null
    }
}
