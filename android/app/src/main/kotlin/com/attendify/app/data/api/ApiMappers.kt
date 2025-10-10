package com.attendify.app.data.api

import com.attendify.app.data.model.*

/**
 * Extension functions to convert between API DTOs and domain models
 */

// User conversions
fun UserDto.toDomainModel(): User = User(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    profileImageUrl = profileImageUrl,
    role = role,
    studentId = studentId,
    department = department,
    biometricEnabled = false // This is local-only
)

fun User.toDto(): UserDto = UserDto(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    profileImageUrl = profileImageUrl,
    role = role,
    studentId = studentId,
    department = department
)

// Course conversions
fun CourseDto.toDomainModel(): Course = Course(
    id = id,
    code = code,
    name = name,
    description = description,
    instructorId = instructorId,
    semester = semester,
    year = year
)

fun Course.toDto(): CourseDto = CourseDto(
    id = id,
    code = code,
    name = name,
    description = description,
    instructorId = instructorId,
    semester = semester,
    year = year
)

fun Course.toCreateRequest(): CreateCourseRequest = CreateCourseRequest(
    code = code,
    name = name,
    description = description,
    instructorId = instructorId,
    semester = semester,
    year = year
)

// Session conversions
fun SessionDto.toDomainModel(): Session = Session(
    id = id,
    courseId = courseId,
    title = title,
    scheduledDate = scheduledDate,
    startTime = startTime,
    endTime = endTime,
    qrCode = qrCode,
    isActive = isActive
)

fun Session.toDto(): SessionDto = SessionDto(
    id = id,
    courseId = courseId,
    title = title,
    scheduledDate = scheduledDate,
    startTime = startTime,
    endTime = endTime,
    qrCode = qrCode,
    isActive = isActive
)

fun Session.toCreateRequest(): CreateSessionRequest = CreateSessionRequest(
    courseId = courseId,
    title = title,
    scheduledDate = scheduledDate,
    startTime = startTime,
    endTime = endTime
)

// Attendance conversions
fun AttendanceRecordDto.toDomainModel(): AttendanceRecord = AttendanceRecord(
    id = id,
    sessionId = sessionId,
    studentId = studentId,
    checkedInAt = checkedInAt,
    status = status
)

fun AttendanceRecord.toDto(): AttendanceRecordDto = AttendanceRecordDto(
    id = id,
    sessionId = sessionId,
    studentId = studentId,
    checkedInAt = checkedInAt,
    status = status
)

// Enrollment conversions
fun EnrollmentDto.toDomainModel(): Enrollment = Enrollment(
    id = id,
    courseId = courseId,
    studentId = studentId
)

fun Enrollment.toDto(): EnrollmentDto = EnrollmentDto(
    id = id,
    courseId = courseId,
    studentId = studentId
)

fun Enrollment.toCreateRequest(): CreateEnrollmentRequest = CreateEnrollmentRequest(
    courseId = courseId,
    studentId = studentId
)
