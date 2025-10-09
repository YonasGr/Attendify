# Attendify REST API Server

Backend REST API server for the Attendify attendance management system, providing data synchronization capabilities between the Android app and cloud database.

## Features

- **RESTful API** for all Attendify data models
- **Data Synchronization** between local (Android) and cloud storage
- **Conflict Resolution** for offline-first architecture
- **JWT Authentication** for secure API access
- **PostgreSQL Database** support (optimized for Render.com deployment)
- **CORS Enabled** for cross-origin requests

## Tech Stack

- **Runtime**: Node.js
- **Framework**: Express.js
- **Database**: PostgreSQL (compatible with Render.com)
- **Authentication**: JWT (JSON Web Tokens)
- **Password Hashing**: bcrypt

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

```
POST /api/auth/login          - User login
POST /api/auth/register       - User registration
POST /api/auth/refresh        - Refresh JWT token
```

### Users

```
GET    /api/users             - Get all users
GET    /api/users/:id         - Get user by ID
POST   /api/users             - Create new user
PUT    /api/users/:id         - Update user
DELETE /api/users/:id         - Delete user
```

### Courses

```
GET    /api/courses           - Get all courses
GET    /api/courses/:id       - Get course by ID
POST   /api/courses           - Create new course
PUT    /api/courses/:id       - Update course
DELETE /api/courses/:id       - Delete course
```

### Sessions

```
GET    /api/sessions          - Get all sessions
GET    /api/sessions/:id      - Get session by ID
POST   /api/sessions          - Create new session
PUT    /api/sessions/:id      - Update session
DELETE /api/sessions/:id      - Delete session
```

### Enrollments

```
GET    /api/enrollments       - Get all enrollments
POST   /api/enrollments       - Create new enrollment
DELETE /api/enrollments/:id   - Delete enrollment
```

### Attendance

```
GET    /api/attendance                      - Get all attendance records
GET    /api/attendance/session/:sessionId   - Get attendance by session
GET    /api/attendance/student/:studentId   - Get attendance by student
POST   /api/attendance                      - Create attendance record
DELETE /api/attendance/:id                  - Delete attendance record
```

### Data Synchronization

```
POST   /api/sync/upload              - Upload local data to server
GET    /api/sync/download            - Download server data to local
GET    /api/sync/status              - Check sync status
POST   /api/sync/resolve-conflicts   - Resolve sync conflicts
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

### Current Status (Scaffold)

- [x] Basic Express server setup
- [x] Route structure
- [x] API endpoint stubs
- [x] Sync endpoint structure
- [ ] Database schema implementation
- [ ] Database migrations
- [ ] JWT authentication implementation
- [ ] Route implementations with database operations
- [ ] Sync logic implementation
- [ ] Conflict resolution algorithm
- [ ] Testing suite

### Future Enhancements

- [ ] Rate limiting
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Request validation middleware
- [ ] Logging (Winston, Morgan)
- [ ] Monitoring and analytics
- [ ] Backup and restore utilities
- [ ] Admin dashboard

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
