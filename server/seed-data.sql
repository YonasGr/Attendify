-- Attendify Database Seed Data
-- This script populates the database with sample/test data for development and testing

-- Clear existing data (in proper order to respect foreign key constraints)
DELETE FROM attendance_records;
DELETE FROM enrollments;
DELETE FROM sessions;
DELETE FROM courses;
DELETE FROM users;

-- Insert Users
-- Password for all users: 'password123' (hashed with bcrypt, 10 rounds)
-- Hash: $2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS

-- Admin User
INSERT INTO users (id, username, password, email, firstName, lastName, role, studentId, department, createdAt, updatedAt)
VALUES 
('admin-001', 'admin', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'admin@attendify.com', 'System', 'Administrator', 'admin', NULL, NULL, NOW(), NOW());

-- Instructor Users
INSERT INTO users (id, username, password, email, firstName, lastName, role, studentId, department, createdAt, updatedAt)
VALUES 
('instructor-001', 'instructor', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'john.doe@university.edu', 'John', 'Doe', 'instructor', NULL, 'Computer Science', NOW(), NOW()),
('instructor-002', 'jane.smith', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'jane.smith@university.edu', 'Jane', 'Smith', 'instructor', NULL, 'Mathematics', NOW(), NOW()),
('instructor-003', 'robert.jones', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'robert.jones@university.edu', 'Robert', 'Jones', 'instructor', NULL, 'Physics', NOW(), NOW());

-- Student Users
INSERT INTO users (id, username, password, email, firstName, lastName, role, studentId, department, createdAt, updatedAt)
VALUES 
('student-001', 'student', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'alice.johnson@student.university.edu', 'Alice', 'Johnson', 'student', 'CS001', 'Computer Science', NOW(), NOW()),
('student-002', 'bob.williams', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'bob.williams@student.university.edu', 'Bob', 'Williams', 'student', 'CS002', 'Computer Science', NOW(), NOW()),
('student-003', 'carol.jones', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'carol.jones@student.university.edu', 'Carol', 'Jones', 'student', 'MA001', 'Mathematics', NOW(), NOW()),
('student-004', 'david.brown', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'david.brown@student.university.edu', 'David', 'Brown', 'student', 'PH001', 'Physics', NOW(), NOW()),
('student-005', 'emma.wilson', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'emma.wilson@student.university.edu', 'Emma', 'Wilson', 'student', 'CS003', 'Computer Science', NOW(), NOW()),
('student-006', 'frank.taylor', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'frank.taylor@student.university.edu', 'Frank', 'Taylor', 'student', 'MA002', 'Mathematics', NOW(), NOW()),
('student-007', 'grace.lee', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'grace.lee@student.university.edu', 'Grace', 'Lee', 'student', 'PH002', 'Physics', NOW(), NOW()),
('student-008', 'henry.martin', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'henry.martin@student.university.edu', 'Henry', 'Martin', 'student', 'CS004', 'Computer Science', NOW(), NOW()),
('student-009', 'isabel.garcia', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'isabel.garcia@student.university.edu', 'Isabel', 'Garcia', 'student', 'MA003', 'Mathematics', NOW(), NOW()),
('student-010', 'jack.rodriguez', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'jack.rodriguez@student.university.edu', 'Jack', 'Rodriguez', 'student', 'PH003', 'Physics', NOW(), NOW()),
('student-011', 'kate.anderson', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'kate.anderson@student.university.edu', 'Kate', 'Anderson', 'student', 'CS005', 'Computer Science', NOW(), NOW()),
('student-012', 'liam.thomas', '$2b$10$7xfqU4dRJ8LLLLnZkJvdK.FWOHnrYK0DvP4j9W.8xW4oPeV7C/3yS', 'liam.thomas@student.university.edu', 'Liam', 'Thomas', 'student', 'MA004', 'Mathematics', NOW(), NOW());

