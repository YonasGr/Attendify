const express = require('express');
const { v4: uuidv4 } = require('uuid');
const { authenticateToken, authorizeRoles } = require('../middleware/auth');
const router = express.Router();

// GET /api/enrollments - Get all enrollments
router.get('/', authenticateToken, async (req, res) => {
  try {
    const { courseId, studentId } = req.query;
    
    let query = `
      SELECT e.*, 
             u.username as studentUsername,
             u.firstName as studentFirstName, 
             u.lastName as studentLastName,
             u.studentId as studentNumber,
             c.code as courseCode,
             c.name as courseName
      FROM enrollments e
      LEFT JOIN users u ON e.studentId = u.id
      LEFT JOIN courses c ON e.courseId = c.id
    `;
    const params = [];
    const conditions = [];
    let paramCount = 1;
    
    if (courseId) {
      conditions.push(`e.courseId = $${paramCount++}`);
      params.push(courseId);
    }
    
    if (studentId) {
      conditions.push(`e.studentId = $${paramCount++}`);
      params.push(studentId);
    }
    
    if (req.user.role === 'instructor') {
      // Instructors see enrollments for their courses
      conditions.push(`c.instructorId = $${paramCount++}`);
      params.push(req.user.id);
    } else if (req.user.role === 'student') {
      // Students see only their own enrollments
      conditions.push(`e.studentId = $${paramCount++}`);
      params.push(req.user.id);
    }
    
    if (conditions.length > 0) {
      query += ' WHERE ' + conditions.join(' AND ');
    }
    
    query += ' ORDER BY e.enrolledAt DESC';
    
    const { rows } = await req.db.query(query, params);
    res.status(200).json({ success: true, data: rows });
  } catch (error) {
    console.error('Get enrollments error:', error);
    res.status(500).json({ success: false, message: 'Failed to retrieve enrollments' });
  }
});

// POST /api/enrollments - Create new enrollment
router.post('/', authenticateToken, authorizeRoles('instructor', 'admin'), async (req, res) => {
  try {
    const { studentId, courseId } = req.body;
    
    // Validate required fields
    if (!studentId || !courseId) {
      return res.status(400).json({
        success: false,
        message: 'StudentId and courseId are required'
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

    // Verify course exists
    const courseCheck = await req.db.query(
      'SELECT id, instructorId FROM courses WHERE id = $1',
      [courseId]
    );
    
    if (courseCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Course not found'
      });
    }

    // Instructors can only enroll students in their courses
    if (req.user.role === 'instructor' && courseCheck.rows[0].instructorid !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }

    // Check if already enrolled
    const existingEnrollment = await req.db.query(
      'SELECT id FROM enrollments WHERE studentId = $1 AND courseId = $2',
      [studentId, courseId]
    );
    
    if (existingEnrollment.rows.length > 0) {
      return res.status(409).json({
        success: false,
        message: 'Student is already enrolled in this course'
      });
    }

    const id = uuidv4();
    const newEnrollment = await req.db.query(
      `INSERT INTO enrollments (id, studentId, courseId) 
       VALUES ($1, $2, $3) 
       RETURNING *`,
      [id, studentId, courseId]
    );
    
    res.status(201).json({ 
      success: true, 
      message: 'Enrollment created successfully',
      data: newEnrollment.rows[0] 
    });
  } catch (error) {
    console.error('Create enrollment error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to create enrollment' 
    });
  }
});

// DELETE /api/enrollments/:id - Delete enrollment
router.delete('/:id', authenticateToken, authorizeRoles('instructor', 'admin'), async (req, res) => {
  try {
    const { id } = req.params;
    
    // Get enrollment details
    const enrollmentCheck = await req.db.query(`
      SELECT e.id, c.instructorId 
      FROM enrollments e
      JOIN courses c ON e.courseId = c.id
      WHERE e.id = $1
    `, [id]);
    
    if (enrollmentCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Enrollment not found'
      });
    }

    // Instructors can only delete enrollments for their courses
    if (req.user.role === 'instructor' && enrollmentCheck.rows[0].instructorid !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }

    await req.db.query('DELETE FROM enrollments WHERE id = $1', [id]);
    
    res.status(200).json({ 
      success: true, 
      message: 'Enrollment deleted successfully' 
    });
  } catch (error) {
    console.error('Delete enrollment error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to delete enrollment' 
    });
  }
});

module.exports = router;
