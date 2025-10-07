import {
  type User,
  type UpsertUser,
  type InsertUser,
  type Course,
  type InsertCourse,
  type Session,
  type InsertSession,
  type Enrollment,
  type InsertEnrollment,
  type AttendanceRecord,
  type InsertAttendanceRecord,
} from "@shared/schema";
import { randomUUID } from "crypto";

export interface IStorage {
  // User operations (required for Replit Auth)
  getUser(id: string): Promise<User | undefined>;
  upsertUser(user: UpsertUser): Promise<User>;
  getUserByEmail(email: string): Promise<User | undefined>;
  createUser(user: InsertUser): Promise<User>;
  getAllInstructors(): Promise<User[]>;
  getAllStudents(): Promise<User[]>;
  getAllUsers(): Promise<User[]>;
  updateUser(id: string, user: Partial<InsertUser>): Promise<User | undefined>;
  deleteUser(id: string): Promise<boolean>;

  // Course operations
  getCourse(id: string): Promise<Course | undefined>;
  getCourseByCode(code: string): Promise<Course | undefined>;
  createCourse(course: InsertCourse): Promise<Course>;
  getCoursesByInstructor(instructorId: string): Promise<Course[]>;
  getAllCourses(): Promise<Course[]>;
  updateCourse(
    id: string,
    course: Partial<InsertCourse>,
  ): Promise<Course | undefined>;
  deleteCourse(id: string): Promise<boolean>;

  // Enrollment operations
  getEnrollment(id: string): Promise<Enrollment | undefined>;
  createEnrollment(enrollment: InsertEnrollment): Promise<Enrollment>;
  getEnrollmentsByCourse(courseId: string): Promise<Enrollment[]>;
  getEnrollmentsByStudent(studentId: string): Promise<Enrollment[]>;
  deleteEnrollment(id: string): Promise<boolean>;
  isStudentEnrolled(courseId: string, studentId: string): Promise<boolean>;

  // Session operations
  getSession(id: string): Promise<Session | undefined>;
  getSessionByQRCode(qrCode: string): Promise<Session | undefined>;
  createSession(session: InsertSession & { qrCode: string }): Promise<Session>;
  getSessionsByCourse(courseId: string): Promise<Session[]>;
  updateSession(
    id: string,
    session: Partial<Session>,
  ): Promise<Session | undefined>;
  deleteSession(id: string): Promise<boolean>;
  getActiveSessionsByCourse(courseId: string): Promise<Session[]>;

  // Attendance operations
  getAttendanceRecord(id: string): Promise<AttendanceRecord | undefined>;
  createAttendanceRecord(
    record: InsertAttendanceRecord,
  ): Promise<AttendanceRecord>;
  getAttendanceBySession(sessionId: string): Promise<AttendanceRecord[]>;
  getAttendanceByStudent(studentId: string): Promise<AttendanceRecord[]>;
  hasAttendance(sessionId: string, studentId: string): Promise<boolean>;
  getAttendanceByCourseAndStudent(
    courseId: string,
    studentId: string,
  ): Promise<AttendanceRecord[]>;
}

export class MemStorage implements IStorage {
  private users: Map<string, User>;
  private courses: Map<string, Course>;
  private enrollments: Map<string, Enrollment>;
  private sessions: Map<string, Session>;
  private attendanceRecords: Map<string, AttendanceRecord>;

  constructor() {
    this.users = new Map();
    this.courses = new Map();
    this.enrollments = new Map();
    this.sessions = new Map();
    this.attendanceRecords = new Map();
  }

  // User operations
  async getUser(id: string): Promise<User | undefined> {
    return this.users.get(id);
  }

  async upsertUser(userData: UpsertUser): Promise<User> {
    const existing = this.users.get(userData.id!);
    const user: User = {
      ...userData,
      id: userData.id || randomUUID(),
      email: userData.email ?? null,
      firstName: userData.firstName ?? null,
      lastName: userData.lastName ?? null,
      profileImageUrl: userData.profileImageUrl ?? null,
      createdAt: existing?.createdAt ?? new Date(),
      updatedAt: new Date(),
      role: userData.role ?? "student",
      studentId: userData.studentId ?? null,
      department: userData.department ?? null,
    };
    this.users.set(user.id, user);
    return user;
  }

  async getUserByEmail(email: string): Promise<User | undefined> {
    return Array.from(this.users.values()).find((user) => user.email === email);
  }

  async createUser(insertUser: InsertUser): Promise<User> {
    const id = randomUUID();
    const user: User = {
      ...insertUser,
      id,
      email: insertUser.email ?? null,
      firstName: insertUser.firstName ?? null,
      lastName: insertUser.lastName ?? null,
      profileImageUrl: insertUser.profileImageUrl ?? null,
      createdAt: new Date(),
      updatedAt: new Date(),
      role: insertUser.role ?? "student",
      studentId: insertUser.studentId ?? null,
      department: insertUser.department ?? null,
    };
    this.users.set(id, user);
    return user;
  }

  async getAllInstructors(): Promise<User[]> {
    return Array.from(this.users.values()).filter(
      (user) => user.role === "instructor",
    );
  }

  async getAllStudents(): Promise<User[]> {
    return Array.from(this.users.values()).filter(
      (user) => user.role === "student",
    );
  }

  async getAllUsers(): Promise<User[]> {
    return Array.from(this.users.values());
  }

  async updateUser(
    id: string,
    userData: Partial<InsertUser>,
  ): Promise<User | undefined> {
    const user = this.users.get(id);
    if (!user) return undefined;
    const updated = { ...user, ...userData, updatedAt: new Date() };
    this.users.set(id, updated);
    return updated;
  }

