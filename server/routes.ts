import type { Express } from "express";
import { createServer, type Server } from "http";
import { storage } from "./storage";
import { setupAuth, isAuthenticated } from "./replitAuth";
import {
  insertCourseSchema,
  insertEnrollmentSchema,
  insertSessionSchema,
  insertAttendanceRecordSchema,
} from "@shared/schema";
import QRCode from "qrcode";

export async function registerRoutes(app: Express): Promise<Server> {
  // Auth middleware
  await setupAuth(app);

  // Auth routes
  app.get("/api/auth/user", isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const user = await storage.getUser(userId);
      res.json(user);
    } catch (error) {
      console.error("Error fetching user:", error);
      res.status(500).json({ message: "Failed to fetch user" });
    }
  });

  // User routes
  app.get("/api/users", isAuthenticated, async (req, res) => {
    try {
      const users = await storage.getAllUsers();
      res.json(users);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch users" });
    }
  });

  app.get("/api/users/students", isAuthenticated, async (req, res) => {
    try {
      const students = await storage.getAllStudents();
      res.json(students);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch students" });
    }
  });

  app.get("/api/users/instructors", isAuthenticated, async (req, res) => {
    try {
      const instructors = await storage.getAllInstructors();
      res.json(instructors);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch instructors" });
    }
  });

  app.patch("/api/users/:id", isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const { id } = req.params;

      // Only allow users to update their own profile or admins to update any
      const currentUser = await storage.getUser(userId);
      if (currentUser?.role !== "admin" && userId !== id) {
        return res.status(403).json({ message: "Forbidden" });
      }

      const updated = await storage.updateUser(id, req.body);
      if (!updated) {
        return res.status(404).json({ message: "User not found" });
      }
      res.json(updated);
    } catch (error) {
      res.status(500).json({ message: "Failed to update user" });
    }
  });

  // Course routes
  app.get("/api/courses", isAuthenticated, async (req, res) => {
    try {
      const courses = await storage.getAllCourses();
      res.json(courses);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch courses" });
    }
  });

  app.get("/api/courses/:id", isAuthenticated, async (req, res) => {
    try {
      const course = await storage.getCourse(req.params.id);
      if (!course) {
        return res.status(404).json({ message: "Course not found" });
      }
      res.json(course);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch course" });
    }
  });

  app.post("/api/courses", isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const user = await storage.getUser(userId);

      // Only instructors and admins can create courses
      if (user?.role !== "instructor" && user?.role !== "admin") {
        return res.status(403).json({ message: "Forbidden" });
      }

      const validated = insertCourseSchema.parse(req.body);
      const course = await storage.createCourse(validated);
      res.status(201).json(course);
    } catch (error: any) {
      res.status(400).json({ message: error.message || "Failed to create course" });
    }
  });

  app.get("/api/courses/instructor/:instructorId", isAuthenticated, async (req, res) => {
    try {
      const courses = await storage.getCoursesByInstructor(req.params.instructorId);
      res.json(courses);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch courses" });
    }
  });

  // Enrollment routes
  app.get("/api/enrollments/course/:courseId", isAuthenticated, async (req, res) => {
    try {
      const enrollments = await storage.getEnrollmentsByCourse(req.params.courseId);
      res.json(enrollments);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch enrollments" });
    }
  });

  app.get("/api/enrollments/student/:studentId", isAuthenticated, async (req, res) => {
    try {
      const enrollments = await storage.getEnrollmentsByStudent(req.params.studentId);
      res.json(enrollments);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch enrollments" });
    }
  });

  app.post("/api/enrollments", isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const user = await storage.getUser(userId);

      // Only instructors and admins can enroll students
      if (user?.role !== "instructor" && user?.role !== "admin") {
        return res.status(403).json({ message: "Forbidden" });
      }

      const validated = insertEnrollmentSchema.parse(req.body);
      const enrollment = await storage.createEnrollment(validated);
      res.status(201).json(enrollment);
    } catch (error: any) {
      res.status(400).json({ message: error.message || "Failed to create enrollment" });
    }
  });

  app.delete("/api/enrollments/:id", isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const user = await storage.getUser(userId);

      if (user?.role !== "instructor" && user?.role !== "admin") {
        return res.status(403).json({ message: "Forbidden" });
      }

      const deleted = await storage.deleteEnrollment(req.params.id);
      if (!deleted) {
        return res.status(404).json({ message: "Enrollment not found" });
      }
      res.status(204).send();
    } catch (error) {
      res.status(500).json({ message: "Failed to delete enrollment" });
    }
  });

  // Session routes
  app.get("/api/sessions/course/:courseId", isAuthenticated, async (req, res) => {
    try {
      const sessions = await storage.getSessionsByCourse(req.params.courseId);
      res.json(sessions);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch sessions" });
    }
  });

  app.get("/api/sessions/:id", isAuthenticated, async (req, res) => {
    try {
      const session = await storage.getSession(req.params.id);
      if (!session) {
        return res.status(404).json({ message: "Session not found" });
      }
      res.json(session);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch session" });
    }
  });

  app.post("/api/sessions", isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const user = await storage.getUser(userId);

      if (user?.role !== "instructor" && user?.role !== "admin") {
        return res.status(403).json({ message: "Forbidden" });
      }

      const validated = insertSessionSchema.parse(req.body);
      
      // Generate unique QR code data
      const qrCodeData = `${validated.courseId}-${Date.now()}-${Math.random().toString(36).substring(7)}`;
      
      const session = await storage.createSession({
        ...validated,
        qrCode: qrCodeData,
      });

      res.status(201).json(session);
    } catch (error: any) {
      res.status(400).json({ message: error.message || "Failed to create session" });
    }
  });

  app.patch("/api/sessions/:id", isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const user = await storage.getUser(userId);

      if (user?.role !== "instructor" && user?.role !== "admin") {
        return res.status(403).json({ message: "Forbidden" });
      }

      const updated = await storage.updateSession(req.params.id, req.body);
      if (!updated) {
        return res.status(404).json({ message: "Session not found" });
      }
      res.json(updated);
    } catch (error) {
      res.status(500).json({ message: "Failed to update session" });
    }
  });

  app.get("/api/sessions/:id/qrcode", isAuthenticated, async (req, res) => {
    try {
      const session = await storage.getSession(req.params.id);
      if (!session) {
        return res.status(404).json({ message: "Session not found" });
      }

      const qrCodeUrl = await QRCode.toDataURL(session.qrCode);
      res.json({ qrCode: session.qrCode, qrCodeUrl });
    } catch (error) {
      res.status(500).json({ message: "Failed to generate QR code" });
    }
  });

  // Attendance routes
  app.post("/api/attendance/checkin", isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const { qrCode } = req.body;

      if (!qrCode) {
        return res.status(400).json({ message: "QR code is required" });
      }

      const session = await storage.getSessionByQRCode(qrCode);
      if (!session) {
        return res.status(404).json({ message: "Invalid QR code" });
      }

      // Check if session is active
      const now = new Date();
      if (now < new Date(session.startTime) || now > new Date(session.endTime)) {
        return res.status(400).json({ message: "Session is not active" });
      }

      // Check if student is enrolled
      const isEnrolled = await storage.isStudentEnrolled(session.courseId, userId);
      if (!isEnrolled) {
        return res.status(403).json({ message: "Not enrolled in this course" });
      }

      // Check if already checked in
      const hasAttendance = await storage.hasAttendance(session.id, userId);
      if (hasAttendance) {
        return res.status(400).json({ message: "Already checked in" });
      }

      const attendance = await storage.createAttendanceRecord({
        sessionId: session.id,
        studentId: userId,
      });

      res.status(201).json(attendance);
    } catch (error: any) {
      res.status(500).json({ message: error.message || "Failed to check in" });
    }
  });

  app.get("/api/attendance/session/:sessionId", isAuthenticated, async (req, res) => {
    try {
      const attendance = await storage.getAttendanceBySession(req.params.sessionId);
      res.json(attendance);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch attendance" });
    }
  });

  app.get("/api/attendance/student/:studentId", isAuthenticated, async (req, res) => {
    try {
      const attendance = await storage.getAttendanceByStudent(req.params.studentId);
      res.json(attendance);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch attendance" });
    }
  });

  app.get("/api/attendance/course/:courseId/student/:studentId", isAuthenticated, async (req, res) => {
    try {
      const attendance = await storage.getAttendanceByCourseAndStudent(
        req.params.courseId,
        req.params.studentId,
      );
      res.json(attendance);
    } catch (error) {
      res.status(500).json({ message: "Failed to fetch attendance" });
    }
  });

  const httpServer = createServer(app);
  return httpServer;
}
