package com.attendify.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * User model matching the backend schema
 */
data class User(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("email")
    val email: String?,
    
    @SerializedName("firstName")
    val firstName: String?,
    
    @SerializedName("lastName")
    val lastName: String?,
    
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?,
    
    @SerializedName("role")
    val role: String, // "student", "instructor", "admin"
    
    @SerializedName("studentId")
    val studentId: String?,
    
    @SerializedName("department")
    val department: String?,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?
)

/**
 * Course model
 */
data class Course(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("code")
    val code: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("instructorId")
    val instructorId: String,
    
    @SerializedName("semester")
    val semester: String,
    
    @SerializedName("year")
    val year: Int
)

/**
 * Session model
 */
data class Session(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("courseId")
    val courseId: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("scheduledDate")
    val scheduledDate: String,
    
    @SerializedName("startTime")
    val startTime: String,
    
    @SerializedName("endTime")
    val endTime: String,
    
    @SerializedName("qrCode")
    val qrCode: String,
    
    @SerializedName("isActive")
    val isActive: Boolean
)

/**
 * Attendance record model
 */
data class AttendanceRecord(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("sessionId")
    val sessionId: String,
    
    @SerializedName("studentId")
    val studentId: String,
    
    @SerializedName("checkedInAt")
    val checkedInAt: String,
    
    @SerializedName("status")
    val status: String // "present", "late", "absent"
)

/**
 * Enrollment model
 */
data class Enrollment(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("courseId")
    val courseId: String,
    
    @SerializedName("studentId")
    val studentId: String
)

/**
 * QR Code response
 */
data class QRCodeResponse(
    @SerializedName("qrCode")
    val qrCode: String,
    
    @SerializedName("qrCodeUrl")
    val qrCodeUrl: String
)

/**
 * Generic API error response
 */
data class ApiError(
    @SerializedName("message")
    val message: String
)

/**
 * Request models for API calls
 */
data class CheckInRequest(
    @SerializedName("qrCode")
    val qrCode: String
)

data class UpdateUserRequest(
    @SerializedName("role")
    val role: String? = null,
    
    @SerializedName("studentId")
    val studentId: String? = null,
    
    @SerializedName("department")
    val department: String? = null
)

data class CreateCourseRequest(
    @SerializedName("code")
    val code: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("instructorId")
    val instructorId: String,
    
    @SerializedName("semester")
    val semester: String,
    
    @SerializedName("year")
    val year: Int
)

data class CreateSessionRequest(
    @SerializedName("courseId")
    val courseId: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("scheduledDate")
    val scheduledDate: String,
    
    @SerializedName("startTime")
    val startTime: String,
    
    @SerializedName("endTime")
    val endTime: String
)

data class CreateEnrollmentRequest(
    @SerializedName("courseId")
    val courseId: String,
    
    @SerializedName("studentId")
    val studentId: String
)