  async deleteUser(id: string): Promise<boolean> {
    return this.users.delete(id);
  }

  // Course operations
  async getCourse(id: string): Promise<Course | undefined> {
    return this.courses.get(id);
  }

  async getCourseByCode(code: string): Promise<Course | undefined> {
    return Array.from(this.courses.values()).find(
      (course) => course.code === code,
    );
  }

  async createCourse(insertCourse: InsertCourse): Promise<Course> {
    const id = randomUUID();
    const course: Course = {
      ...insertCourse,
      id,
      description: insertCourse.description ?? null,
    };
    this.courses.set(id, course);
    return course;
  }

  async getCoursesByInstructor(instructorId: string): Promise<Course[]> {
    return Array.from(this.courses.values()).filter(
      (course) => course.instructorId === instructorId,
    );
  }

  async getAllCourses(): Promise<Course[]> {
    return Array.from(this.courses.values());
  }

  async updateCourse(
    id: string,
    courseData: Partial<InsertCourse>,
  ): Promise<Course | undefined> {
    const course = this.courses.get(id);
    if (!course) return undefined;
    const updated = { ...course, ...courseData };
    this.courses.set(id, updated);
    return updated;
  }

  async deleteCourse(id: string): Promise<boolean> {
    return this.courses.delete(id);
  }

  // Enrollment operations
  async getEnrollment(id: string): Promise<Enrollment | undefined> {
    return this.enrollments.get(id);
  }

  async createEnrollment(insertEnrollment: InsertEnrollment): Promise<Enrollment> {
    const id = randomUUID();
    const enrollment: Enrollment = { ...insertEnrollment, id };
    this.enrollments.set(id, enrollment);
    return enrollment;
  }

  async getEnrollmentsByCourse(courseId: string): Promise<Enrollment[]> {
    return Array.from(this.enrollments.values()).filter(
      (enrollment) => enrollment.courseId === courseId,
    );
  }

  async getEnrollmentsByStudent(studentId: string): Promise<Enrollment[]> {
    return Array.from(this.enrollments.values()).filter(
      (enrollment) => enrollment.studentId === studentId,
    );
  }

  async deleteEnrollment(id: string): Promise<boolean> {
    return this.enrollments.delete(id);
  }

  async isStudentEnrolled(
    courseId: string,
    studentId: string,
  ): Promise<boolean> {
    return Array.from(this.enrollments.values()).some(
      (enrollment) =>
        enrollment.courseId === courseId && enrollment.studentId === studentId,
    );
  }

  // Session operations
  async getSession(id: string): Promise<Session | undefined> {
    return this.sessions.get(id);
  }

  async getSessionByQRCode(qrCode: string): Promise<Session | undefined> {
    return Array.from(this.sessions.values()).find(
      (session) => session.qrCode === qrCode,
    );
  }

  async createSession(
    insertSession: InsertSession & { qrCode: string },
  ): Promise<Session> {
    const id = randomUUID();
    const session: Session = { ...insertSession, id, isActive: false };
    this.sessions.set(id, session);
    return session;
  }

  async getSessionsByCourse(courseId: string): Promise<Session[]> {
    return Array.from(this.sessions.values()).filter(
      (session) => session.courseId === courseId,
    );
  }

  async updateSession(
    id: string,
    sessionData: Partial<Session>,
  ): Promise<Session | undefined> {
    const session = this.sessions.get(id);
    if (!session) return undefined;
    const updated = { ...session, ...sessionData };
    this.sessions.set(id, updated);
    return updated;
  }

  async deleteSession(id: string): Promise<boolean> {
    return this.sessions.delete(id);
  }

  async getActiveSessionsByCourse(courseId: string): Promise<Session[]> {
    return Array.from(this.sessions.values()).filter(
      (session) => session.courseId === courseId && session.isActive,
    );
  }

  // Attendance operations
  async getAttendanceRecord(id: string): Promise<AttendanceRecord | undefined> {
    return this.attendanceRecords.get(id);
  }

  async createAttendanceRecord(
    insertRecord: InsertAttendanceRecord,
  ): Promise<AttendanceRecord> {
    const id = randomUUID();
    const record: AttendanceRecord = {
      ...insertRecord,
      id,
      checkedInAt: new Date(),
      status: "present",
    };
    this.attendanceRecords.set(id, record);
    return record;
  }

  async getAttendanceBySession(sessionId: string): Promise<AttendanceRecord[]> {
    return Array.from(this.attendanceRecords.values()).filter(
      (record) => record.sessionId === sessionId,
    );
  }

  async getAttendanceByStudent(studentId: string): Promise<AttendanceRecord[]> {
    return Array.from(this.attendanceRecords.values()).filter(
      (record) => record.studentId === studentId,
    );
  }

  async hasAttendance(sessionId: string, studentId: string): Promise<boolean> {
    return Array.from(this.attendanceRecords.values()).some(
      (record) =>
        record.sessionId === sessionId && record.studentId === studentId,
    );
  }

  async getAttendanceByCourseAndStudent(
    courseId: string,
    studentId: string,
  ): Promise<AttendanceRecord[]> {
    const courseSessions = await this.getSessionsByCourse(courseId);
    const sessionIds = new Set(courseSessions.map((s) => s.id));
    return Array.from(this.attendanceRecords.values()).filter(
      (record) => sessionIds.has(record.sessionId) && record.studentId === studentId,
    );
  }
}

export const storage = new MemStorage();
