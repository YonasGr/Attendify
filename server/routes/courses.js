const express = require('express');
const router = express.Router();

// GET /api/courses - Get all courses
router.get('/', async (req, res) => {
  try {
    res.status(200).json({ success: true, data: [] });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

// GET /api/courses/:id - Get course by ID
router.get('/:id', async (req, res) => {
  try {
    res.status(200).json({ success: true, data: null });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

// POST /api/courses - Create new course
router.post('/', async (req, res) => {
  try {
    res.status(201).json({ success: true, message: 'Course created' });
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
