package com.attendify.app.data.repository

import com.attendify.app.data.local.dao.CourseDao
import com.attendify.app.data.local.dao.EnrollmentDao
import com.attendify.app.data.local.entity.CourseEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Course operations
 */
@Singleton
class CourseRepository @Inject constructor(
    private val courseDao: CourseDao,
    private val enrollmentDao: EnrollmentDao
) {
    
    fun getAllCourses(): Flow<List<CourseEntity>> = courseDao.getAllCourses()
    
    fun getCourseById(courseId: String): Flow<CourseEntity?> = courseDao.getCourseById(courseId)
    
    fun getCoursesByInstructor(instructorId: String): Flow<List<CourseEntity>> = 
        courseDao.getCoursesByInstructor(instructorId)
    
    fun getCourseCount(): Flow<Int> = courseDao.getCourseCount()
    
    suspend fun createCourse(
        code: String,
        name: String,
        description: String?,
        instructorId: String,
        semester: String,
        year: Int
    ): Result<CourseEntity> {
        return try {
            val course = CourseEntity(
                id = UUID.randomUUID().toString(),
                code = code,
                name = name,
                description = description,
                instructorId = instructorId,
                semester = semester,
                year = year
            )
            courseDao.insertCourse(course)
            Result.success(course)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateCourse(course: CourseEntity): Result<Unit> {
        return try {
            courseDao.updateCourse(course)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteCourse(course: CourseEntity): Result<Unit> {
        return try {
            courseDao.deleteCourse(course)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
