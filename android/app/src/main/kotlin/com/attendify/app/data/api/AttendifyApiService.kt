package com.attendify.app.data.api

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service for Attendify backend
 */
interface AttendifyApiService {
    
    // Health Check
    @GET("health")
    suspend fun health(): Response<HealthResponse>
    
    // Authentication
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: Map<String, String>): Response<ApiResponse<LoginData>>
    
    // Users
    @GET("users")
    suspend fun getAllUsers(): Response<ApiResponse<List<UserDto>>>
    
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<ApiResponse<UserDto>>
    
    @GET("users/role/{role}")
    suspend fun getUsersByRole(@Path("role") role: String): Response<ApiResponse<List<UserDto>>>
    
    @POST("users")
    suspend fun createUser(@Body request: RegisterRequest): Response<ApiResponse<UserDto>>
    
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: UserDto): Response<ApiResponse<UserDto>>
    
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<ApiResponse<Unit>>
    
    // Courses
    @GET("courses")
    suspend fun getAllCourses(): Response<ApiResponse<List<CourseDto>>>
    
    @GET("courses/{id}")
    suspend fun getCourseById(@Path("id") id: String): Response<ApiResponse<CourseDto>>
    
    @GET("courses/instructor/{instructorId}")
    suspend fun getCoursesByInstructor(@Path("instructorId") instructorId: String): Response<ApiResponse<List<CourseDto>>>
    
    @GET("courses/student/{studentId}")
    suspend fun getCoursesByStudent(@Path("studentId") studentId: String): Response<ApiResponse<List<CourseDto>>>
    
    @POST("courses")
    suspend fun createCourse(@Body request: CreateCourseRequest): Response<ApiResponse<CourseDto>>
    
    @PUT("courses/{id}")
    suspend fun updateCourse(@Path("id") id: String, @Body course: CourseDto): Response<ApiResponse<CourseDto>>
    
    @DELETE("courses/{id}")
    suspend fun deleteCourse(@Path("id") id: String): Response<ApiResponse<Unit>>
    
    // Sessions
    @GET("sessions")
    suspend fun getAllSessions(): Response<ApiResponse<List<SessionDto>>>
    
    @GET("sessions/{id}")
    suspend fun getSessionById(@Path("id") id: String): Response<ApiResponse<SessionDto>>
    
    @GET("sessions/course/{courseId}")
    suspend fun getSessionsByCourse(@Path("courseId") courseId: String): Response<ApiResponse<List<SessionDto>>>
    
    @GET("sessions/active")
    suspend fun getActiveSessions(): Response<ApiResponse<List<SessionDto>>>
    
    @POST("sessions")
    suspend fun createSession(@Body request: CreateSessionRequest): Response<ApiResponse<SessionDto>>
    
    @PUT("sessions/{id}")
    suspend fun updateSession(@Path("id") id: String, @Body session: SessionDto): Response<ApiResponse<SessionDto>>
    
    @PUT("sessions/{id}/activate")
    suspend fun activateSession(@Path("id") id: String): Response<ApiResponse<SessionDto>>
    
    @PUT("sessions/{id}/deactivate")
    suspend fun deactivateSession(@Path("id") id: String): Response<ApiResponse<SessionDto>>
    
    @DELETE("sessions/{id}")
    suspend fun deleteSession(@Path("id") id: String): Response<ApiResponse<Unit>>
    
    // Enrollments
    @GET("enrollments")
    suspend fun getAllEnrollments(): Response<ApiResponse<List<EnrollmentDto>>>
    
    @GET("enrollments/course/{courseId}")
    suspend fun getEnrollmentsByCourse(@Path("courseId") courseId: String): Response<ApiResponse<List<EnrollmentDto>>>
    
    @GET("enrollments/student/{studentId}")
    suspend fun getEnrollmentsByStudent(@Path("studentId") studentId: String): Response<ApiResponse<List<EnrollmentDto>>>
    
    @POST("enrollments")
    suspend fun createEnrollment(@Body request: CreateEnrollmentRequest): Response<ApiResponse<EnrollmentDto>>
    
    @DELETE("enrollments/{id}")
    suspend fun deleteEnrollment(@Path("id") id: String): Response<ApiResponse<Unit>>
    
    // Attendance
    @GET("attendance")
    suspend fun getAllAttendance(): Response<ApiResponse<List<AttendanceRecordDto>>>
    
    @GET("attendance/session/{sessionId}")
    suspend fun getAttendanceBySession(@Path("sessionId") sessionId: String): Response<ApiResponse<List<AttendanceRecordDto>>>
    
    @GET("attendance/student/{studentId}")
    suspend fun getAttendanceByStudent(@Path("studentId") studentId: String): Response<ApiResponse<List<AttendanceRecordDto>>>
    
    @POST("attendance")
    suspend fun markAttendance(@Body request: MarkAttendanceRequest): Response<ApiResponse<AttendanceRecordDto>>
    
    @DELETE("attendance/{id}")
    suspend fun deleteAttendance(@Path("id") id: String): Response<ApiResponse<Unit>>
    
    // Sync
    @POST("sync/upload")
    suspend fun syncUpload(@Body data: SyncUploadRequest): Response<ApiResponse<String>>
    
    @GET("sync/download")
    suspend fun syncDownload(): Response<ApiResponse<SyncDownloadResponse>>
    
    @GET("sync/status")
    suspend fun syncStatus(): Response<ApiResponse<SyncStatusResponse>>
}
