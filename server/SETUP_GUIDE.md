# Backend Setup and Testing Guide

This guide helps you set up and test the Attendify backend API locally.

## Prerequisites

- Node.js v16 or higher
- PostgreSQL 12 or higher
- npm or yarn
- curl or Postman (for API testing)

## Quick Setup

### 1. Install Dependencies

```bash
cd server
npm install
```

### 2. Set Up PostgreSQL Database

**Option A: Local PostgreSQL**

```bash
# Install PostgreSQL (if not already installed)
# macOS:
brew install postgresql
brew services start postgresql

# Ubuntu/Debian:
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql

# Create database
psql postgres
CREATE DATABASE attendify;
CREATE USER attendify_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE attendify TO attendify_user;
\q
```

**Option B: Use Docker**

```bash
docker run --name attendify-postgres -e POSTGRES_PASSWORD=password -e POSTGRES_DB=attendify -p 5432:5432 -d postgres:14
```

### 3. Configure Environment Variables

```bash
cp .env.example .env
```

Edit `.env` with your configuration:

```env
NODE_ENV=development
PORT=3000

# Local PostgreSQL
DATABASE_URL=postgresql://attendify_user:your_password@localhost:5432/attendify

# OR for Docker:
DATABASE_URL=postgresql://postgres:password@localhost:5432/attendify

# JWT Configuration (generate a secure random string)
JWT_SECRET=your-super-secret-jwt-key-min-32-characters-long
JWT_EXPIRES_IN=7d

# CORS Configuration
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
```

**Generate a secure JWT secret:**
```bash
# Using Node.js
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"

# Using OpenSSL
openssl rand -hex 32
```

### 4. Start the Server

**Development mode (with auto-reload):**
```bash
npm run dev
```

**Production mode:**
```bash
npm start
```

The server will start on `http://localhost:3000` and automatically create all database tables.

### 5. Verify Installation

Check the health endpoint:
```bash
curl http://localhost:3000/api/health
```

Expected response:
```json
{
  "status": "ok",
  "message": "Attendify API Server is running",
  "timestamp": "2024-01-15T10:30:00.000Z"
}
```

## Testing the API

### Using the Test Script

Run the automated test script:
```bash
./test-api.sh
# Or for a remote server:
./test-api.sh https://attendify-mpsw.onrender.com
```

### Manual Testing

#### 1. Register a User

```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "securepass123",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "instructor",
    "department": "Computer Science"
  }'
```

#### 2. Login

```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "securepass123"
  }'
```

Save the token from the response:
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 3. Create a Course

```bash
curl -X POST http://localhost:3000/api/courses \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CS101",
    "name": "Introduction to Programming",
    "description": "Learn the basics of programming",
    "instructorId": "your-user-id-from-login",
    "semester": "Fall",
    "year": 2024
  }'
```

#### 4. Get All Courses

```bash
curl -X GET http://localhost:3000/api/courses \
  -H "Authorization: Bearer $TOKEN"
```

#### 5. Create a Session

```bash
curl -X POST http://localhost:3000/api/sessions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": "course-id-from-previous-step",
    "date": "2024-10-20",
    "startTime": "10:00",
    "endTime": "11:30",
    "qrCode": "session-qr-code-123",
    "isActive": true
  }'
```

#### 6. Register a Student

```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student1",
    "password": "student123",
    "email": "student@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "role": "student",
    "studentId": "STU001",
    "department": "Computer Science"
  }'
```

#### 7. Enroll Student in Course

```bash
curl -X POST http://localhost:3000/api/enrollments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": "student-user-id",
    "courseId": "course-id"
  }'
```

#### 8. Mark Attendance

```bash
curl -X POST http://localhost:3000/api/attendance \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "session-id",
    "studentId": "student-user-id",
    "status": "present"
  }'
```

## Common Issues

### Database Connection Errors

**Problem:** `Error: connect ECONNREFUSED 127.0.0.1:5432`

**Solution:**
- Ensure PostgreSQL is running: `sudo systemctl status postgresql` (Linux) or `brew services list` (macOS)
- Check DATABASE_URL in `.env` is correct
- Verify PostgreSQL is listening on port 5432: `sudo netstat -plnt | grep 5432`

### Authentication Errors

**Problem:** `403 Forbidden` or `401 Unauthorized`

**Solution:**
- Ensure you're including the JWT token in the Authorization header: `Authorization: Bearer YOUR_TOKEN`
- Check if token has expired (default: 7 days)
- Verify user has the required role for the endpoint

### Validation Errors

**Problem:** `400 Bad Request - Username already exists`

**Solution:**
- Use a unique username for registration
- Check if user already exists: `curl http://localhost:3000/api/users -H "Authorization: Bearer $TOKEN"`

### Port Already in Use

**Problem:** `Error: listen EADDRINUSE: address already in use :::3000`

**Solution:**
```bash
# Find process using port 3000
lsof -i :3000
# Kill the process
kill -9 <PID>
# Or use a different port in .env
PORT=3001
```

## Development Tips

### Viewing Database Tables

```bash
psql -U attendify_user -d attendify

# List all tables
\dt

# View table structure
\d users
\d courses
\d sessions

# Query data
SELECT * FROM users;
SELECT * FROM courses;

# Exit psql
\q
```

### Clearing Database

```bash
psql -U attendify_user -d attendify

DROP TABLE IF EXISTS attendance_records CASCADE;
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS sessions CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS users CASCADE;

# Restart server to recreate tables
```

### Debugging

Enable detailed logging by adding to your code:
```javascript
// In server.js or any route file
console.log('Request body:', req.body);
console.log('User:', req.user);
console.log('Database query:', query, params);
```

### Using Postman

1. Create a new Postman collection
2. Set base URL variable: `{{baseUrl}}` = `http://localhost:3000`
3. Create requests for each endpoint
4. Use Tests tab to save token automatically:
   ```javascript
   if (pm.response.code === 200) {
       const response = pm.response.json();
       if (response.data && response.data.token) {
           pm.environment.set("token", response.data.token);
       }
   }
   ```
5. Use `{{token}}` in Authorization header for protected routes

## Production Deployment

### Deploy to Render.com

1. Push code to GitHub
2. Create a new Web Service on Render.com
3. Connect your GitHub repository
4. Add PostgreSQL database (free tier available)
5. Set environment variables:
   - `DATABASE_URL`: (automatically set by Render)
   - `JWT_SECRET`: (generate secure random string)
   - `NODE_ENV`: `production`
6. Deploy!

The backend will be available at `https://your-app-name.onrender.com`

## Security Checklist

- [ ] Use strong JWT_SECRET (min 32 characters)
- [ ] Enable HTTPS in production
- [ ] Set secure CORS origins
- [ ] Use environment variables for all secrets
- [ ] Never commit `.env` file
- [ ] Implement rate limiting (future enhancement)
- [ ] Regular security updates: `npm audit fix`
- [ ] Use prepared statements (already implemented via pg library)

## Next Steps

1. Test all endpoints with the test script
2. Create admin, instructor, and student accounts
3. Create sample courses and sessions
4. Connect Android app to your local backend
5. Test the full workflow: registration → course creation → session creation → attendance marking

## Support

If you encounter issues:
1. Check server logs for error messages
2. Verify database connection and tables
3. Ensure all environment variables are set correctly
4. Review the troubleshooting section in README.md
5. Open an issue on GitHub with error details

---

Happy coding! 🚀