-- Insert Courses
INSERT INTO courses (id, code, name, description, instructorId, semester, year, createdAt, updatedAt)
VALUES 
-- Computer Science Courses
('course-001', 'CS101', 'Introduction to Programming', 'Fundamentals of programming using Python', 'instructor-001', 'Fall', 2024, NOW(), NOW()),
('course-002', 'CS201', 'Data Structures', 'Advanced data structures and algorithms', 'instructor-001', 'Spring', 2025, NOW(), NOW()),
('course-003', 'CS301', 'Database Systems', 'Database design and SQL programming', 'instructor-001', 'Fall', 2024, NOW(), NOW()),
('course-004', 'CS401', 'Software Engineering', 'Software development lifecycle and best practices', 'instructor-001', 'Spring', 2025, NOW(), NOW()),
-- Mathematics Courses
('course-005', 'MATH101', 'Calculus I', 'Single-variable calculus fundamentals', 'instructor-002', 'Fall', 2024, NOW(), NOW()),
('course-006', 'MATH201', 'Linear Algebra', 'Matrices, vectors, and linear transformations', 'instructor-002', 'Spring', 2025, NOW(), NOW()),
('course-007', 'MATH301', 'Differential Equations', 'Ordinary and partial differential equations', 'instructor-002', 'Fall', 2024, NOW(), NOW()),
('course-008', 'MATH401', 'Real Analysis', 'Advanced calculus and real number theory', 'instructor-002', 'Spring', 2025, NOW(), NOW()),
-- Physics Courses
('course-009', 'PHYS101', 'Physics I: Mechanics', 'Classical mechanics and motion', 'instructor-003', 'Fall', 2024, NOW(), NOW()),
('course-010', 'PHYS201', 'Physics II: Electricity & Magnetism', 'Electromagnetic theory and applications', 'instructor-003', 'Spring', 2025, NOW(), NOW()),
('course-011', 'PHYS301', 'Modern Physics', 'Quantum mechanics and relativity', 'instructor-003', 'Fall', 2024, NOW(), NOW()),
('course-012', 'PHYS401', 'Thermodynamics', 'Heat, energy, and statistical mechanics', 'instructor-003', 'Spring', 2025, NOW(), NOW());

-- Insert Sessions (scheduled class meetings)
INSERT INTO sessions (id, courseId, date, startTime, endTime, qrCode, isActive, createdAt, updatedAt)
VALUES 
-- CS101 Sessions
('session-001', 'course-001', '2024-09-15', '09:00', '10:30', 'QR-CS101-001', true, NOW(), NOW()),
('session-002', 'course-001', '2024-09-17', '09:00', '10:30', 'QR-CS101-002', false, NOW(), NOW()),
('session-003', 'course-001', '2024-09-20', '09:00', '10:30', 'QR-CS101-003', false, NOW(), NOW()),
-- CS201 Sessions
('session-004', 'course-002', '2025-01-15', '10:00', '11:30', 'QR-CS201-001', false, NOW(), NOW()),
('session-005', 'course-002', '2025-01-17', '10:00', '11:30', 'QR-CS201-002', false, NOW(), NOW()),
-- MATH101 Sessions
('session-006', 'course-005', '2024-09-16', '11:00', '12:30', 'QR-MATH101-001', true, NOW(), NOW()),
('session-007', 'course-005', '2024-09-18', '11:00', '12:30', 'QR-MATH101-002', false, NOW(), NOW()),
('session-008', 'course-005', '2024-09-21', '11:00', '12:30', 'QR-MATH101-003', false, NOW(), NOW()),
-- PHYS101 Sessions
('session-009', 'course-009', '2024-09-16', '14:00', '15:30', 'QR-PHYS101-001', true, NOW(), NOW()),
('session-010', 'course-009', '2024-09-18', '14:00', '15:30', 'QR-PHYS101-002', false, NOW(), NOW()),
('session-011', 'course-009', '2024-09-21', '14:00', '15:30', 'QR-PHYS101-003', false, NOW(), NOW());

