package com.attendify.app.data.api

import com.attendify.app.data.model.*

/**
 * Mappers for converting API DTOs to domain models
 */

fun UserDto.toModel(): User {
    return User(
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
}

fun CourseDto.toModel(): Course {
    return Course(
        id = id,
        code = code,
        name = name,
        description = description,
        instructorId = instructorId,
        semester = semester,
        year = year
    )
}

fun SessionDto.toModel(): Session {
    return Session(
        id = id,
        courseId = courseId,
        title = title,
        scheduledDate = scheduledDate,
        startTime = startTime,
        endTime = endTime,
        qrCode = qrCode,
        isActive = isActive
    )
}

fun AttendanceRecordDto.toModel(): AttendanceRecord {
    return AttendanceRecord(
        id = id,
        sessionId = sessionId,
        studentId = studentId,
        checkedInAt = checkedInAt,
        status = status
    )
}

fun EnrollmentDto.toModel(): Enrollment {
    return Enrollment(
        id = id,
        courseId = courseId,
        studentId = studentId
    )
}

fun UserDto.toDomainModel(): User = toModel()

fun CourseDto.toDomainModel(): Course = toModel()

fun SessionDto.toDomainModel(): Session = toModel()

fun AttendanceRecordDto.toDomainModel(): AttendanceRecord = toModel()

fun EnrollmentDto.toDomainModel(): Enrollment = toModel()

// Mappers from domain models to DTOs
fun Course.toCreateRequest(): CreateCourseRequest {
    return CreateCourseRequest(
        code = code,
        name = name,
        description = description,
        instructorId = instructorId,
        semester = semester,
        year = year
    )
}

fun Session.toCreateRequest(): CreateSessionRequest {
    return CreateSessionRequest(
        courseId = courseId,
        title = title,
        scheduledDate = scheduledDate,
        startTime = startTime,
        endTime = endTime
    )
}

// Mappers from domain models to DTOs for sync
fun User.toDto(): UserDto {
    return UserDto(
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
}

fun Course.toDto(): CourseDto {
    return CourseDto(
        id = id,
        code = code,
        name = name,
        description = description,
        instructorId = instructorId,
        semester = semester,
        year = year
    )
}

fun Session.toDto(): SessionDto {
    return SessionDto(
        id = id,
        courseId = courseId,
        title = title,
        scheduledDate = scheduledDate,
        startTime = startTime,
        endTime = endTime,
        qrCode = qrCode,
        isActive = isActive
    )
}

fun Enrollment.toDto(): EnrollmentDto {
    return EnrollmentDto(
        id = id,
        courseId = courseId,
        studentId = studentId
    )
}

fun AttendanceRecord.toDto(): AttendanceRecordDto {
    return AttendanceRecordDto(
        id = id,
        sessionId = sessionId,
        studentId = studentId,
        checkedInAt = checkedInAt,
        status = status
    )
}
