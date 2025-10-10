package com.attendify.app.data.local.dao

import androidx.room.*
import com.attendify.app.data.local.entity.EnrollmentEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Enrollment operations
 */
@Dao
interface EnrollmentDao {
    
    @Query("SELECT * FROM enrollments WHERE id = :enrollmentId")
    fun getEnrollmentById(enrollmentId: String): Flow<EnrollmentEntity?>
    
    @Query("SELECT * FROM enrollments WHERE courseId = :courseId")
    fun getEnrollmentsByCourse(courseId: String): Flow<List<EnrollmentEntity>>
    
    @Query("SELECT * FROM enrollments WHERE studentId = :studentId")
    fun getEnrollmentsByStudent(studentId: String): Flow<List<EnrollmentEntity>>
    
    @Query("SELECT * FROM enrollments WHERE courseId = :courseId AND studentId = :studentId LIMIT 1")
    suspend fun getEnrollment(courseId: String, studentId: String): EnrollmentEntity?
    
    @Query("SELECT * FROM enrollments")
    fun getAllEnrollments(): Flow<List<EnrollmentEntity>>
    
    @Query("SELECT * FROM enrollments")
    suspend fun getAllEnrollmentsOnce(): List<EnrollmentEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollments(enrollments: List<EnrollmentEntity>)
    
    @Delete
    suspend fun deleteEnrollment(enrollment: EnrollmentEntity)
    
    @Query("DELETE FROM enrollments WHERE courseId = :courseId")
    suspend fun deleteEnrollmentsByCourse(courseId: String)
    
    @Query("SELECT COUNT(*) FROM enrollments WHERE courseId = :courseId")
    fun getEnrollmentCountByCourse(courseId: String): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM enrollments WHERE studentId = :studentId")
    fun getEnrollmentCountByStudent(studentId: String): Flow<Int>
}
