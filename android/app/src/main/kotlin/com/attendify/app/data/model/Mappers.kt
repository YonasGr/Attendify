package com.attendify.app.data.model

import com.attendify.app.data.local.entity.*

/**
 * Extension functions to map between Room entities and UI models
 */

// User mappings
fun UserEntity.toModel(): User = User(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    profileImageUrl = profileImageUrl,
    role = role,
    studentId = studentId,
    department = department,
    biometricEnabled = biometricEnabled
)

fun User.toEntity(password: String): UserEntity = UserEntity(
    id = id,
    username = username,
    password = password,
    email = email,
    firstName = firstName,
    lastName = lastName,
    profileImageUrl = profileImageUrl,
    role = role,
    studentId = studentId,
    department = department,
    biometricEnabled = biometricEnabled
)

// Course mappings
fun CourseEntity.toModel(): Course = Course(
    id = id,
    code = code,
    name = name,
    description = description,
    instructorId = instructorId,
    semester = semester,
    year = year
)

fun Course.toEntity(): CourseEntity = CourseEntity(
    id = id,
    code = code,
    name = name,
    description = description,
    instructorId = instructorId,
    semester = semester,
    year = year
)

// Session mappings
fun SessionEntity.toModel(): Session = Session(
    id = id,
    courseId = courseId,
    title = title,
    scheduledDate = scheduledDate,
    startTime = startTime,
    endTime = endTime,
    qrCode = qrCode,
    isActive = isActive
)

fun Session.toEntity(): SessionEntity = SessionEntity(
    id = id,
    courseId = courseId,
    title = title,
    scheduledDate = scheduledDate,
    startTime = startTime,
    endTime = endTime,
    qrCode = qrCode,
    isActive = isActive
)

// AttendanceRecord mappings
fun AttendanceRecordEntity.toModel(): AttendanceRecord = AttendanceRecord(
    id = id,
    sessionId = sessionId,
    studentId = studentId,
    checkedInAt = checkedInAt,
    status = status
)

fun AttendanceRecord.toEntity(): AttendanceRecordEntity = AttendanceRecordEntity(
    id = id,
    sessionId = sessionId,
    studentId = studentId,
    checkedInAt = checkedInAt,
    status = status
)

// Enrollment mappings
fun EnrollmentEntity.toModel(): Enrollment = Enrollment(
    id = id,
    courseId = courseId,
    studentId = studentId
)

fun Enrollment.toEntity(): EnrollmentEntity = EnrollmentEntity(
    id = id,
    courseId = courseId,
    studentId = studentId
)
