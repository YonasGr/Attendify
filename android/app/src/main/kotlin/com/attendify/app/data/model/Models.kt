package com.attendify.app.data.model

import com.attendify.app.data.local.entity.UserEntity

data class User(
    val id: String,
    val username: String,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val profileImageUrl: String?,
    val role: String,
    val studentId: String?,
    val department: String?,
    var biometricEnabled: Boolean = false
)

data class Course(
    val id: String,
    val code: String,
    val name: String,
    val description: String?,
    val instructorId: String,
    val semester: String,
    val year: Int
)

data class Session(
    val id: String,
    val courseId: String,
    val title: String,
    val scheduledDate: Long,
    val startTime: String,
    val endTime: String,
    val qrCode: String,
    val isActive: Boolean,
    val attendees: List<String> = emptyList()
)

data class AttendanceRecord(
    val id: String,
    val sessionId: String,
    val studentId: String,
    val checkedInAt: Long,
    val status: String
)

data class Enrollment(
    val id: String,
    val courseId: String,
    val studentId: String
)
