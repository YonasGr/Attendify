package com.attendify.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for Enrollment
 */
@Entity(
    tableName = "enrollments",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("courseId"), Index("studentId")]
)
data class EnrollmentEntity(
    @PrimaryKey
    val id: String,
    val courseId: String,
    val studentId: String,
    val enrolledAt: Long = System.currentTimeMillis()
)
