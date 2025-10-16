# Changelog

All notable changes to the Attendify project.

## [Unreleased] - 2025-10-16

### Backend API - Complete Implementation

#### Added
- **Complete Database Schema**
  - Users table with role validation (student, instructor, admin)
  - Courses table with instructor foreign key
  - Sessions table with QR code support and active status
  - Enrollments table with unique student-course constraint
  - Attendance records table with status tracking
  - Proper foreign key constraints with cascading deletes
  - Created/updated timestamp tracking on all tables

- **Authentication System**
  - JWT-based authentication with configurable expiration (default 7 days)
  - bcrypt password hashing with 10 salt rounds
  - User registration with validation and duplicate checking
  - User login with username/password verification
  - Token refresh endpoint for extended sessions
  - Secure token storage and transmission

- **Authorization Middleware**
  - JWT token verification middleware
  - Role-based authorization middleware
  - Granular access control per endpoint
  - User self-access vs admin access control

- **Validation & Security**
  - Input validation middleware
  - Input sanitization to prevent injection attacks
  - UUID validation for ID parameters
  - Email format validation
  - Role value validation
  - Required fields validation
  - Request body trimming and cleaning

- **Users API - Complete CRUD**
  - GET /api/users - List all users (admin only) with role filtering
  - GET /api/users/:id - Get user by ID (self or admin)
  - POST /api/users - Create user (admin only)
  - PUT /api/users/:id - Update user profile (self or admin)
  - DELETE /api/users/:id - Delete user (admin only)
  - Password hashing on creation and updates
  - Prevents duplicate usernames

- **Courses API - Complete CRUD**
  - GET /api/courses - List courses (filtered by role and enrollment)
  - GET /api/courses/:id - Get course details with access checks
  - POST /api/courses - Create course (instructor/admin)
  - PUT /api/courses/:id - Update course (owner/admin)
  - DELETE /api/courses/:id - Delete course (owner/admin)
  - Instructor validation and assignment
  - Duplicate code prevention
  - Year validation (2000-2100)
  - Includes instructor details in responses

- **Sessions API - Complete CRUD**
  - GET /api/sessions - List sessions (filtered by role and course)
  - GET /api/sessions/:id - Get session details with access checks
  - POST /api/sessions - Create session (instructor/admin)
  - PUT /api/sessions/:id - Update session (owner/admin)
  - DELETE /api/sessions/:id - Delete session (owner/admin)
  - Course ownership validation
  - QR code support
  - Active/inactive status tracking
  - Date and time validation

- **Enrollments API**
  - GET /api/enrollments - List enrollments (filtered by role)
  - POST /api/enrollments - Enroll student in course (instructor/admin)
  - DELETE /api/enrollments/:id - Remove enrollment (instructor/admin)
  - Student role validation
  - Course existence validation
  - Duplicate enrollment prevention
  - Course ownership checks for instructors

- **Attendance API**
  - GET /api/attendance - List all attendance records (role-filtered)
  - GET /api/attendance/session/:sessionId - Get session attendance (instructor/admin)
  - GET /api/attendance/student/:studentId - Get student attendance (self/admin)
  - POST /api/attendance - Mark attendance (student/instructor/admin)
  - DELETE /api/attendance/:id - Remove attendance record (instructor/admin)
  - Enrollment validation (student must be enrolled)
  - Status validation (present, late, absent)
  - Duplicate attendance prevention
  - Session existence validation

- **Error Handling**
  - Comprehensive error handling at all endpoints
  - Proper HTTP status codes:
    - 200 OK for successful GET/PUT/DELETE
    - 201 Created for successful POST
    - 400 Bad Request for validation errors
    - 401 Unauthorized for missing authentication
    - 403 Forbidden for insufficient permissions
    - 404 Not Found for missing resources
    - 409 Conflict for duplicate entries
    - 500 Internal Server Error for unexpected errors
  - Detailed error messages
  - Error logging to console
  - Consistent error response format

#### Documentation

