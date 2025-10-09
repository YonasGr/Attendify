const express = require('express');
const router = express.Router();

// GET /api/enrollments - Get all enrollments
router.get('/', async (req, res) => {
  try {
    res.status(200).json({ success: true, data: [] });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

// POST /api/enrollments - Create new enrollment
router.post('/', async (req, res) => {
  try {
    res.status(201).json({ success: true, message: 'Enrollment created' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

// DELETE /api/enrollments/:id - Delete enrollment
router.delete('/:id', async (req, res) => {
  try {
    res.status(200).json({ success: true, message: 'Enrollment deleted' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

module.exports = router;
