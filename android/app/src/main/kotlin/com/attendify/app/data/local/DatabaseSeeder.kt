package com.attendify.app.data.local

import com.attendify.app.data.local.entity.*
import java.util.UUID
import javax.inject.Inject

/**
 * Seeds the database with initial data if it is empty.
 */
class DatabaseSeeder @Inject constructor(private val database: AttendifyDatabase) {

    suspend fun seedDatabaseIfEmpty() {
        val userDao = database.userDao()
        if (userDao.getUserCount() == 0) {
            // Create admin, instructor, and student users
            val adminId = UUID.randomUUID().toString()
            val instructorId1 = UUID.randomUUID().toString()
            val instructorId2 = UUID.randomUUID().toString()
            val instructorId3 = UUID.randomUUID().toString()
            val studentId1 = UUID.randomUUID().toString()
            val studentId2 = UUID.randomUUID().toString()
            val studentId3 = UUID.randomUUID().toString()
            val studentId4 = UUID.randomUUID().toString()
            val studentId5 = UUID.randomUUID().toString()
            val studentId6 = UUID.randomUUID().toString()
            val studentId7 = UUID.randomUUID().toString()
            val studentId8 = UUID.randomUUID().toString()
            val studentId9 = UUID.randomUUID().toString()
            val studentId10 = UUID.randomUUID().toString()
            val studentId11 = UUID.randomUUID().toString()
            val studentId12 = UUID.randomUUID().toString()

            val users = listOf(
                UserEntity(
                    id = adminId,
                    username = "admin",
                    password = "admin123",
                    email = "admin@attendify.com",
                    firstName = "Admin",
                    lastName = "User",
                    profileImageUrl = null,
                    role = "admin",
                    studentId = null,
                    department = "IT"
                ),
                UserEntity(
                    id = instructorId1,
                    username = "instructor",
                    password = "instructor123",
                    email = "instructor@attendify.com",
                    firstName = "John",
                    lastName = "Doe",
                    profileImageUrl = null,
                    role = "instructor",
                    studentId = null,
                    department = "Computer Science"
                ),
                UserEntity(
                    id = instructorId2,
                    username = "jane.smith",
                    password = "instructor123",
                    email = "jane.smith@attendify.com",
                    firstName = "Jane",
                    lastName = "Smith",
                    profileImageUrl = null,
                    role = "instructor",
                    studentId = null,
                    department = "Mathematics"
                ),
                UserEntity(
                    id = instructorId3,
                    username = "robert.jones",
                    password = "instructor123",
                    email = "robert.jones@attendify.com",
                    firstName = "Robert",
                    lastName = "Jones",
                    profileImageUrl = null,
                    role = "instructor",
                    studentId = null,
                    department = "Physics"
                ),
                UserEntity(
                    id = studentId1,
                    username = "student",
                    password = "student123",
                    email = "student@student.attendify.com",
                    firstName = "Alice",
                    lastName = "Johnson",
                    profileImageUrl = null,
                    role = "student",
                    studentId = "STU001",
                    department = "Computer Science"
                ),
                UserEntity(
                    id = studentId2,
                    username = "bob.williams",
                    password = "student123",
                    email = "bob.williams@student.attendify.com",
                    firstName = "Bob",
                    lastName = "Williams",
                    profileImageUrl = null,
                    role = "student",
                    studentId = "STU002",
                    department = "Computer Science"
                ),
                UserEntity(
                    id = studentId3,
                    username = "carol.jones",
                    password = "student123",
                    email = "carol.jones@student.attendify.com",
                    firstName = "Carol",
                    lastName = "Jones",
                    profileImageUrl = null,
                    role = "student",
                    studentId = "STU003",
                    department = "Mathematics"
                ),
                UserEntity(
                    id = studentId4,
                    username = "david.brown",
                    password = "student123",
                    email = "david.brown@student.attendify.com",
                    firstName = "David",
                    lastName = "Brown",
                    profileImageUrl = null,
                    role = "student",
                    studentId = "STU004",
                    department = "Physics"
                ),
                UserEntity(
                    id = studentId5,
                    username = "emma.wilson",
                    password = "student123",
                    email = "emma.wilson@student.attendify.com",
                    firstName = "Emma",
                    lastName = "Wilson",
                    profileImageUrl = null,
                    role = "student",
                    studentId = "STU005",
                    department = "Computer Science"
                ),
                UserEntity(
                    id = studentId6,
                    username = "frank.taylor",
                    password = "student123",
                    email = "frank.taylor@student.attendify.com",
                    firstName = "Frank",
                    lastName = "Taylor",
                    profileImageUrl = null,
                    role = "student",
                    studentId = "STU006",
                    department = "Mathematics"
                ),
                UserEntity(
                    id = studentId7,
                    username = "grace.lee",
                    password = "student123",
                    email = "grace.lee@student.attendify.com",
                    firstName = "Grace",
                    lastName = "Lee",
                    profileImageUrl = null,
                    role = "student",
                    studentId = "STU007",
                    department = "Physics"
                ),
                UserEntity(
                    id = studentId8,
                    username = "henry.martin",
                    password = "student123",
                    email = "henry.martin@student.attendify.com",
                    firstName = "Henry",
                    lastName = "Martin",
                    profileImageUrl = null,
                    role = "student",
                    studentId = "STU008",
                    department = "Computer Science"
                ),
                UserEntity(
                    id = studentId9,
                    username = "isabel.garcia",
                    password = "student123",
                    email = "isabel.garcia@student.attendify.com",
                    firstName = "Isabel",
                    lastName = "Garcia",
                    profileImageUrl = null,
                    role = "student",
                    studentId = "STU009",
                    department = "Mathematics"
                ),
                UserEntity(
                    id = studentId10,
                    username = "jack.rodriguez",
                    password = "student123",
                    email = "jack.rodriguez@student.attendify.com",
                    firstName = "Jack",
                    lastName = "Rodriguez",
                    profileImageUrl = null,
                    role = "student",
                    studentId = "STU010",
                    department = "Physics"
                ),
                UserEntity(
                    id = studentId11,
                    username = "kate.anderson",
                    password = "student123",
                    email = "kate.anderson@student.attendify.com",
                    firstName = "Kate",
                    lastName = "Anderson",
                    profileImageUrl = null,
                    role = "student",
                    studentId = "STU011",
                    department = "Computer Science"
                ),
                UserEntity(
                    id = studentId12,
                    username = "liam.thomas",
                    password = "student123",
                    email = "liam.thomas@student.attendify.com",
                    firstName = "Liam",
                    lastName = "Thomas",
                    profileImageUrl = null,
                    role = "student",
                    studentId = "STU012",
                    department = "Mathematics"
                )
            )
            
            userDao.insertUsers(users)
            
            // Create sample courses across multiple departments
            val courseId1 = UUID.randomUUID().toString()
            val courseId2 = UUID.randomUUID().toString()
            val courseId3 = UUID.randomUUID().toString()
            val courseId4 = UUID.randomUUID().toString()
            val courseId5 = UUID.randomUUID().toString()
            val courseId6 = UUID.randomUUID().toString()
            val courseId7 = UUID.randomUUID().toString()
            val courseId8 = UUID.randomUUID().toString()
            val courseId9 = UUID.randomUUID().toString()
            val courseId10 = UUID.randomUUID().toString()
            val courseId11 = UUID.randomUUID().toString()
            val courseId12 = UUID.randomUUID().toString()
            
            val courses = listOf(
                // Computer Science courses
                CourseEntity(
                    id = courseId1,
                    code = "CS101",
                    name = "Introduction to Programming",
                    description = "Learn the fundamentals of programming using Python. Topics include variables, control structures, functions, and object-oriented programming basics.",
                    instructorId = instructorId1,
                    semester = "Fall",
                    year = 2024
                ),
                CourseEntity(
                    id = courseId2,
                    code = "CS201",
                    name = "Data Structures and Algorithms",
                    description = "Advanced programming concepts including arrays, linked lists, trees, graphs, sorting, and searching algorithms.",
                    instructorId = instructorId1,
                    semester = "Fall",
                    year = 2024
                ),
                CourseEntity(
                    id = courseId3,
                    code = "CS301",
                    name = "Database Systems",
                    description = "Design and implementation of relational databases, SQL, normalization, transactions, and database management.",
                    instructorId = instructorId1,
                    semester = "Spring",
                    year = 2025
                ),
                // Mathematics courses
                CourseEntity(
                    id = courseId4,
                    code = "MATH101",
                    name = "Calculus I",
                    description = "Limits, derivatives, applications of derivatives, and introduction to integration.",
                    instructorId = instructorId2,
                    semester = "Fall",
                    year = 2024
                ),
                CourseEntity(
                    id = courseId5,
                    code = "MATH201",
                    name = "Linear Algebra",
                    description = "Vector spaces, matrices, determinants, eigenvalues, and linear transformations.",
                    instructorId = instructorId2,
                    semester = "Fall",
                    year = 2024
                ),
                // Physics courses
                CourseEntity(
                    id = courseId6,
                    code = "PHYS101",
                    name = "Physics I: Mechanics",
                    description = "Kinematics, Newton's laws, work and energy, momentum, rotation, and gravitation.",
                    instructorId = instructorId3,
                    semester = "Fall",
                    year = 2024
                ),
                CourseEntity(
                    id = courseId7,
                    code = "PHYS201",
                    name = "Physics II: Electromagnetism",
                    description = "Electric fields, magnetic fields, circuits, electromagnetic induction, and waves.",
                    instructorId = instructorId3,
                    semester = "Spring",
                    year = 2025
                ),
                CourseEntity(
                    id = courseId8,
                    code = "CS401",
                    name = "Software Engineering",
                    description = "Software development lifecycle, design patterns, testing, agile methodologies, and project management.",
                    instructorId = instructorId1,
                    semester = "Fall",
                    year = 2024
                ),
                CourseEntity(
                    id = courseId9,
                    code = "MATH301",
                    name = "Differential Equations",
                    description = "First and second order differential equations, Laplace transforms, and applications to physics and engineering.",
                    instructorId = instructorId2,
                    semester = "Spring",
                    year = 2025
                ),
                CourseEntity(
                    id = courseId10,
                    code = "CS501",
                    name = "Machine Learning",
                    description = "Introduction to machine learning algorithms, supervised and unsupervised learning, neural networks.",
                    instructorId = instructorId1,
                    semester = "Spring",
                    year = 2025
                ),
                CourseEntity(
                    id = courseId11,
                    code = "PHYS301",
                    name = "Quantum Mechanics",
                    description = "Wave functions, Schrödinger equation, operators, quantum states, and applications.",
                    instructorId = instructorId3,
                    semester = "Fall",
                    year = 2024
                ),
                CourseEntity(
                    id = courseId12,
                    code = "MATH401",
                    name = "Abstract Algebra",
                    description = "Groups, rings, fields, and their applications in cryptography and coding theory.",
                    instructorId = instructorId2,
                    semester = "Fall",
                    year = 2024
                )
            )
            
            database.courseDao().insertCourses(courses)
            
            // Create sample enrollments - students enrolled in multiple courses
            val enrollments = mutableListOf<EnrollmentEntity>()
            
            // Alice (STU001) - CS major, enrolled in CS courses
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId1, studentId1))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId2, studentId1))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId8, studentId1))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId4, studentId1))
            
            // Bob (STU002) - CS major
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId1, studentId2))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId2, studentId2))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId5, studentId2))
            
            // Carol (STU003) - Math major
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId4, studentId3))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId5, studentId3))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId1, studentId3))
            
            // David (STU004) - Physics major
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId6, studentId4))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId4, studentId4))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId5, studentId4))
            
            // Emma (STU005) - CS major
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId1, studentId5))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId2, studentId5))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId8, studentId5))
            
            // Frank (STU006) - Math major
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId4, studentId6))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId5, studentId6))
            
            // Grace (STU007) - Physics major
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId6, studentId7))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId4, studentId7))
            
            // Henry (STU008) - CS major
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId1, studentId8))
            enrollments.add(EnrollmentEntity(UUID.randomUUID().toString(), courseId8, studentId8))
            
            database.enrollmentDao().insertEnrollments(enrollments)
            
            // Create sample sessions with realistic schedules
            val currentTime = System.currentTimeMillis()
            val oneDayMillis = 24 * 60 * 60 * 1000L
            
            val sessions = mutableListOf<SessionEntity>()
            
            // CS101 sessions
            sessions.add(SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId1,
                title = "Introduction to Python - Lecture 1",
                scheduledDate = currentTime + oneDayMillis,
                startTime = "09:00",
                endTime = "10:30",
                qrCode = "CS101-${UUID.randomUUID().toString().take(8)}",
                isActive = true
            ))
            sessions.add(SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId1,
                title = "Variables and Data Types",
                scheduledDate = currentTime + (3 * oneDayMillis),
                startTime = "09:00",
                endTime = "10:30",
                qrCode = "CS101-${UUID.randomUUID().toString().take(8)}",
                isActive = false
            ))
            sessions.add(SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId1,
                title = "Control Flow and Loops",
                scheduledDate = currentTime + (5 * oneDayMillis),
                startTime = "09:00",
                endTime = "10:30",
                qrCode = "CS101-${UUID.randomUUID().toString().take(8)}",
                isActive = false
            ))
            
            // CS201 sessions
            sessions.add(SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId2,
                title = "Arrays and Linked Lists",
                scheduledDate = currentTime + oneDayMillis,
                startTime = "11:00",
                endTime = "12:30",
                qrCode = "CS201-${UUID.randomUUID().toString().take(8)}",
                isActive = true
            ))
            sessions.add(SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId2,
                title = "Stacks and Queues",
                scheduledDate = currentTime + (2 * oneDayMillis),
                startTime = "11:00",
                endTime = "12:30",
                qrCode = "CS201-${UUID.randomUUID().toString().take(8)}",
                isActive = true
            ))
            
            // MATH101 sessions
            sessions.add(SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId4,
                title = "Limits and Continuity",
                scheduledDate = currentTime + oneDayMillis,
                startTime = "13:00",
                endTime = "14:30",
                qrCode = "MATH101-${UUID.randomUUID().toString().take(8)}",
                isActive = true
            ))
            sessions.add(SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId4,
                title = "Derivatives - Part 1",
                scheduledDate = currentTime + (4 * oneDayMillis),
                startTime = "13:00",
                endTime = "14:30",
                qrCode = "MATH101-${UUID.randomUUID().toString().take(8)}",
                isActive = false
            ))
            
            // MATH201 sessions
            sessions.add(SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId5,
                title = "Vector Spaces Introduction",
                scheduledDate = currentTime + (2 * oneDayMillis),
                startTime = "15:00",
                endTime = "16:30",
                qrCode = "MATH201-${UUID.randomUUID().toString().take(8)}",
                isActive = true
            ))
            
            // PHYS101 sessions
            sessions.add(SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId6,
                title = "Kinematics in One Dimension",
                scheduledDate = currentTime + oneDayMillis,
                startTime = "10:00",
                endTime = "11:30",
                qrCode = "PHYS101-${UUID.randomUUID().toString().take(8)}",
                isActive = true
            ))
            sessions.add(SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId6,
                title = "Newton's Laws of Motion",
                scheduledDate = currentTime + (3 * oneDayMillis),
                startTime = "10:00",
                endTime = "11:30",
                qrCode = "PHYS101-${UUID.randomUUID().toString().take(8)}",
                isActive = false
            ))
            
            // CS401 sessions
            sessions.add(SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId8,
                title = "Software Development Lifecycle",
                scheduledDate = currentTime + (2 * oneDayMillis),
                startTime = "14:00",
                endTime = "16:00",
                qrCode = "CS401-${UUID.randomUUID().toString().take(8)}",
                isActive = true
            ))
            
            database.sessionDao().insertSessions(sessions)
            
            // Create sample attendance records for past sessions
            val attendanceRecords = mutableListOf<AttendanceRecordEntity>()
            val sessionId1 = sessions[0].id
            val sessionId2 = sessions[3].id
            
            // Attendance for CS101 first session
            attendanceRecords.add(AttendanceRecordEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId1,
                studentId = studentId1,
                checkedInAt = currentTime - (2 * oneDayMillis),
                status = "present"
            ))
            attendanceRecords.add(AttendanceRecordEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId1,
                studentId = studentId2,
                checkedInAt = currentTime - (2 * oneDayMillis),
                status = "late"
            ))
            attendanceRecords.add(AttendanceRecordEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId1,
                studentId = studentId5,
                checkedInAt = currentTime - (2 * oneDayMillis),
                status = "present"
            ))
            attendanceRecords.add(AttendanceRecordEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId1,
                studentId = studentId8,
                checkedInAt = currentTime - (2 * oneDayMillis),
                status = "present"
            ))
            
            // Attendance for CS201 session
            attendanceRecords.add(AttendanceRecordEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId2,
                studentId = studentId1,
                checkedInAt = currentTime - oneDayMillis,
                status = "present"
            ))
            attendanceRecords.add(AttendanceRecordEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId2,
                studentId = studentId2,
                checkedInAt = currentTime - oneDayMillis,
                status = "present"
            ))
            attendanceRecords.add(AttendanceRecordEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId2,
                studentId = studentId5,
                checkedInAt = currentTime - oneDayMillis,
                status = "late"
            ))
            
            database.attendanceRecordDao().insertAttendanceRecords(attendanceRecords)
        }
    }
}
