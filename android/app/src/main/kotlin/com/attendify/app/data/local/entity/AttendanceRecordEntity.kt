package com.attendify.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for Attendance Record
 */
@Entity(
    tableName = "attendance_records",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("studentId")]
)
data class AttendanceRecordEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val studentId: String,
    val checkedInAt: Long = System.currentTimeMillis(),
    val status: String // "present", "late", "absent"
)
