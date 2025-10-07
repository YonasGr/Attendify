package com.attendify.app.data.repository

import com.attendify.app.data.api.AttendifyApiService
import com.attendify.app.data.model.*
import com.attendify.app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for all Attendify API operations
 * Converts Retrofit responses to Flow<Resource<T>> for easy UI consumption
 */
@Singleton
class AttendifyRepository @Inject constructor(
    private val api: AttendifyApiService
) {
    
    // ============ User Operations ============
    
    fun getCurrentUser(): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getCurrentUser()
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch user"))
        }
    }
    
    fun getAllUsers(): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAllUsers()
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch users"))
        }
    }
    
    fun updateUser(userId: String, request: UpdateUserRequest): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.updateUser(userId, request)
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update user"))
        }
    }
    
    // ============ Course Operations ============
    
    fun getAllCourses(): Flow<Resource<List<Course>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAllCourses()
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch courses"))
        }
    }
    
    fun getCoursesByInstructor(instructorId: String): Flow<Resource<List<Course>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getCoursesByInstructor(instructorId)
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch courses"))
        }
    }
    
    fun createCourse(request: CreateCourseRequest): Flow<Resource<Course>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.createCourse(request)
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create course"))
        }
    }
    
    // ============ Enrollment Operations ============
    
    fun getEnrollmentsByStudent(studentId: String): Flow<Resource<List<Enrollment>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getEnrollmentsByStudent(studentId)
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch enrollments"))
        }
    }
    
    fun getEnrollmentsByCourse(courseId: String): Flow<Resource<List<Enrollment>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getEnrollmentsByCourse(courseId)
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch enrollments"))
        }
    }
    
    fun createEnrollment(request: CreateEnrollmentRequest): Flow<Resource<Enrollment>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.createEnrollment(request)
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create enrollment"))
        }
    }
    
    // ============ Session Operations ============
    
    fun getSessionsByCourse(courseId: String): Flow<Resource<List<Session>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getSessionsByCourse(courseId)
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch sessions"))
        }
    }
    
    fun createSession(request: CreateSessionRequest): Flow<Resource<Session>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.createSession(request)
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create session"))
        }
    }
    
    fun getSessionQRCode(sessionId: String): Flow<Resource<QRCodeResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getSessionQRCode(sessionId)
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch QR code"))
        }
    }
    
    // ============ Attendance Operations ============
    
    fun checkIn(qrCode: String): Flow<Resource<AttendanceRecord>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.checkIn(CheckInRequest(qrCode))
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to check in"))
        }
    }
    
    fun getAttendanceByStudent(studentId: String): Flow<Resource<List<AttendanceRecord>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAttendanceByStudent(studentId)
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch attendance"))
        }
    }
    
    fun getAttendanceBySession(sessionId: String): Flow<Resource<List<AttendanceRecord>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAttendanceBySession(sessionId)
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch attendance"))
        }
    }
    
    fun getAttendanceByCourseAndStudent(
        courseId: String,
        studentId: String
    ): Flow<Resource<List<AttendanceRecord>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAttendanceByCourseAndStudent(courseId, studentId)
            emit(handleResponse(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch attendance"))
        }
    }
    
    // ============ Helper Functions ============
    
    private fun <T> handleResponse(response: Response<T>): Resource<T> {
        return if (response.isSuccessful) {
            response.body()?.let {
                Resource.Success(it)
            } ?: Resource.Error("Empty response body")
        } else {
            Resource.Error(response.message() ?: "Unknown error")
        }
    }
}
