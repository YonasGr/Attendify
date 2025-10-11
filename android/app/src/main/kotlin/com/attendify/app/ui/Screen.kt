package com.attendify.app.ui

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object StudentDashboard : Screen("student_dashboard")
    object InstructorDashboard : Screen("instructor_dashboard")
    object AdminDashboard : Screen("admin_dashboard")
    object Settings : Screen("settings")
    object QRScanner : Screen("qr_scanner")
    object StudentCourses : Screen("student_courses")
    object UpcomingSessions : Screen("upcoming_sessions")
    object AttendanceHistory : Screen("attendance_history")
    object InstructorCourses : Screen("instructor_courses")
    object CreateCourse : Screen("create_course")
    object Sessions : Screen("sessions")
    object CreateSession : Screen("create_session")
    object UserManagement : Screen("user_management")
    object CreateUser : Screen("create_user")
    object EnrollmentManagement : Screen("enrollment_management")
}
