package com.attendify.app.data.api

import com.attendify.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service for Attendify backend
 * All endpoints match the Express.js API structure
 */
interface AttendifyApiService {
    
    // ============ Auth Endpoints ============
    
    @GET("auth/user")
    suspend fun getCurrentUser(): Response<User>
    
    // ============ User Endpoints ============
    
    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>
    
    @GET("users/students")
    suspend fun getAllStudents(): Response<List<User>>
    
    @GET("users/instructors")
    suspend fun getAllInstructors(): Response<List<User>>
    
    @PATCH("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: String,
        @Body request: UpdateUserRequest
    ): Response<User>
    
    // ============ Course Endpoints ============
    
    @GET("courses")
    suspend fun getAllCourses(): Response<List<Course>>
    
    @GET("courses/{id}")
    suspend fun getCourse(@Path("id") courseId: String): Response<Course>
    
    @POST("courses")
    suspend fun createCourse(@Body request: CreateCourseRequest): Response<Course>
    
    @GET("courses/instructor/{instructorId}")
    suspend fun getCoursesByInstructor(
        @Path("instructorId") instructorId: String
    ): Response<List<Course>>
    
    // ============ Enrollment Endpoints ============
    
    @GET("enrollments/course/{courseId}")
    suspend fun getEnrollmentsByCourse(
        @Path("courseId") courseId: String
    ): Response<List<Enrollment>>
    
    @GET("enrollments/student/{studentId}")
    suspend fun getEnrollmentsByStudent(
        @Path("studentId") studentId: String
    ): Response<List<Enrollment>>
    
    @POST("enrollments")
    suspend fun createEnrollment(
        @Body request: CreateEnrollmentRequest
    ): Response<Enrollment>
    
    @DELETE("enrollments/{id}")
    suspend fun deleteEnrollment(@Path("id") enrollmentId: String): Response<Unit>
    
    // ============ Session Endpoints ============
    
    @GET("sessions/course/{courseId}")
    suspend fun getSessionsByCourse(
        @Path("courseId") courseId: String
    ): Response<List<Session>>
    
    @GET("sessions/{id}")
    suspend fun getSession(@Path("id") sessionId: String): Response<Session>
    
    @POST("sessions")
    suspend fun createSession(@Body request: CreateSessionRequest): Response<Session>
    
    @PATCH("sessions/{id}")
    suspend fun updateSession(
        @Path("id") sessionId: String,
        @Body updates: Map<String, Any>
    ): Response<Session>
    
    @GET("sessions/{id}/qrcode")
    suspend fun getSessionQRCode(@Path("id") sessionId: String): Response<QRCodeResponse>
    
    // ============ Attendance Endpoints ============
    
    @POST("attendance/checkin")
    suspend fun checkIn(@Body request: CheckInRequest): Response<AttendanceRecord>
    
    @GET("attendance/session/{sessionId}")
    suspend fun getAttendanceBySession(
        @Path("sessionId") sessionId: String
    ): Response<List<AttendanceRecord>>
    
    @GET("attendance/student/{studentId}")
    suspend fun getAttendanceByStudent(
        @Path("studentId") studentId: String
    ): Response<List<AttendanceRecord>>
    
    @GET("attendance/course/{courseId}/student/{studentId}")
    suspend fun getAttendanceByCourseAndStudent(
        @Path("courseId") courseId: String,
        @Path("studentId") studentId: String
    ): Response<List<AttendanceRecord>>
}
