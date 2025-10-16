const express = require('express');
const { v4: uuidv4 } = require('uuid');
const { authenticateToken, authorizeRoles } = require('../middleware/auth');
const router = express.Router();

// GET /api/attendance - Get all attendance records
router.get('/', authenticateToken, async (req, res) => {
  try {
    let query = `
      SELECT a.*, 
             u.username as studentUsername,
             u.firstName as studentFirstName, 
             u.lastName as studentLastName,
             u.studentId as studentNumber,
             s.date as sessionDate,
             s.startTime as sessionStartTime,
             c.code as courseCode,
             c.name as courseName
      FROM attendance_records a
      LEFT JOIN users u ON a.studentId = u.id
      LEFT JOIN sessions s ON a.sessionId = s.id
      LEFT JOIN courses c ON s.courseId = c.id
    `;
    const params = [];
    const conditions = [];
    let paramCount = 1;
    
    if (req.user.role === 'instructor') {
      // Instructors see attendance for their courses
      conditions.push(`c.instructorId = $${paramCount++}`);
      params.push(req.user.id);
    } else if (req.user.role === 'student') {
      // Students see only their own attendance
      conditions.push(`a.studentId = $${paramCount++}`);
      params.push(req.user.id);
    }
    
    if (conditions.length > 0) {
      query += ' WHERE ' + conditions.join(' AND ');
    }
    
    query += ' ORDER BY a.timestamp DESC';
    
    const { rows } = await req.db.query(query, params);
    res.status(200).json({ success: true, data: rows });
  } catch (error) {
    console.error('Get attendance error:', error);
    res.status(500).json({ success: false, message: 'Failed to retrieve attendance records' });
  }
});

// GET /api/attendance/session/:sessionId - Get attendance by session
router.get('/session/:sessionId', authenticateToken, async (req, res) => {
  try {
    const { sessionId } = req.params;
    
    // Verify session exists and check permissions
    const sessionCheck = await req.db.query(`
      SELECT s.id, c.instructorId 
      FROM sessions s
      JOIN courses c ON s.courseId = c.id
      WHERE s.id = $1
    `, [sessionId]);
    
    if (sessionCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Session not found'
      });
    }

    // Only instructors and admins can view session attendance
    if (req.user.role === 'instructor' && sessionCheck.rows[0].instructorid !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }

    if (req.user.role === 'student') {
      return res.status(403).json({
        success: false,
        message: 'Students cannot view session attendance list'
      });
    }

    const { rows } = await req.db.query(`
      SELECT a.*, 
             u.username as studentUsername,
             u.firstName as studentFirstName, 
             u.lastName as studentLastName,
             u.studentId as studentNumber
      FROM attendance_records a
      LEFT JOIN users u ON a.studentId = u.id
      WHERE a.sessionId = $1
      ORDER BY u.lastName, u.firstName
    `, [sessionId]);
    
    res.status(200).json({ success: true, data: rows });
  } catch (error) {
    console.error('Get session attendance error:', error);
    res.status(500).json({ success: false, message: 'Failed to retrieve session attendance' });
  }
});

// GET /api/attendance/student/:studentId - Get attendance by student
router.get('/student/:studentId', authenticateToken, async (req, res) => {
  try {
    const { studentId } = req.params;
    
    // Students can only view their own attendance, admins can view any
    if (req.user.role === 'student' && req.user.id !== studentId) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }

    // Verify student exists
    const studentCheck = await req.db.query(
      'SELECT id FROM users WHERE id = $1 AND role = $2',
      [studentId, 'student']
    );
    
    if (studentCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Student not found'
      });
    }

    const { rows } = await req.db.query(`
      SELECT a.*, 
             s.date as sessionDate,
             s.startTime as sessionStartTime,
             c.code as courseCode,
             c.name as courseName
      FROM attendance_records a
      LEFT JOIN sessions s ON a.sessionId = s.id
      LEFT JOIN courses c ON s.courseId = c.id
      WHERE a.studentId = $1
      ORDER BY s.date DESC, s.startTime DESC
    `, [studentId]);
    
    res.status(200).json({ success: true, data: rows });
  } catch (error) {
    console.error('Get student attendance error:', error);
    res.status(500).json({ success: false, message: 'Failed to retrieve student attendance' });
  }
});

