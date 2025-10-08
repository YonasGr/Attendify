package com.attendify.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for User
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val password: String, // In production, this should be hashed
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val profileImageUrl: String?,
    val role: String, // "student", "instructor", "admin"
    val studentId: String?,
    val department: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
