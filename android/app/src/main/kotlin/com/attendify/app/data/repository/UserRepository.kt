package com.attendify.app.data.repository

import com.attendify.app.data.local.dao.UserDao
import com.attendify.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for User operations
 */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    
    fun getUserById(userId: String): Flow<UserEntity?> = userDao.getUserById(userId)
    
    fun getUsersByRole(role: String): Flow<List<UserEntity>> = userDao.getUsersByRole(role)
    
    fun getStudentCount(): Flow<Int> = userDao.getStudentCount()
    
    fun getInstructorCount(): Flow<Int> = userDao.getInstructorCount()
    
    suspend fun createUser(
        username: String,
        password: String,
        email: String?,
        firstName: String?,
        lastName: String?,
        role: String,
        studentId: String? = null,
        department: String? = null
    ): Result<UserEntity> {
        return try {
            // Check if username already exists
            val existing = userDao.getUserByUsername(username)
            if (existing != null) {
                return Result.failure(Exception("Username already exists"))
            }
            
            val user = UserEntity(
                id = UUID.randomUUID().toString(),
                username = username,
                password = password, // In production, hash this
                email = email,
                firstName = firstName,
                lastName = lastName,
                profileImageUrl = null,
                role = role,
                studentId = studentId,
                department = department
            )
            userDao.insertUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUser(user: UserEntity): Result<Unit> {
        return try {
            userDao.updateUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteUser(user: UserEntity): Result<Unit> {
        return try {
            userDao.deleteUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
