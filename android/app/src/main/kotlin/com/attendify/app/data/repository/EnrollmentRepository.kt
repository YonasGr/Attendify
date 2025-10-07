package com.attendify.app.data.repository

import com.attendify.app.data.local.dao.EnrollmentDao
import com.attendify.app.data.local.entity.EnrollmentEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Enrollment operations
 */
@Singleton
class EnrollmentRepository @Inject constructor(
    private val enrollmentDao: EnrollmentDao
) {
    
    fun getAllEnrollments(): Flow<List<EnrollmentEntity>> = enrollmentDao.getAllEnrollments()
    
    fun getEnrollmentById(enrollmentId: String): Flow<EnrollmentEntity?> = 
        enrollmentDao.getEnrollmentById(enrollmentId)
    
    fun getEnrollmentsByCourse(courseId: String): Flow<List<EnrollmentEntity>> = 
        enrollmentDao.getEnrollmentsByCourse(courseId)
    
    fun getEnrollmentsByStudent(studentId: String): Flow<List<EnrollmentEntity>> = 
        enrollmentDao.getEnrollmentsByStudent(studentId)
    
    fun getEnrollmentCountByCourse(courseId: String): Flow<Int> = 
        enrollmentDao.getEnrollmentCountByCourse(courseId)
    
    fun getEnrollmentCountByStudent(studentId: String): Flow<Int> = 
        enrollmentDao.getEnrollmentCountByStudent(studentId)
    
    suspend fun enrollStudent(courseId: String, studentId: String): Result<EnrollmentEntity> {
        return try {
            // Check if already enrolled
            val existing = enrollmentDao.getEnrollment(courseId, studentId)
            if (existing != null) {
                return Result.failure(Exception("Student already enrolled in this course"))
            }
            
            val enrollment = EnrollmentEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId,
                studentId = studentId
            )
            enrollmentDao.insertEnrollment(enrollment)
            Result.success(enrollment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unenrollStudent(courseId: String, studentId: String): Result<Unit> {
        return try {
            val enrollment = enrollmentDao.getEnrollment(courseId, studentId)
            if (enrollment != null) {
                enrollmentDao.deleteEnrollment(enrollment)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Enrollment not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteEnrollment(enrollment: EnrollmentEntity): Result<Unit> {
        return try {
            enrollmentDao.deleteEnrollment(enrollment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
