package com.attendify.app.data.local.dao

import androidx.room.*
import com.attendify.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for User operations
 */
@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>
    
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>
    
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
    
    @Query("SELECT COUNT(*) FROM users WHERE role = 'student'")
    fun getStudentCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM users WHERE role = 'instructor'")
    fun getInstructorCount(): Flow<Int>
    
    @Query("UPDATE users SET biometricEnabled = :enabled WHERE id = :userId")
    suspend fun updateBiometricEnabled(userId: String, enabled: Boolean)
}
