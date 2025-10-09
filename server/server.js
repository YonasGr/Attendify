const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

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

// Start server
app.listen(PORT, () => {
  console.log(`Attendify API Server running on port ${PORT}`);
  console.log(`Health check: http://localhost:${PORT}/api/health`);
});

module.exports = app;
