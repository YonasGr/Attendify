package com.attendify.app.data.repository

import com.attendify.app.data.api.*
import com.attendify.app.data.model.*
import com.attendify.app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling network operations with the backend API
 * Provides offline-first architecture with proper error handling
 */
@Singleton
class NetworkRepository @Inject constructor(
    private val api: AttendifyApiService
) {
    
    /**
     * Generic function to handle API responses
     */
    private fun <T> handleResponse(response: Response<T>): Resource<T> {
        return if (response.isSuccessful) {
            response.body()?.let {
                Resource.Success(it)
            } ?: Resource.Error("Empty response body")
        } else {
            Resource.Error("Error ${response.code()}: ${response.message()}")
        }
    }
    
    // Health Check
    fun checkHealth(): Flow<Resource<HealthResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.health()
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to check server health"))
        }
    }
    
    // Authentication
    fun login(username: String, password: String): Flow<Resource<LoginData>> = flow {
        emit(Resource.Loading())
        try {
            val request = LoginRequest(username, password)
            val response = api.login(request)
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    if (loginResponse.success && loginResponse.data != null) {
                        emit(Resource.Success(loginResponse.data))
                    } else {
                        emit(Resource.Error(loginResponse.message ?: "Login failed"))
                    }
                } ?: emit(Resource.Error("Empty response"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error during login"))
        }
    }
    
    fun register(
        username: String,
        password: String,
        email: String?,
        firstName: String?,
        lastName: String?,
        role: String,
        studentId: String?,
        department: String?
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val request = RegisterRequest(
                username, password, email, firstName, lastName, role, studentId, department
            )
            val response = api.register(request)
            if (response.isSuccessful) {
                response.body()?.let { registerResponse ->
                    if (registerResponse.success && registerResponse.data != null) {
                        emit(Resource.Success(registerResponse.data.toDomainModel()))
                    } else {
                        emit(Resource.Error(registerResponse.message ?: "Registration failed"))
                    }
                } ?: emit(Resource.Error("Empty response"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error during registration"))
        }
    }
    
    // Users
    fun getAllUsers(): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAllUsers()
            if (response.isSuccessful) {
                response.body()?.data?.let { users ->
                    emit(Resource.Success(users.map { it.toDomainModel() }))
                } ?: emit(Resource.Error("No users data"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch users"))
        }
    }
    
    fun getUserById(id: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getUserById(id)
            if (response.isSuccessful) {
                response.body()?.data?.let { user ->
                    emit(Resource.Success(user.toDomainModel()))
                } ?: emit(Resource.Error("User not found"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch user"))
        }
    }
    
    // Courses
    fun getAllCourses(): Flow<Resource<List<Course>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAllCourses()
            if (response.isSuccessful) {
                response.body()?.data?.let { courses ->
                    emit(Resource.Success(courses.map { it.toDomainModel() }))
                } ?: emit(Resource.Error("No courses data"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch courses"))
        }
    }
    
    fun getCoursesByInstructor(instructorId: String): Flow<Resource<List<Course>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getCoursesByInstructor(instructorId)
            if (response.isSuccessful) {
                response.body()?.data?.let { courses ->
                    emit(Resource.Success(courses.map { it.toDomainModel() }))
                } ?: emit(Resource.Error("No courses data"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch courses"))
        }
    }
    
    fun getCoursesByStudent(studentId: String): Flow<Resource<List<Course>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getCoursesByStudent(studentId)
            if (response.isSuccessful) {
                response.body()?.data?.let { courses ->
                    emit(Resource.Success(courses.map { it.toDomainModel() }))
                } ?: emit(Resource.Error("No courses data"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch courses"))
        }
    }
    
    fun createCourse(course: Course): Flow<Resource<Course>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.createCourse(course.toCreateRequest())
            if (response.isSuccessful) {
                response.body()?.data?.let { createdCourse ->
                    emit(Resource.Success(createdCourse.toDomainModel()))
                } ?: emit(Resource.Error("Failed to create course"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create course"))
        }
    }
    
    // Sessions
    fun getAllSessions(): Flow<Resource<List<Session>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAllSessions()
            if (response.isSuccessful) {
                response.body()?.data?.let { sessions ->
                    emit(Resource.Success(sessions.map { it.toDomainModel() }))
                } ?: emit(Resource.Error("No sessions data"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch sessions"))
        }
    }
    
    fun getSessionsByCourse(courseId: String): Flow<Resource<List<Session>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getSessionsByCourse(courseId)
            if (response.isSuccessful) {
                response.body()?.data?.let { sessions ->
                    emit(Resource.Success(sessions.map { it.toDomainModel() }))
                } ?: emit(Resource.Error("No sessions data"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch sessions"))
        }
    }
    
    fun createSession(session: Session): Flow<Resource<Session>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.createSession(session.toCreateRequest())
            if (response.isSuccessful) {
                response.body()?.data?.let { createdSession ->
                    emit(Resource.Success(createdSession.toDomainModel()))
                } ?: emit(Resource.Error("Failed to create session"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create session"))
        }
    }
    
    // Attendance
    fun getAttendanceBySession(sessionId: String): Flow<Resource<List<AttendanceRecord>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAttendanceBySession(sessionId)
            if (response.isSuccessful) {
                response.body()?.data?.let { attendance ->
                    emit(Resource.Success(attendance.map { it.toDomainModel() }))
                } ?: emit(Resource.Error("No attendance data"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch attendance"))
        }
    }
    
    fun getAttendanceByStudent(studentId: String): Flow<Resource<List<AttendanceRecord>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAttendanceByStudent(studentId)
            if (response.isSuccessful) {
                response.body()?.data?.let { attendance ->
                    emit(Resource.Success(attendance.map { it.toDomainModel() }))
                } ?: emit(Resource.Error("No attendance data"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch attendance"))
        }
    }
    
    fun markAttendance(
        sessionId: String,
        studentId: String,
        qrCode: String?
    ): Flow<Resource<AttendanceRecord>> = flow {
        emit(Resource.Loading())
        try {
            val request = MarkAttendanceRequest(sessionId, studentId, qrCode)
            val response = api.markAttendance(request)
            if (response.isSuccessful) {
                response.body()?.data?.let { attendance ->
                    emit(Resource.Success(attendance.toDomainModel()))
                } ?: emit(Resource.Error("Failed to mark attendance"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to mark attendance"))
        }
    }
    
    // Enrollments
    fun getEnrollmentsByStudent(studentId: String): Flow<Resource<List<Enrollment>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getEnrollmentsByStudent(studentId)
            if (response.isSuccessful) {
                response.body()?.data?.let { enrollments ->
                    emit(Resource.Success(enrollments.map { it.toDomainModel() }))
                } ?: emit(Resource.Error("No enrollments data"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch enrollments"))
        }
    }
    
    fun createEnrollment(courseId: String, studentId: String): Flow<Resource<Enrollment>> = flow {
        emit(Resource.Loading())
        try {
            val request = CreateEnrollmentRequest(courseId, studentId)
            val response = api.createEnrollment(request)
            if (response.isSuccessful) {
                response.body()?.data?.let { enrollment ->
                    emit(Resource.Success(enrollment.toDomainModel()))
                } ?: emit(Resource.Error("Failed to create enrollment"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create enrollment"))
        }
    }
    
    // Sync operations
    fun syncUpload(
        users: List<User>?,
        courses: List<Course>?,
        sessions: List<Session>?,
        enrollments: List<Enrollment>?,
        attendance: List<AttendanceRecord>?
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val request = SyncUploadRequest(
                users = users?.map { it.toDto() },
                courses = courses?.map { it.toDto() },
                sessions = sessions?.map { it.toDto() },
                enrollments = enrollments?.map { it.toDto() },
                attendance = attendance?.map { it.toDto() }
            )
            val response = api.syncUpload(request)
            if (response.isSuccessful) {
                response.body()?.data?.let { result ->
                    emit(Resource.Success(result))
                } ?: emit(Resource.Error("Sync upload failed"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to upload data"))
        }
    }
    
    fun syncDownload(): Flow<Resource<SyncDownloadResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.syncDownload()
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    emit(Resource.Success(data))
                } ?: emit(Resource.Error("No sync data"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to download data"))
        }
    }
}