- **README.md Updates**
  - Documented backend improvements and implementation status
  - Added comprehensive troubleshooting section
  - Listed all fixed issues (course creation, missing lists, panels, etc.)
  - Updated network features with detailed explanations
  - Added backend API issues resolution guide
  - Included authentication and database troubleshooting

- **Server README.md**
  - Complete API documentation with examples
  - Endpoint descriptions with parameters
  - Authentication and authorization requirements
  - Request/response examples with curl
  - Error response formats
  - Query parameter documentation
  - Validation requirements per endpoint
  - Updated tech stack and features list

- **SETUP_GUIDE.md**
  - Comprehensive local setup instructions
  - PostgreSQL installation for multiple platforms
  - Docker alternative for database
  - Environment configuration guide
  - JWT secret generation instructions
  - Manual API testing examples
  - Common issues and solutions
  - Development tips and debugging
  - Postman collection instructions
  - Production deployment guide
  - Security checklist

- **API Test Script**
  - Automated test script (test-api.sh)
  - Health check validation
  - Authentication flow testing
  - Registration and login tests
  - Protected route verification
  - Color-coded output
  - Pass/fail reporting

- **PROJECT_STATUS.md Updates**
  - Updated with backend implementation status
  - Listed all completed backend features
  - Documented fixed issues
  - Updated development status sections

#### Fixed

- **Course Creation Errors (400/500)**
  - Implemented complete validation for all required fields
  - Added proper error messages for missing/invalid data
  - Implemented duplicate course code checking (409 Conflict)
  - Added instructor existence and role validation
  - Fixed year validation with proper range checking

- **Missing Course Lists for Instructors**
  - Implemented proper database queries with JOINs
  - Added instructor filtering in GET /api/courses
  - Included instructor details (name, username) in responses
  - Added role-based filtering (students see enrolled courses)

- **Non-functional Admin/Instructor Panels**
  - Implemented complete role-based access control
  - Added authentication middleware to all protected routes
  - Implemented all CRUD operations with real database queries
  - Added proper permission checks for create/update/delete
  - Separated student, instructor, and admin access levels

- **Backend Connectivity Issues**
  - Added comprehensive error handling throughout
  - Implemented proper HTTP status codes
  - Added detailed error logging
  - Created consistent error response format
  - Added database connection error handling

- **Stub Implementations**
  - Replaced all TODO comments with working implementations
  - Converted all route handlers to use database operations
  - Added proper async/await error handling
  - Implemented parameterized queries for security

#### Changed

- **Database Schema**
  - Enhanced users table with role constraint and timestamps
  - Added unique constraint to course code
  - Added foreign key constraints with ON DELETE CASCADE
  - Added timestamps to all tables (createdAt, updatedAt)
  - Improved table structure with proper data types

- **Authentication**
  - Changed from placeholder responses to real JWT implementation
  - Upgraded password storage from plain text to bcrypt hashing
  - Added token expiration and refresh mechanism
  - Implemented secure token verification

- **.gitignore**
  - Added server/node_modules/
  - Added server/.env to prevent credential leaks
  - Added server log files
  - Added server/package-lock.json

#### Security

- Implemented bcrypt password hashing (10 salt rounds)
- Added JWT token authentication and verification
- Implemented input sanitization middleware
- Added prepared statements via pg library (SQL injection prevention)
- Secured environment variables (JWT_SECRET, DATABASE_URL)
- Added role-based authorization checks
- Implemented CORS configuration
- Added token expiration (7-day default)

## [Previous] - Before 2025-10-16

### Android Application
- Native Android app with Kotlin and Jetpack Compose
- Room database for local storage
- MVVM architecture with repositories
- User authentication (local)
- Role-based navigation
- QR code generation and scanning
- Biometric authentication
- Material 3 design
- Offline-first functionality
- Demo data seeding

### Backend API (Initial - Stubs Only)
- Express.js server setup
- Route structure defined
- API endpoint stubs (returned mock data)
- PostgreSQL database connection
- CORS configuration
- Basic error handling

---

**Note:** Version numbers follow [Semantic Versioning](https://semver.org/). This project is currently in active development.
