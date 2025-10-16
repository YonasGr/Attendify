const express = require('express');
const bcrypt = require('bcrypt');
const { v4: uuidv4 } = require('uuid');
const { authenticateToken, authorizeRoles } = require('../middleware/auth');
const router = express.Router();

// GET /api/users - Get all users (admin only)
router.get('/', authenticateToken, authorizeRoles('admin'), async (req, res) => {
  try {
    const { role } = req.query;
    
    let query = 'SELECT id, username, email, firstName, lastName, role, studentId, department, createdAt FROM users';
    const params = [];
    
    if (role) {
      query += ' WHERE role = $1';
      params.push(role);
    }
    
    query += ' ORDER BY createdAt DESC';
    
    const { rows } = await req.db.query(query, params);
    
    res.status(200).json({ 
      success: true, 
      data: rows 
    });
  } catch (error) {
    console.error('Get users error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to retrieve users' 
    });
  }
});

// GET /api/users/:id - Get user by ID
router.get('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    
    // Users can only view their own profile unless they're admin
    if (req.user.role !== 'admin' && req.user.id !== id) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }
    
    const { rows } = await req.db.query(
      'SELECT id, username, email, firstName, lastName, role, studentId, department, createdAt FROM users WHERE id = $1',
      [id]
    );
    
    if (rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }
    
    res.status(200).json({ 
      success: true, 
      data: rows[0] 
    });
  } catch (error) {
    console.error('Get user error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to retrieve user' 
    });
  }
});

// POST /api/users - Create new user (admin only)
router.post('/', authenticateToken, authorizeRoles('admin'), async (req, res) => {
  try {
    const { username, password, email, firstName, lastName, role, studentId, department } = req.body;
    
    // Validate required fields
    if (!username || !password || !role) {
      return res.status(400).json({
        success: false,
        message: 'Username, password, and role are required'
      });
    }

    // Validate role
    const validRoles = ['student', 'instructor', 'admin'];
    if (!validRoles.includes(role)) {
      return res.status(400).json({
        success: false,
        message: 'Invalid role'
      });
    }

    // Check if username already exists
    const existing = await req.db.query(
      'SELECT id FROM users WHERE username = $1',
      [username]
    );
    
    if (existing.rows.length > 0) {
      return res.status(409).json({
        success: false,
        message: 'Username already exists'
      });
    }

    // Hash password
    const saltRounds = 10;
    const hashedPassword = await bcrypt.hash(password, saltRounds);

    // Create user
    const id = uuidv4();
    const newUser = await req.db.query(
      `INSERT INTO users (id, username, password, email, firstName, lastName, role, studentId, department)
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
       RETURNING id, username, email, firstName, lastName, role, studentId, department`,
      [id, username, hashedPassword, email, firstName, lastName, role, studentId, department]
    );
    
    res.status(201).json({ 
      success: true, 
      message: 'User created successfully',
      data: newUser.rows[0]
    });
  } catch (error) {
    console.error('Create user error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to create user' 
    });
  }
});

// PUT /api/users/:id - Update user
router.put('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const { email, firstName, lastName, department, password } = req.body;
    
    // Users can only update their own profile unless they're admin
    if (req.user.role !== 'admin' && req.user.id !== id) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }
    
    // Check if user exists
    const userCheck = await req.db.query('SELECT id FROM users WHERE id = $1', [id]);
    if (userCheck.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }

    const updates = [];
    const values = [];
    let paramCount = 1;

    if (email !== undefined) {
      updates.push(`email = $${paramCount++}`);
      values.push(email);
    }
    if (firstName !== undefined) {
      updates.push(`firstName = $${paramCount++}`);
      values.push(firstName);
    }
    if (lastName !== undefined) {
      updates.push(`lastName = $${paramCount++}`);
      values.push(lastName);
    }
    if (department !== undefined) {
      updates.push(`department = $${paramCount++}`);
      values.push(department);
    }
    if (password !== undefined) {
      const hashedPassword = await bcrypt.hash(password, 10);
      updates.push(`password = $${paramCount++}`);
      values.push(hashedPassword);
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
      UPDATE users 
      SET ${updates.join(', ')} 
      WHERE id = $${paramCount}
      RETURNING id, username, email, firstName, lastName, role, studentId, department
    `;

    const result = await req.db.query(query, values);
    
    res.status(200).json({ 
      success: true, 
      message: 'User updated successfully',
      data: result.rows[0]
    });
  } catch (error) {
    console.error('Update user error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to update user' 
    });
  }
});

// DELETE /api/users/:id - Delete user (admin only)
router.delete('/:id', authenticateToken, authorizeRoles('admin'), async (req, res) => {
  try {
    const { id } = req.params;
    
    const result = await req.db.query('DELETE FROM users WHERE id = $1 RETURNING id', [id]);
    
    if (result.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }
    
    res.status(200).json({ 
      success: true, 
      message: 'User deleted successfully' 
    });
  } catch (error) {
    console.error('Delete user error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Failed to delete user' 
    });
  }
});

module.exports = router;
