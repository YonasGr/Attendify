# Attendify REST API Server

Backend REST API server for the Attendify attendance management system, providing data synchronization capabilities between the Android app and cloud database.

## ✅ Implementation Status

**FULLY IMPLEMENTED** - All core endpoints are production-ready with complete database operations, authentication, and error handling.

## Features

- **RESTful API** for all Attendify data models with full CRUD operations
- **JWT Authentication** with bcrypt password hashing for secure access
- **Role-Based Authorization** with middleware for granular access control
- **PostgreSQL Database** with complete schema and constraints
- **Input Validation** at every endpoint
- **Comprehensive Error Handling** with proper HTTP status codes
- **CORS Enabled** for cross-origin requests
- **Production Ready** - Deployed on Render.com at https://attendify-mpsw.onrender.com

## Tech Stack

- **Runtime**: Node.js
- **Framework**: Express.js
- **Database**: PostgreSQL (compatible with Render.com)
- **Authentication**: JWT (JSON Web Tokens)
- **Password Hashing**: bcrypt (10 salt rounds)
- **UUID Generation**: uuid v4

## Prerequisites

- Node.js (v16 or higher)
- npm or yarn
- PostgreSQL database (local or cloud-hosted)

## Installation

### 1. Install Dependencies

```bash
npm install
```

### 2. Environment Configuration

Create a `.env` file in the server root directory:

```bash
cp .env.example .env
```

Edit `.env` with your configuration:

```env
NODE_ENV=development
PORT=3000

# Database Configuration (PostgreSQL on Render or similar)
DATABASE_URL=postgresql://username:password@host:5432/attendify_db

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_EXPIRES_IN=7d

# CORS Configuration
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
```

### 3. Database Setup

The server uses PostgreSQL. You can use:
- **Local PostgreSQL**: Install and run PostgreSQL locally
- **Render.com**: Create a free PostgreSQL database
- **Other cloud providers**: Heroku, AWS RDS, Google Cloud SQL, etc.

## Running the Server

### Development Mode

```bash
npm run dev
```

This uses `nodemon` for auto-reloading during development.

### Production Mode

```bash
npm start
```

## API Endpoints

### Health Check

```
GET /api/health
```

Response:
```json
{
  "status": "ok",
  "message": "Attendify API Server is running",
  "timestamp": "2024-01-XX:XX:XX.XXXZ"
}
```

### Authentication

All authentication endpoints are fully implemented with bcrypt password hashing and JWT token generation.

```
POST /api/auth/login          - User login with username/password
POST /api/auth/register       - User registration with validation
POST /api/auth/refresh        - Refresh JWT token for extended sessions
```

**Login Example:**
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": "...",
      "username": "admin",
      "role": "admin",
      "firstName": "Admin",
      "lastName": "User"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### Users

All user endpoints require authentication. Admin role required for most operations.

```
GET    /api/users             - Get all users (admin only, supports ?role= filter)
GET    /api/users/:id         - Get user by ID (own profile or admin)
POST   /api/users             - Create new user (admin only)
PUT    /api/users/:id         - Update user (own profile or admin)
DELETE /api/users/:id         - Delete user (admin only)
```

**Authentication Required:** All endpoints  
**Authorization:** Admin for list/create/delete, self or admin for get/update

### Courses

Course endpoints with role-based access control and validation.

```
GET    /api/courses           - Get all courses (filtered by role)
GET    /api/courses/:id       - Get course by ID (enrollment/ownership check)
POST   /api/courses           - Create new course (instructor/admin)
PUT    /api/courses/:id       - Update course (owner/admin)
DELETE /api/courses/:id       - Delete course (owner/admin)
```

**Query Parameters:**
- `instructorId`: Filter courses by instructor

**Authentication Required:** All endpoints  
**Authorization:** 
- Students see enrolled courses
- Instructors see their courses
- Admins see all courses

**Validation:**
- Required: code, name, instructorId, semester, year
- Unique: course code
- Year must be 2000-2100
- Instructor must exist and have instructor role

### Sessions

Session management with instructor authorization.

```
GET    /api/sessions          - Get all sessions (filtered by role/course)
GET    /api/sessions/:id      - Get session by ID (enrollment/ownership check)
POST   /api/sessions          - Create new session (instructor/admin)
PUT    /api/sessions/:id      - Update session (owner/admin)
DELETE /api/sessions/:id      - Delete session (owner/admin)
```

**Query Parameters:**
- `courseId`: Filter sessions by course

**Authentication Required:** All endpoints  
**Authorization:** Instructors can only manage sessions for their courses

**Validation:**
- Required: courseId, date, startTime, endTime
- Course must exist
- Instructor ownership validated

### Enrollments

Student enrollment management.

```
GET    /api/enrollments       - Get all enrollments (filtered by role)
POST   /api/enrollments       - Create new enrollment (instructor/admin)
DELETE /api/enrollments/:id   - Delete enrollment (instructor/admin)
```

**Query Parameters:**
- `courseId`: Filter enrollments by course
- `studentId`: Filter enrollments by student

**Authentication Required:** All endpoints  
**Authorization:** 
- Students see only their enrollments
- Instructors manage enrollments for their courses
- Admins see all enrollments

**Validation:**
- Student must exist and have student role
- Course must exist
- No duplicate enrollments
- Student must be enrolled to mark attendance

### Attendance

Attendance tracking with comprehensive validation.

```
GET    /api/attendance                      - Get all attendance records
GET    /api/attendance/session/:sessionId   - Get attendance by session (instructor/admin)
GET    /api/attendance/student/:studentId   - Get attendance by student (self/admin)
POST   /api/attendance                      - Create attendance record
DELETE /api/attendance/:id                  - Delete attendance record (instructor/admin)
```

