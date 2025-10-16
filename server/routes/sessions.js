const express = require('express');
const { v4: uuidv4 } = require('uuid');
const { authenticateToken, authorizeRoles } = require('../middleware/auth');
const router = express.Router();

// GET /api/sessions - Get all sessions
router.get('/', authenticateToken, async (req, res) => {
  try {
    const { courseId } = req.query;
    
    let query = `
      SELECT s.*, 
             c.code as courseCode, 
             c.name as courseName
      FROM sessions s
      LEFT JOIN courses c ON s.courseId = c.id
    `;
    const params = [];
    
    if (courseId) {
      query += ' WHERE s.courseId = $1';
      params.push(courseId);
    } else if (req.user.role === 'instructor') {
      // Instructors see sessions for their courses
      query += ' WHERE c.instructorId = $1';
      params.push(req.user.id);
    } else if (req.user.role === 'student') {
      // Students see sessions for enrolled courses
      query = `
        SELECT s.*, 
               c.code as courseCode, 
               c.name as courseName
        FROM sessions s
        LEFT JOIN courses c ON s.courseId = c.id
        INNER JOIN enrollments e ON c.id = e.courseId
        WHERE e.studentId = $1
      `;
      params.push(req.user.id);
    }
    
    query += ' ORDER BY s.date DESC, s.startTime DESC';
    
    const { rows } = await req.db.query(query, params);
    res.status(200).json({ success: true, data: rows });
  } catch (error) {
    console.error('Get sessions error:', error);
    res.status(500).json({ success: false, message: 'Failed to retrieve sessions' });
  }
});

// GET /api/sessions/:id - Get session by ID
router.get('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    
    const { rows } = await req.db.query(`
      SELECT s.*, 
             c.code as courseCode, 
             c.name as courseName,
             c.instructorId
      FROM sessions s
      LEFT JOIN courses c ON s.courseId = c.id
      WHERE s.id = $1
    `, [id]);
    
    if (rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Session not found' });
    }
    
    const session = rows[0];
    
    // Check access permissions
    if (req.user.role === 'instructor' && session.instructorid !== req.user.id) {
      return res.status(403).json({ success: false, message: 'Access denied' });
    }
    
    if (req.user.role === 'student') {
      // Check if student is enrolled in the course
      const enrollment = await req.db.query(
        'SELECT id FROM enrollments WHERE studentId = $1 AND courseId = $2',
        [req.user.id, session.courseid]
      );
      if (enrollment.rows.length === 0) {
        return res.status(403).json({ success: false, message: 'Not enrolled in this course' });
      }
    }
    
    res.status(200).json({ success: true, data: session });
  } catch (error) {
    console.error('Get session error:', error);
    res.status(500).json({ success: false, message: 'Failed to retrieve session' });
  }
});

// POST /api/sessions - Create new session
router.post('/', authenticateToken, authorizeRoles('instructor', 'admin'), async (req, res) => {
  try {
    const { courseId, date, startTime, endTime, qrCode, isActive } = req.body;
    
    // Validate required fields
    if (!courseId || !date || !startTime || !endTime) {
      return res.status(400).json({
        success: false,
        message: 'CourseId, date, startTime, and endTime are required'
      });
    }

    // Verify course exists
    const courseCheck = await req.db.query(
      'SELECT instructorId FROM courses WHERE id = $1',
      [courseId]
    );
    
    if (courseCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Course not found'
      });
    }

    // Instructors can only create sessions for their courses
    if (req.user.role === 'instructor' && courseCheck.rows[0].instructorid !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }

    const id = uuidv4();
    const newSession = await req.db.query(
      `INSERT INTO sessions (id, courseId, date, startTime, endTime, qrCode, isActive) 
       VALUES ($1, $2, $3, $4, $5, $6, $7) 
       RETURNING *`,
      [id, courseId, date, startTime, endTime, qrCode || null, isActive !== undefined ? isActive : true]
    );
    
    res.status(201).json({ 
      success: true, 
      message: 'Session created successfully',
      data: newSession.rows[0] 
    });
  } catch (error) {
    console.error('Create session error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to create session' 
    });
  }
});

// PUT /api/sessions/:id - Update session
router.put('/:id', authenticateToken, authorizeRoles('instructor', 'admin'), async (req, res) => {
  try {
    const { id } = req.params;
    const { date, startTime, endTime, qrCode, isActive } = req.body;
    
    // Check if session exists and get course info
    const sessionCheck = await req.db.query(`
      SELECT s.id, c.instructorId 
      FROM sessions s
      JOIN courses c ON s.courseId = c.id
      WHERE s.id = $1
    `, [id]);
    
    if (sessionCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Session not found'
      });
    }

    // Instructors can only update sessions for their courses
    if (req.user.role === 'instructor' && sessionCheck.rows[0].instructorid !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }

    const updates = [];
    const values = [];
    let paramCount = 1;

    if (date !== undefined) {
      updates.push(`date = $${paramCount++}`);
      values.push(date);
    }
    if (startTime !== undefined) {
      updates.push(`startTime = $${paramCount++}`);
      values.push(startTime);
    }
    if (endTime !== undefined) {
      updates.push(`endTime = $${paramCount++}`);
      values.push(endTime);
    }
    if (qrCode !== undefined) {
      updates.push(`qrCode = $${paramCount++}`);
      values.push(qrCode);
    }
    if (isActive !== undefined) {
      updates.push(`isActive = $${paramCount++}`);
      values.push(isActive);
    }

    if (updates.length === 0) {
      return res.status(400).json({
        success: false,
        message: 'No fields to update'
      });
    }

    updates.push(`updatedAt = CURRENT_TIMESTAMP`);
    values.push(id);

    const query = `
      UPDATE sessions 
      SET ${updates.join(', ')} 
      WHERE id = $${paramCount}
      RETURNING *
    `;

    const result = await req.db.query(query, values);
    
    res.status(200).json({ 
      success: true, 
      message: 'Session updated successfully',
      data: result.rows[0]
    });
  } catch (error) {
    console.error('Update session error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to update session' 
    });
  }
});

// DELETE /api/sessions/:id - Delete session
router.delete('/:id', authenticateToken, authorizeRoles('instructor', 'admin'), async (req, res) => {
  try {
    const { id } = req.params;
    
    // Check if session exists and get course info
    const sessionCheck = await req.db.query(`
      SELECT s.id, c.instructorId 
      FROM sessions s
      JOIN courses c ON s.courseId = c.id
      WHERE s.id = $1
    `, [id]);
    
    if (sessionCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Session not found'
      });
    }

    // Instructors can only delete sessions for their courses
    if (req.user.role === 'instructor' && sessionCheck.rows[0].instructorid !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }

    await req.db.query('DELETE FROM sessions WHERE id = $1', [id]);
    
    res.status(200).json({ 
      success: true, 
      message: 'Session deleted successfully' 
    });
  } catch (error) {
    console.error('Delete session error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to delete session' 
    });
  }
});

module.exports = router;
