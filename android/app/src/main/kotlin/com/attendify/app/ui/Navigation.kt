package com.attendify.app.ui

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object StudentDashboard : Screen("student_dashboard")
    object InstructorDashboard : Screen("instructor_dashboard")
    object AdminDashboard : Screen("admin_dashboard")
    object QRScanner : Screen("qr_scanner")
    object StudentCourses : Screen("student_courses")
    object AttendanceHistory : Screen("attendance_history")
    object InstructorCourses : Screen("instructor_courses")
    object CreateCourse : Screen("create_course")
    object Sessions : Screen("sessions")
    object CreateSession : Screen("create_session")
    object UserManagement : Screen("user_management")
    object CreateUser : Screen("create_user")
    object QRCodeDisplay : Screen("qr_code_display/{sessionId}/{sessionTitle}/{qrCode}") {
        fun createRoute(sessionId: String, sessionTitle: String, qrCode: String) = 
            "qr_code_display/$sessionId/$sessionTitle/$qrCode"
    }
    object CourseDetails : Screen("course_details/{courseId}") {
        fun createRoute(courseId: String) = "course_details/$courseId"
    }
    object SessionDetails : Screen("session_details/{sessionId}") {
        fun createRoute(sessionId: String) = "session_details/$sessionId"
    }
}
