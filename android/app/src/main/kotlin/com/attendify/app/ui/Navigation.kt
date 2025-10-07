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
    object CourseDetails : Screen("course_details/{courseId}") {
        fun createRoute(courseId: String) = "course_details/$courseId"
    }
    object SessionDetails : Screen("session_details/{sessionId}") {
        fun createRoute(sessionId: String) = "session_details/$sessionId"
    }
}