// POST /api/attendance - Create attendance record
router.post('/', authenticateToken, async (req, res) => {
  try {
    const { sessionId, studentId, status } = req.body;
    
    // Validate required fields
    if (!sessionId || !studentId) {
      return res.status(400).json({
        success: false,
        message: 'SessionId and studentId are required'
      });
    }

    // Validate status
    const validStatuses = ['present', 'late', 'absent'];
    if (status && !validStatuses.includes(status)) {
      return res.status(400).json({
        success: false,
        message: 'Invalid status. Must be present, late, or absent'
      });
    }

    // Verify student exists and is a student
    const studentCheck = await req.db.query(
      'SELECT id, role FROM users WHERE id = $1',
      [studentId]
    );
    
    if (studentCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Student not found'
      });
    }
    
    if (studentCheck.rows[0].role !== 'student') {
      return res.status(400).json({
        success: false,
        message: 'User is not a student'
      });
    }

    // Verify session exists
    const sessionCheck = await req.db.query(`
      SELECT s.id, s.courseId, c.instructorId 
      FROM sessions s
      JOIN courses c ON s.courseId = c.id
      WHERE s.id = $1
    `, [sessionId]);
    
    if (sessionCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Session not found'
      });
    }

    // Check enrollment
    const enrollmentCheck = await req.db.query(
      'SELECT id FROM enrollments WHERE studentId = $1 AND courseId = $2',
      [studentId, sessionCheck.rows[0].courseid]
    );
    
    if (enrollmentCheck.rows.length === 0) {
      return res.status(400).json({
        success: false,
        message: 'Student is not enrolled in this course'
      });
    }

    // Students can only mark their own attendance
    if (req.user.role === 'student' && req.user.id !== studentId) {
      return res.status(403).json({
        success: false,
        message: 'Students can only mark their own attendance'
      });
    }

    // Instructors can only mark attendance for their courses
    if (req.user.role === 'instructor' && sessionCheck.rows[0].instructorid !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }

    // Check if attendance already exists
    const existingAttendance = await req.db.query(
      'SELECT id FROM attendance_records WHERE sessionId = $1 AND studentId = $2',
      [sessionId, studentId]
    );
    
    if (existingAttendance.rows.length > 0) {
      return res.status(409).json({
        success: false,
        message: 'Attendance already recorded for this session'
      });
    }

    const id = uuidv4();
    const attendanceStatus = status || 'present';
    
    const newAttendance = await req.db.query(
      `INSERT INTO attendance_records (id, sessionId, studentId, status) 
       VALUES ($1, $2, $3, $4) 
       RETURNING *`,
      [id, sessionId, studentId, attendanceStatus]
    );
    
    res.status(201).json({ 
      success: true, 
      message: 'Attendance recorded successfully',
      data: newAttendance.rows[0] 
    });
  } catch (error) {
    console.error('Create attendance error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to record attendance' 
    });
  }
});

// DELETE /api/attendance/:id - Delete attendance record
router.delete('/:id', authenticateToken, authorizeRoles('instructor', 'admin'), async (req, res) => {
  try {
    const { id } = req.params;
    
    // Get attendance details
    const attendanceCheck = await req.db.query(`
      SELECT a.id, c.instructorId 
      FROM attendance_records a
      JOIN sessions s ON a.sessionId = s.id
      JOIN courses c ON s.courseId = c.id
      WHERE a.id = $1
    `, [id]);
    
    if (attendanceCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Attendance record not found'
      });
    }

    // Instructors can only delete attendance for their courses
    if (req.user.role === 'instructor' && attendanceCheck.rows[0].instructorid !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }

    await req.db.query('DELETE FROM attendance_records WHERE id = $1', [id]);
    
    res.status(200).json({ 
      success: true, 
      message: 'Attendance record deleted successfully' 
    });
  } catch (error) {
    console.error('Delete attendance error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to delete attendance record' 
    });
  }
});

module.exports = router;
