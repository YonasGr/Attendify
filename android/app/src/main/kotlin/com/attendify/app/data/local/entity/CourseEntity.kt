package com.attendify.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for Course
 */
@Entity(
    tableName = "courses",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["instructorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("instructorId")]
)
data class CourseEntity(
    @PrimaryKey
    val id: String,
    val code: String,
    val name: String,
    val description: String?,
    val instructorId: String,
    val semester: String,
    val year: Int,
    val createdAt: Long = System.currentTimeMillis()
)