**Authentication Required:** All endpoints  
**Authorization:**
- Students can mark own attendance and view own records
- Instructors can view/manage attendance for their courses
- Admins have full access

**Validation:**
- Required: sessionId, studentId
- Valid status: present, late, absent (default: present)
- Student must be enrolled in course
- No duplicate attendance for same session
- Session must exist

### Data Synchronization

Sync endpoints are planned for future implementation. Core CRUD operations are fully functional.

```
POST   /api/sync/upload              - Upload local data to server (planned)
GET    /api/sync/download            - Download server data to local (planned)
GET    /api/sync/status              - Check sync status (planned)
POST   /api/sync/resolve-conflicts   - Resolve sync conflicts (planned)
```

## Synchronization Architecture

### Offline-First Approach

The Attendify app follows an **offline-first** architecture:

1. **Local Database (Android)**: Primary data store using Room (SQLite)
2. **Cloud Database (Server)**: Backup and sync store using PostgreSQL
3. **Automatic Sync**: Periodic background sync when online
4. **Conflict Resolution**: Last-write-wins with manual resolution option

### Sync Flow

```
┌─────────────────┐         ┌─────────────────┐
│  Android App    │         │  REST API       │
│  (Room/SQLite)  │◄───────►│  (PostgreSQL)   │
└─────────────────┘         └─────────────────┘
        │                            │
        │  1. Check sync status      │
        ├───────────────────────────►│
        │                            │
        │  2. Upload local changes   │
        ├───────────────────────────►│
        │                            │
        │  3. Download server changes│
        │◄───────────────────────────┤
        │                            │
        │  4. Resolve conflicts      │
        ├───────────────────────────►│
        │                            │
```

### Conflict Resolution Strategies

1. **Last-Write-Wins**: Default strategy using timestamps
2. **Manual Resolution**: User chooses which version to keep
3. **Merge**: Combine non-conflicting changes

## Deploying to Render.com

### 1. Create PostgreSQL Database

1. Go to [Render.com](https://render.com/)
2. Create a new PostgreSQL database
3. Copy the internal database URL

### 2. Create Web Service

1. Create a new Web Service
2. Connect your GitHub repository
3. Configure:
   - **Build Command**: `npm install`
   - **Start Command**: `npm start`
   - **Environment Variables**:
     - `DATABASE_URL`: Your PostgreSQL internal URL
     - `JWT_SECRET`: Generate a secure random string
     - `NODE_ENV`: `production`

### 3. Deploy

Render will automatically deploy when you push to your main branch.

## Development Roadmap

### Current Status ✅

- [x] Basic Express server setup
- [x] Route structure with all endpoints
- [x] Complete database schema implementation
- [x] Database tables with constraints and indexes
- [x] JWT authentication implementation
- [x] bcrypt password hashing
- [x] Authentication middleware
- [x] Role-based authorization
- [x] Complete route implementations with database operations
- [x] Input validation at all endpoints
- [x] Comprehensive error handling
- [x] Proper HTTP status codes
- [x] Foreign key constraints and cascading deletes
- [x] Production deployment on Render.com

### Future Enhancements 📋

- [ ] Sync logic implementation (offline-first architecture)
- [ ] Conflict resolution algorithm for data sync
- [ ] Rate limiting for API protection
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Advanced request validation middleware (e.g., Joi, express-validator)
- [ ] Logging (Winston, Morgan)
- [ ] Monitoring and analytics
- [ ] Backup and restore utilities
- [ ] Admin dashboard
- [ ] Testing suite (Jest, Supertest)
- [ ] Database migrations (e.g., node-pg-migrate)
- [ ] Email notifications
- [ ] Password reset functionality
- [ ] Account verification

## Testing the API

### Using curl

**1. Register a new user:**
```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User",
    "role": "student",
    "studentId": "STU123",
    "department": "Computer Science"
  }'
```

**2. Login:**
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**3. Get all courses (requires token):**
```bash
curl -X GET http://localhost:3000/api/courses \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**4. Create a course (instructor/admin only):**
```bash
curl -X POST http://localhost:3000/api/courses \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CS101",
    "name": "Introduction to Programming",
    "description": "Learn programming fundamentals",
    "instructorId": "instructor-uuid",
    "semester": "Fall",
    "year": 2024
  }'
```

**5. Mark attendance:**
```bash
curl -X POST http://localhost:3000/api/attendance \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "session-uuid",
    "studentId": "student-uuid",
    "status": "present"
  }'
```

### Using Postman

1. Import the API collection (create from endpoints above)
2. Set base URL: `http://localhost:3000` or `https://attendify-mpsw.onrender.com`
3. Create an environment variable for JWT token
4. Login and save token
5. Use token in Authorization header for protected routes

### Error Responses

The API returns consistent error responses:

**400 Bad Request:**
```json
{
  "success": false,
  "message": "Username, password, and role are required"
}
```

**401 Unauthorized:**
```json
{
  "success": false,
  "message": "Access token is required"
}
```

**403 Forbidden:**
```json
{
  "success": false,
  "message": "Insufficient permissions"
}
```

**404 Not Found:**
```json
{
  "success": false,
  "message": "Course not found"
}
```

**409 Conflict:**
```json
{
  "success": false,
  "message": "Username already exists"
}
```

**500 Internal Server Error:**
```json
{
  "success": false,
  "message": "Failed to create course"
}
```

## Contributing

1. Create a feature branch
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## Security Considerations

- Never commit `.env` file
- Use strong JWT secrets
- Implement rate limiting in production
- Use HTTPS in production
- Validate and sanitize all inputs
- Implement proper error handling

## License

[Add license information]

## Support

For issues and questions:
- Open an issue on GitHub
- Check API documentation
- Contact the development team

---

Built with ❤️ for better attendance management
