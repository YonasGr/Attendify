package com.attendify.app.data.model

/**
 * User model for UI layer
 */
data class User(
    val id: String,
    val username: String,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val profileImageUrl: String?,
    val role: String, // "student", "instructor", "admin"
    val studentId: String?,
    val department: String?,
    val biometricEnabled: Boolean = false
)

/**
 * Course model for UI layer
 */
data class Course(
    val id: String,
    val code: String,
    val name: String,
    val description: String?,
    val instructorId: String,
    val semester: String,
    val year: Int
)

/**
 * Session model for UI layer
 */
data class Session(
    val id: String,
    val courseId: String,
    val title: String,
    val scheduledDate: Long,
    val startTime: String,
    val endTime: String,
    val qrCode: String,
    val isActive: Boolean
)

/**
 * Attendance record model for UI layer
 */
data class AttendanceRecord(
    val id: String,
    val sessionId: String,
    val studentId: String,
    val checkedInAt: Long,
    val status: String // "present", "late", "absent"
)

/**
 * Enrollment model for UI layer
 */
data class Enrollment(
    val id: String,
    val courseId: String,
    val studentId: String
)
