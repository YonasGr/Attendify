package com.attendify.app.data.local

import com.attendify.app.data.local.entity.*
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds the database with initial data on first launch
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    private val database: AttendifyDatabase
) {
    
    suspend fun seedDatabaseIfEmpty() {
        // Check if database is already seeded
        val userDao = database.userDao()
        val existingUser = userDao.getUserByUsername("admin")
        
        if (existingUser != null) {
            return // Database already seeded
        }
        
        // Create default users
        val adminId = UUID.randomUUID().toString()
        val instructorId = UUID.randomUUID().toString()
        val studentId = UUID.randomUUID().toString()
        
        val users = listOf(
            UserEntity(
                id = adminId,
                username = "admin",
                password = "admin123", // In production, use hashed passwords
                email = "admin@attendify.com",
                firstName = "Admin",
                lastName = "User",
                profileImageUrl = null,
                role = "admin",
                studentId = null,
                department = "Administration"
            ),
            UserEntity(
                id = instructorId,
                username = "instructor",
                password = "instructor123",
                email = "instructor@attendify.com",
                firstName = "John",
                lastName = "Smith",
                profileImageUrl = null,
                role = "instructor",
                studentId = null,
                department = "Computer Science"
            ),
            UserEntity(
                id = studentId,
                username = "student",
                password = "student123",
                email = "student@attendify.com",
                firstName = "Alice",
                lastName = "Johnson",
                profileImageUrl = null,
                role = "student",
                studentId = "STU001",
                department = "Computer Science"
            )
        )
        
        userDao.insertUsers(users)
        
        // Create sample courses
        val courseId1 = UUID.randomUUID().toString()
        val courseId2 = UUID.randomUUID().toString()
        
        val courses = listOf(
            CourseEntity(
                id = courseId1,
                code = "CS101",
                name = "Introduction to Programming",
                description = "Learn the fundamentals of programming using Python",
                instructorId = instructorId,
                semester = "Fall",
                year = 2024
            ),
            CourseEntity(
                id = courseId2,
                code = "CS201",
                name = "Data Structures and Algorithms",
                description = "Advanced programming concepts and algorithms",
                instructorId = instructorId,
                semester = "Fall",
                year = 2024
            )
        )
        
        database.courseDao().insertCourses(courses)
        
        // Create sample enrollments
        val enrollments = listOf(
            EnrollmentEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId1,
                studentId = studentId
            ),
            EnrollmentEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId2,
                studentId = studentId
            )
        )
        
        database.enrollmentDao().insertEnrollments(enrollments)
        
        // Create sample sessions
        val currentTime = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        
        val sessions = listOf(
            SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId1,
                title = "Introduction to Python",
                scheduledDate = currentTime + oneDayMillis,
                startTime = "09:00",
                endTime = "10:30",
                qrCode = "CS101-${UUID.randomUUID().toString().take(8)}",
                isActive = true
            ),
            SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId1,
                title = "Variables and Data Types",
                scheduledDate = currentTime + (2 * oneDayMillis),
                startTime = "09:00",
                endTime = "10:30",
                qrCode = "CS101-${UUID.randomUUID().toString().take(8)}",
                isActive = false
            ),
            SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId2,
                title = "Arrays and Linked Lists",
                scheduledDate = currentTime + oneDayMillis,
                startTime = "11:00",
                endTime = "12:30",
                qrCode = "CS201-${UUID.randomUUID().toString().take(8)}",
                isActive = true
            )
        )
        
        database.sessionDao().insertSessions(sessions)
    }
}
