const express = require('express');
const { v4: uuidv4 } = require('uuid');
const { authenticateToken, authorizeRoles } = require('../middleware/auth');
const router = express.Router();

// GET /api/courses - Get all courses
router.get('/', authenticateToken, async (req, res) => {
  try {
    const { instructorId } = req.query;
    
    let query = `
      SELECT c.*, 
             u.firstName as instructorFirstName, 
             u.lastName as instructorLastName,
             u.username as instructorUsername
      FROM courses c
      LEFT JOIN users u ON c.instructorId = u.id
    `;
    const params = [];
    
    if (instructorId) {
      query += ' WHERE c.instructorId = $1';
      params.push(instructorId);
    } else if (req.user.role === 'instructor') {
      // Instructors can only see their own courses
      query += ' WHERE c.instructorId = $1';
      params.push(req.user.id);
    } else if (req.user.role === 'student') {
      // Students see only enrolled courses
      query = `
        SELECT c.*, 
               u.firstName as instructorFirstName, 
               u.lastName as instructorLastName,
               u.username as instructorUsername
        FROM courses c
        LEFT JOIN users u ON c.instructorId = u.id
        INNER JOIN enrollments e ON c.id = e.courseId
        WHERE e.studentId = $1
      `;
      params.push(req.user.id);
    }
    
    query += ' ORDER BY c.year DESC, c.semester DESC, c.code ASC';
    
    const { rows } = await req.db.query(query, params);
    res.status(200).json({ success: true, data: rows });
  } catch (error) {
    console.error('Get courses error:', error);
    res.status(500).json({ success: false, message: 'Failed to retrieve courses' });
  }
});

// GET /api/courses/:id - Get course by ID
router.get('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    
    const { rows } = await req.db.query(`
      SELECT c.*, 
             u.firstName as instructorFirstName, 
             u.lastName as instructorLastName,
             u.username as instructorUsername
      FROM courses c
      LEFT JOIN users u ON c.instructorId = u.id
      WHERE c.id = $1
    `, [id]);
    
    if (rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Course not found' });
    }
    
    // Check access permissions
    const course = rows[0];
    if (req.user.role === 'instructor' && course.instructorid !== req.user.id) {
      return res.status(403).json({ success: false, message: 'Access denied' });
    }
    
    if (req.user.role === 'student') {
      // Check if student is enrolled
      const enrollment = await req.db.query(
        'SELECT id FROM enrollments WHERE studentId = $1 AND courseId = $2',
        [req.user.id, id]
      );
      if (enrollment.rows.length === 0) {
        return res.status(403).json({ success: false, message: 'Not enrolled in this course' });
      }
    }
    
    res.status(200).json({ success: true, data: course });
  } catch (error) {
    console.error('Get course error:', error);
    res.status(500).json({ success: false, message: 'Failed to retrieve course' });
  }
});

// POST /api/courses - Create new course
router.post('/', authenticateToken, authorizeRoles('instructor', 'admin'), async (req, res) => {
  try {
    const { code, name, description, instructorId, semester, year } = req.body;
    
    // Validate required fields
    if (!code || !name || !instructorId || !semester || !year) {
      return res.status(400).json({
        success: false,
        message: 'Code, name, instructorId, semester, and year are required'
      });
    }

    // Validate year is a number
    if (isNaN(year) || year < 2000 || year > 2100) {
      return res.status(400).json({
        success: false,
        message: 'Invalid year'
      });
    }

    // Instructors can only create courses for themselves
    if (req.user.role === 'instructor' && instructorId !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Instructors can only create courses for themselves'
      });
    }

    // Verify instructor exists and is an instructor
    const instructorCheck = await req.db.query(
      'SELECT id, role FROM users WHERE id = $1',
      [instructorId]
    );
    
    if (instructorCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Instructor not found'
      });
    }
    
    if (instructorCheck.rows[0].role !== 'instructor') {
      return res.status(400).json({
        success: false,
        message: 'User is not an instructor'
      });
    }

    // Check if course code already exists
    const existingCourse = await req.db.query(
      'SELECT id FROM courses WHERE code = $1',
      [code]
    );
    
    if (existingCourse.rows.length > 0) {
      return res.status(409).json({
        success: false,
        message: 'Course code already exists'
      });
    }

    const id = uuidv4();
    const newCourse = await req.db.query(
      `INSERT INTO courses (id, code, name, description, instructorId, semester, year) 
       VALUES ($1, $2, $3, $4, $5, $6, $7) 
       RETURNING *`,
      [id, code, name, description, instructorId, semester, year]
    );
    
    res.status(201).json({ 
      success: true, 
      message: 'Course created successfully',
      data: newCourse.rows[0] 
    });
  } catch (error) {
    console.error('Create course error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to create course' 
    });
  }
});

// PUT /api/courses/:id - Update course
router.put('/:id', authenticateToken, authorizeRoles('instructor', 'admin'), async (req, res) => {
  try {
    const { id } = req.params;
    const { code, name, description, semester, year } = req.body;
    
    // Check if course exists
    const courseCheck = await req.db.query(
      'SELECT instructorId FROM courses WHERE id = $1',
      [id]
    );
    
    if (courseCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Course not found'
      });
    }

    // Instructors can only update their own courses
    if (req.user.role === 'instructor' && courseCheck.rows[0].instructorid !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }

    const updates = [];
    const values = [];
    let paramCount = 1;

    if (code !== undefined) {
      updates.push(`code = $${paramCount++}`);
      values.push(code);
    }
    if (name !== undefined) {
      updates.push(`name = $${paramCount++}`);
      values.push(name);
    }
    if (description !== undefined) {
      updates.push(`description = $${paramCount++}`);
      values.push(description);
    }
    if (semester !== undefined) {
      updates.push(`semester = $${paramCount++}`);
      values.push(semester);
    }
    if (year !== undefined) {
      if (isNaN(year) || year < 2000 || year > 2100) {
        return res.status(400).json({
          success: false,
          message: 'Invalid year'
        });
      }
      updates.push(`year = $${paramCount++}`);
      values.push(year);
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
      UPDATE courses 
      SET ${updates.join(', ')} 
      WHERE id = $${paramCount}
      RETURNING *
    `;

    const result = await req.db.query(query, values);
    
    res.status(200).json({ 
      success: true, 
      message: 'Course updated successfully',
      data: result.rows[0]
    });
  } catch (error) {
    console.error('Update course error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to update course' 
    });
  }
});

// DELETE /api/courses/:id - Delete course
router.delete('/:id', authenticateToken, authorizeRoles('instructor', 'admin'), async (req, res) => {
  try {
    const { id } = req.params;
    
    // Check if course exists
    const courseCheck = await req.db.query(
      'SELECT instructorId FROM courses WHERE id = $1',
      [id]
    );
    
    if (courseCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Course not found'
      });
    }

    // Instructors can only delete their own courses
    if (req.user.role === 'instructor' && courseCheck.rows[0].instructorid !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }

    await req.db.query('DELETE FROM courses WHERE id = $1', [id]);
    
    res.status(200).json({ 
      success: true, 
      message: 'Course deleted successfully' 
    });
  } catch (error) {
    console.error('Delete course error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to delete course' 
    });
  }
});

module.exports = router;
