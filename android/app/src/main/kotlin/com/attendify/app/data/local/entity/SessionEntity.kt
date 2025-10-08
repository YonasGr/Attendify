package com.attendify.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for Session
 */
@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("courseId")]
)
data class SessionEntity(
    @PrimaryKey
    val id: String,
    val courseId: String,
    val title: String,
    val scheduledDate: Long, // Timestamp
    val startTime: String, // HH:mm format
    val endTime: String,   // HH:mm format
    val qrCode: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
