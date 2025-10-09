package com.attendify.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.attendify.app.data.local.dao.*
import com.attendify.app.data.local.entity.*

/**
 * Main Room database for Attendify app
 * All data is stored locally on device
 */
@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        SessionEntity::class,
        EnrollmentEntity::class,
        AttendanceRecordEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AttendifyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun sessionDao(): SessionDao
    abstract fun enrollmentDao(): EnrollmentDao
    abstract fun attendanceRecordDao(): AttendanceRecordDao
    
    companion object {
        const val DATABASE_NAME = "attendify_database"
    }
}
