const express = require('express');
const cors = require('cors');
const db = require('./db');
const { sanitizeInput } = require('./middleware/validation');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(sanitizeInput); // Sanitize all inputs

// Make db available to routes
app.use((req, res, next) => {
  req.db = db;
  next();
});

// Import routes
const authRoutes = require('./routes/auth');
const usersRoutes = require('./routes/users');
const coursesRoutes = require('./routes/courses');
const sessionsRoutes = require('./routes/sessions');
const enrollmentsRoutes = require('./routes/enrollments');
const attendanceRoutes = require('./routes/attendance');
const syncRoutes = require('./routes/sync');

// Use routes
app.use('/api/auth', authRoutes);
app.use('/api/users', usersRoutes);
app.use('/api/courses', coursesRoutes);
app.use('/api/sessions', sessionsRoutes);
app.use('/api/enrollments', enrollmentsRoutes);
app.use('/api/attendance', attendanceRoutes);
app.use('/api/sync', syncRoutes);

// Health check endpoint
app.get('/api/health', (req, res) => {
  res.json({ 
    status: 'ok', 
    message: 'Attendify API Server is running',
    timestamp: new Date().toISOString()
  });
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ 
    error: 'Something went wrong!',
    message: err.message 
  });
});

// Create tables
async function createTables() {
    try {
        // Users table
        await db.query(`
            CREATE TABLE IF NOT EXISTS users (
                id TEXT PRIMARY KEY,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                email TEXT,
                firstName TEXT,
                lastName TEXT,
                role TEXT NOT NULL CHECK (role IN ('student', 'instructor', 'admin')),
                studentId TEXT,
                department TEXT,
                createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        `);
        console.log('✓ Users table created');

        // Courses table
        await db.query(`
            CREATE TABLE IF NOT EXISTS courses (
                id TEXT PRIMARY KEY,
                code TEXT NOT NULL UNIQUE,
                name TEXT NOT NULL,
                description TEXT,
                instructorId TEXT NOT NULL,
                semester TEXT NOT NULL,
                year INTEGER NOT NULL,
                createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (instructorId) REFERENCES users(id) ON DELETE CASCADE
            )
        `);
        console.log('✓ Courses table created');

        // Sessions table
        await db.query(`
            CREATE TABLE IF NOT EXISTS sessions (
                id TEXT PRIMARY KEY,
                courseId TEXT NOT NULL,
                date TEXT NOT NULL,
                startTime TEXT NOT NULL,
                endTime TEXT NOT NULL,
                qrCode TEXT,
                isActive BOOLEAN DEFAULT true,
                createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (courseId) REFERENCES courses(id) ON DELETE CASCADE
            )
        `);
        console.log('✓ Sessions table created');

        // Enrollments table
        await db.query(`
            CREATE TABLE IF NOT EXISTS enrollments (
                id TEXT PRIMARY KEY,
                studentId TEXT NOT NULL,
                courseId TEXT NOT NULL,
                enrolledAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (studentId) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (courseId) REFERENCES courses(id) ON DELETE CASCADE,
                UNIQUE(studentId, courseId)
            )
        `);
        console.log('✓ Enrollments table created');

        // Attendance records table
        await db.query(`
            CREATE TABLE IF NOT EXISTS attendance_records (
                id TEXT PRIMARY KEY,
                sessionId TEXT NOT NULL,
                studentId TEXT NOT NULL,
                status TEXT NOT NULL CHECK (status IN ('present', 'late', 'absent')),
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (sessionId) REFERENCES sessions(id) ON DELETE CASCADE,
                FOREIGN KEY (studentId) REFERENCES users(id) ON DELETE CASCADE,
                UNIQUE(sessionId, studentId)
            )
        `);
        console.log('✓ Attendance records table created');

        console.log('✅ All tables created successfully');
    } catch (err) {
        console.error('❌ Error creating tables:', err.stack);
        throw err;
    }
}

// Start server
app.listen(PORT, async () => {
  console.log(`Attendify API Server running on port ${PORT}`);
  await createTables();
  console.log(`Health check: http://localhost:${PORT}/api/health`);
});

module.exports = app;