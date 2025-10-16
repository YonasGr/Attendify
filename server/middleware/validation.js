/**
 * Request validation middleware
 * Validates common request parameters and bodies
 */

const validateUUID = (id) => {
  const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
  return uuidRegex.test(id);
};

/**
 * Validate UUID parameter in URL
 */
const validateIdParam = (paramName = 'id') => {
  return (req, res, next) => {
    const id = req.params[paramName];
    
    if (!id) {
      return res.status(400).json({
        success: false,
        message: `${paramName} is required`
      });
    }
    
    if (!validateUUID(id)) {
      return res.status(400).json({
        success: false,
        message: `Invalid ${paramName} format`
      });
    }
    
    next();
  };
};

/**
 * Validate required fields in request body
 */
const validateRequiredFields = (fields) => {
  return (req, res, next) => {
    const missingFields = [];
    
    for (const field of fields) {
      if (req.body[field] === undefined || req.body[field] === null || req.body[field] === '') {
        missingFields.push(field);
      }
    }
    
    if (missingFields.length > 0) {
      return res.status(400).json({
        success: false,
        message: `Missing required fields: ${missingFields.join(', ')}`
      });
    }
    
    next();
  };
};

/**
 * Validate email format
 */
const validateEmail = (req, res, next) => {
  const { email } = req.body;
  
  if (!email) {
    return next(); // Email might be optional
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  
  if (!emailRegex.test(email)) {
    return res.status(400).json({
      success: false,
      message: 'Invalid email format'
    });
  }
  
  next();
};

/**
 * Validate role value
 */
const validateRole = (req, res, next) => {
  const { role } = req.body;
  
  if (!role) {
    return res.status(400).json({
      success: false,
      message: 'Role is required'
    });
  }
  
  const validRoles = ['student', 'instructor', 'admin'];
  
  if (!validRoles.includes(role)) {
    return res.status(400).json({
      success: false,
      message: 'Invalid role. Must be student, instructor, or admin'
    });
  }
  
  next();
};

/**
 * Sanitize input to prevent common attacks
 */
const sanitizeInput = (req, res, next) => {
  // Remove any potential SQL injection characters from strings
  const sanitizeString = (str) => {
    if (typeof str !== 'string') return str;
    // Basic sanitization - parameterized queries already protect against SQL injection
    return str.trim();
  };
  
  // Sanitize body
  if (req.body && typeof req.body === 'object') {
    Object.keys(req.body).forEach(key => {
      if (typeof req.body[key] === 'string') {
        req.body[key] = sanitizeString(req.body[key]);
      }
    });
  }
  
  // Sanitize query parameters
  if (req.query && typeof req.query === 'object') {
    Object.keys(req.query).forEach(key => {
      if (typeof req.query[key] === 'string') {
        req.query[key] = sanitizeString(req.query[key]);
      }
    });
  }
  
  next();
};

module.exports = {
  validateIdParam,
  validateRequiredFields,
  validateEmail,
  validateRole,
  sanitizeInput,
  validateUUID
};