-- Insert Enrollments
INSERT INTO enrollments (id, studentId, courseId, enrolledAt)
VALUES 
-- CS101 Enrollments
('enrollment-001', 'student-001', 'course-001', NOW()),
('enrollment-002', 'student-002', 'course-001', NOW()),
('enrollment-003', 'student-005', 'course-001', NOW()),
('enrollment-004', 'student-008', 'course-001', NOW()),
('enrollment-005', 'student-011', 'course-001', NOW()),
-- CS201 Enrollments
('enrollment-006', 'student-001', 'course-002', NOW()),
('enrollment-007', 'student-002', 'course-002', NOW()),
-- MATH101 Enrollments
('enrollment-008', 'student-003', 'course-005', NOW()),
('enrollment-009', 'student-006', 'course-005', NOW()),
('enrollment-010', 'student-009', 'course-005', NOW()),
('enrollment-011', 'student-012', 'course-005', NOW()),
-- PHYS101 Enrollments
('enrollment-012', 'student-004', 'course-009', NOW()),
('enrollment-013', 'student-007', 'course-009', NOW()),
('enrollment-014', 'student-010', 'course-009', NOW());

-- Insert Attendance Records
INSERT INTO attendance_records (id, sessionId, studentId, status, timestamp)
VALUES 
-- Session 1 (CS101) Attendance
('attendance-001', 'session-001', 'student-001', 'present', NOW() - INTERVAL '5 days'),
('attendance-002', 'session-001', 'student-002', 'present', NOW() - INTERVAL '5 days'),
('attendance-003', 'session-001', 'student-005', 'late', NOW() - INTERVAL '5 days'),
('attendance-004', 'session-001', 'student-008', 'absent', NOW() - INTERVAL '5 days'),
('attendance-005', 'session-001', 'student-011', 'present', NOW() - INTERVAL '5 days'),
-- Session 2 (CS101) Attendance
('attendance-006', 'session-002', 'student-001', 'present', NOW() - INTERVAL '3 days'),
('attendance-007', 'session-002', 'student-002', 'present', NOW() - INTERVAL '3 days'),
('attendance-008', 'session-002', 'student-005', 'present', NOW() - INTERVAL '3 days'),
('attendance-009', 'session-002', 'student-008', 'late', NOW() - INTERVAL '3 days'),
-- Session 6 (MATH101) Attendance
('attendance-010', 'session-006', 'student-003', 'present', NOW() - INTERVAL '4 days'),
('attendance-011', 'session-006', 'student-006', 'present', NOW() - INTERVAL '4 days'),
('attendance-012', 'session-006', 'student-009', 'present', NOW() - INTERVAL '4 days'),
('attendance-013', 'session-006', 'student-012', 'absent', NOW() - INTERVAL '4 days'),
-- Session 9 (PHYS101) Attendance
('attendance-014', 'session-009', 'student-004', 'present', NOW() - INTERVAL '4 days'),
('attendance-015', 'session-009', 'student-007', 'late', NOW() - INTERVAL '4 days'),
('attendance-016', 'session-009', 'student-010', 'present', NOW() - INTERVAL '4 days');

-- Display summary
SELECT 'Database seeded successfully!' AS status;
SELECT COUNT(*) AS total_users FROM users;
SELECT COUNT(*) AS total_courses FROM courses;
SELECT COUNT(*) AS total_sessions FROM sessions;
SELECT COUNT(*) AS total_enrollments FROM enrollments;
SELECT COUNT(*) AS total_attendance_records FROM attendance_records;

-- Display login credentials for testing
SELECT 
    '=== TEST ACCOUNTS ===' AS info,
    NULL AS username,
    NULL AS password,
    NULL AS role;
    
SELECT 
    NULL AS info,
    'admin' AS username,
    'password123' AS password,
    'Admin' AS role
UNION ALL
SELECT NULL, 'instructor', 'password123', 'Instructor (CS)'
UNION ALL
SELECT NULL, 'jane.smith', 'password123', 'Instructor (Math)'
UNION ALL
SELECT NULL, 'robert.jones', 'password123', 'Instructor (Physics)'
UNION ALL
SELECT NULL, 'student', 'password123', 'Student (Alice Johnson - CS)'
UNION ALL
SELECT NULL, 'bob.williams', 'password123', 'Student (Bob Williams - CS)';
