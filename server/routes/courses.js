const express = require('express');
const { v4: uuidv4 } = require('uuid');
const router = express.Router();

// GET /api/courses - Get all courses
router.get('/', async (req, res) => {
  try {
    const { rows } = await req.db.query('SELECT * FROM courses');
    res.status(200).json({ success: true, data: rows });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

// GET /api/courses/:id - Get course by ID
router.get('/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const { rows } = await req.db.query('SELECT * FROM courses WHERE id = $1', [id]);
    if (rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Course not found' });
    }
    res.status(200).json({ success: true, data: rows[0] });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

// POST /api/courses - Create new course
router.post('/', async (req, res) => {
  try {
    const { code, name, description, instructorId, semester, year } = req.body;
    const id = uuidv4();
    const newCourse = await req.db.query(
      'INSERT INTO courses (id, code, name, description, instructorId, semester, year) VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING *',
      [id, code, name, description, instructorId, semester, year]
    );
    res.status(201).json({ success: true, data: newCourse.rows[0] });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

// PUT /api/courses/:id - Update course
router.put('/:id', async (req, res) => {
  try {
    res.status(200).json({ success: true, message: 'Course updated' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

// DELETE /api/courses/:id - Delete course
router.delete('/:id', async (req, res) => {
  try {
    res.status(200).json({ success: true, message: 'Course deleted' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

module.exports = router;
