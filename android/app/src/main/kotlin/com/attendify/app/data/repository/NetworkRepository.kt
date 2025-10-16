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
 * Repository for handling network operations with the backend API.
 * Provides a streamlined and robust approach to data fetching and error handling.
 */
@Singleton
class NetworkRepository @Inject constructor(
    private val api: AttendifyApiService
) {

    /**
     * Generic function to safely execute API calls and handle responses for single items.
     */
    private inline fun <DTO, Domain> safeApiCall(
        crossinline transform: (DTO) -> Domain,
        crossinline apiCall: suspend () -> Response<ApiResponse<DTO>>
    ): Flow<Resource<Domain>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    body.data?.let {
                        emit(Resource.Success(transform(it)))
                    } ?: emit(Resource.Error(body.message ?: "Empty data"))
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                val errorMsg = try {
                    response.errorBody()?.string() ?: "Error ${response.code()}: ${response.message()}"
                } catch (e: Exception) {
                    "Error ${response.code()}: ${response.message()}"
                }
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    /**
     * Generic function to safely execute API calls and handle responses for lists.
     */
    private inline fun <DTO, Domain> safeApiCallForList(
        crossinline transform: (DTO) -> Domain,
        crossinline apiCall: suspend () -> Response<ApiResponse<List<DTO>>>
    ): Flow<Resource<List<Domain>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    body.data?.let {
                        emit(Resource.Success(it.map(transform)))
                    } ?: emit(Resource.Error(body.message ?: "Empty data"))
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                val errorMsg = try {
                    response.errorBody()?.string() ?: "Error ${response.code()}: ${response.message()}"
                } catch (e: Exception) {
                    "Error ${response.code()}: ${response.message()}"
                }
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    // Health Check
    fun checkHealth(): Flow<Resource<HealthResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.health()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()} "))
            }
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
                emit(Resource.Error("Error ${response.code()}: ${response.message()} "))
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
                emit(Resource.Error("Error ${response.code()}: ${response.message()} "))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error during registration"))
        }
    }

    // Users
    fun getAllUsers(): Flow<Resource<List<User>>> = safeApiCallForList({ it.toDomainModel() }) {
        api.getAllUsers()
    }

    fun getUserById(id: String): Flow<Resource<User>> = safeApiCall({ it.toDomainModel() }) {
        api.getUserById(id)
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
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val request = RegisterRequest(
                username, password, email, firstName, lastName, role, studentId, department
            )
            val response = api.createUser(request)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        emit(Resource.Success(apiResponse.data.toDomainModel()))
                    } else {
                        emit(Resource.Error(apiResponse.message ?: "User creation failed"))
                    }
                } ?: emit(Resource.Error("Empty response"))
            } else {
                val errorMsg = try {
                    response.errorBody()?.string() ?: "Error ${response.code()}: ${response.message()}"
                } catch (e: Exception) {
                    "Error ${response.code()}: ${response.message()}"
                }
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error during user creation"))
        }
    }

    // Courses
    fun getAllCourses(): Flow<Resource<List<Course>>> = safeApiCallForList({ it.toDomainModel() }) {
        api.getAllCourses()
    }

    fun getCoursesByInstructor(instructorId: String): Flow<Resource<List<Course>>> = safeApiCallForList({ it.toDomainModel() }) {
        api.getCoursesByInstructor(instructorId)
    }

    fun getCoursesByStudent(studentId: String): Flow<Resource<List<Course>>> = safeApiCallForList({ it.toDomainModel() }) {
        api.getCoursesByStudent(studentId)
    }

    fun createCourse(course: Course): Flow<Resource<Course>> = safeApiCall({ it.toDomainModel() }) {
        api.createCourse(course.toCreateRequest())
    }

    // Sessions
    fun getAllSessions(): Flow<Resource<List<Session>>> = safeApiCallForList({ it.toDomainModel() }) {
        api.getAllSessions()
    }

    fun getSessionsByCourse(courseId: String): Flow<Resource<List<Session>>> = safeApiCallForList({ it.toDomainModel() }) {
        api.getSessionsByCourse(courseId)
    }

    fun createSession(session: Session): Flow<Resource<Session>> = safeApiCall({ it.toDomainModel() }) {
        api.createSession(session.toCreateRequest())
    }

    // Attendance
    fun getAttendanceBySession(sessionId: String): Flow<Resource<List<AttendanceRecord>>> = safeApiCallForList({ it.toDomainModel() }) {
        api.getAttendanceBySession(sessionId)
    }

    fun getAttendanceByStudent(studentId: String): Flow<Resource<List<AttendanceRecord>>> = safeApiCallForList({ it.toDomainModel() }) {
        api.getAttendanceByStudent(studentId)
    }

    fun markAttendance(
        sessionId: String,
        studentId: String,
        qrCode: String?
    ): Flow<Resource<AttendanceRecord>> = safeApiCall({ it.toDomainModel() }) {
        val request = MarkAttendanceRequest(sessionId, studentId, qrCode)
        api.markAttendance(request)
    }

    // Enrollments
    fun getEnrollmentsByStudent(studentId: String): Flow<Resource<List<Enrollment>>> = safeApiCallForList({ it.toDomainModel() }) {
        api.getEnrollmentsByStudent(studentId)
    }

    fun createEnrollment(courseId: String, studentId: String): Flow<Resource<Enrollment>> = safeApiCall({ it.toDomainModel() }) {
        val request = CreateEnrollmentRequest(courseId, studentId)
        api.createEnrollment(request)
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
                response.body()?.data?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Sync upload failed"))
            } else {
                emit(Resource.Error("Error ${response.code()}: ${response.message()} "))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to upload data"))
        }
    }

    fun syncDownload(): Flow<Resource<SyncDownloadResponse>> = safeApiCall({ it }) {
        api.syncDownload()
    }
}
