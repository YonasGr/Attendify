package com.attendify.app.data.api

import com.google.gson.annotations.SerializedName

/**
 * API request and response models for backend communication
 */

// Authentication
data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: LoginData?
)

data class LoginData(
    @SerializedName("user") val user: UserDto,
    @SerializedName("token") val token: String
)

data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("email") val email: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("role") val role: String,
    @SerializedName("studentId") val studentId: String?,
    @SerializedName("department") val department: String?
)

data class RegisterResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: UserDto?
)

// User DTOs
data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    @SerializedName("role") val role: String,
    @SerializedName("studentId") val studentId: String?,
    @SerializedName("department") val department: String?
)

// Course DTOs
data class CourseDto(
    @SerializedName("id") val id: String,
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("instructorId") val instructorId: String,
    @SerializedName("semester") val semester: String,
    @SerializedName("year") val year: Int
)

data class CreateCourseRequest(
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("instructorId") val instructorId: String,
    @SerializedName("semester") val semester: String,
    @SerializedName("year") val year: Int
)

// Session DTOs
data class SessionDto(
    @SerializedName("id") val id: String,
    @SerializedName("courseId") val courseId: String,
    @SerializedName("title") val title: String,
    @SerializedName("scheduledDate") val scheduledDate: Long,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("qrCode") val qrCode: String,
    @SerializedName("isActive") val isActive: Boolean
)

data class CreateSessionRequest(
    @SerializedName("courseId") val courseId: String,
    @SerializedName("title") val title: String,
    @SerializedName("scheduledDate") val scheduledDate: Long,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String
)

// Attendance DTOs
data class AttendanceRecordDto(
    @SerializedName("id") val id: String,
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("studentId") val studentId: String,
    @SerializedName("checkedInAt") val checkedInAt: Long,
    @SerializedName("status") val status: String
)

data class MarkAttendanceRequest(
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("studentId") val studentId: String,
    @SerializedName("qrCode") val qrCode: String?
)

// Enrollment DTOs
data class EnrollmentDto(
    @SerializedName("id") val id: String,
    @SerializedName("courseId") val courseId: String,
    @SerializedName("studentId") val studentId: String
)

data class CreateEnrollmentRequest(
    @SerializedName("courseId") val courseId: String,
    @SerializedName("studentId") val studentId: String
)

// Generic API response wrapper
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?
)

// Health check
data class HealthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String
)

// Sync models
data class SyncUploadRequest(
    @SerializedName("users") val users: List<UserDto>?,
    @SerializedName("courses") val courses: List<CourseDto>?,
    @SerializedName("sessions") val sessions: List<SessionDto>?,
    @SerializedName("enrollments") val enrollments: List<EnrollmentDto>?,
    @SerializedName("attendance") val attendance: List<AttendanceRecordDto>?
)

data class SyncDownloadResponse(
    @SerializedName("users") val users: List<UserDto>,
    @SerializedName("courses") val courses: List<CourseDto>,
    @SerializedName("sessions") val sessions: List<SessionDto>,
    @SerializedName("enrollments") val enrollments: List<EnrollmentDto>,
    @SerializedName("attendance") val attendance: List<AttendanceRecordDto>
)

data class SyncStatusResponse(
    @SerializedName("lastSyncTime") val lastSyncTime: Long?,
    @SerializedName("pendingChanges") val pendingChanges: Int
)

// Admin models
data class SystemStats(
    @SerializedName("userCount") val userCount: Int,
    @SerializedName("courseCount") val courseCount: Int,
    @SerializedName("sessionCount") val sessionCount: Int,
    @SerializedName("attendanceCount") val attendanceCount: Int
)
