import { sql } from "drizzle-orm";
import {
  pgTable,
  text,
  varchar,
  timestamp,
  integer,
  boolean,
  index,
  jsonb,
} from "drizzle-orm/pg-core";
import { createInsertSchema } from "drizzle-zod";
import { z } from "zod";

// Session storage table (required for Replit Auth)
export const sessions = pgTable(
  "sessions",
  {
    sid: varchar("sid").primaryKey(),
    sess: jsonb("sess").notNull(),
    expire: timestamp("expire").notNull(),
  },
  (table) => [index("IDX_session_expire").on(table.expire)],
);

// Users table (extended for Replit Auth + attendance system)
export const users = pgTable("users", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  email: varchar("email").unique(),
  firstName: varchar("first_name"),
  lastName: varchar("last_name"),
  profileImageUrl: varchar("profile_image_url"),
  createdAt: timestamp("created_at").defaultNow(),
  updatedAt: timestamp("updated_at").defaultNow(),
  // Attendance system fields
  role: text("role").notNull().default("student"), // 'student', 'instructor', 'admin'
  studentId: text("student_id"), // For students - their university ID
  department: text("department"),
});

export const courses = pgTable("courses", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  code: text("code").notNull().unique(),
  name: text("name").notNull(),
  description: text("description"),
  instructorId: varchar("instructor_id")
    .notNull()
    .references(() => users.id),
  semester: text("semester").notNull(),
  year: integer("year").notNull(),
});

export const enrollments = pgTable("enrollments", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  courseId: varchar("course_id")
    .notNull()
    .references(() => courses.id),
  studentId: varchar("student_id")
    .notNull()
    .references(() => users.id),
});

export const sessions_attendance = pgTable("sessions_attendance", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  courseId: varchar("course_id")
    .notNull()
    .references(() => courses.id),
  title: text("title").notNull(),
  scheduledDate: timestamp("scheduled_date").notNull(),
  startTime: timestamp("start_time").notNull(),
  endTime: timestamp("end_time").notNull(),
  qrCode: text("qr_code").notNull().unique(),
  isActive: boolean("is_active").notNull().default(false),
});

export const attendanceRecords = pgTable("attendance_records", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  sessionId: varchar("session_id")
    .notNull()
    .references(() => sessions_attendance.id),
  studentId: varchar("student_id")
    .notNull()
    .references(() => users.id),
  checkedInAt: timestamp("checked_in_at").notNull().default(sql`now()`),
  status: text("status").notNull().default("present"), // 'present', 'late', 'absent'
});

// Upsert schema for Replit Auth
export const upsertUserSchema = createInsertSchema(users);
export type UpsertUser = z.infer<typeof upsertUserSchema>;
export type User = typeof users.$inferSelect;

// Insert schemas
export const insertUserSchema = createInsertSchema(users).omit({
  id: true,
  createdAt: true,
  updatedAt: true,
});

export const insertCourseSchema = createInsertSchema(courses).omit({
  id: true,
});

export const insertEnrollmentSchema = createInsertSchema(enrollments).omit({
  id: true,
});

export const insertSessionSchema = createInsertSchema(sessions_attendance).omit({
  id: true,
  qrCode: true,
  isActive: true,
});

export const insertAttendanceRecordSchema = createInsertSchema(
  attendanceRecords,
).omit({
  id: true,
  checkedInAt: true,
  status: true,
});

// Types
export type InsertUser = z.infer<typeof insertUserSchema>;

export type InsertCourse = z.infer<typeof insertCourseSchema>;
export type Course = typeof courses.$inferSelect;

export type InsertEnrollment = z.infer<typeof insertEnrollmentSchema>;
export type Enrollment = typeof enrollments.$inferSelect;

export type InsertSession = z.infer<typeof insertSessionSchema>;
export type Session = typeof sessions_attendance.$inferSelect;

export type InsertAttendanceRecord = z.infer<typeof insertAttendanceRecordSchema>;
export type AttendanceRecord = typeof attendanceRecords.$inferSelect;
