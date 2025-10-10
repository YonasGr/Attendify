package com.attendify.app.data.repository

import com.attendify.app.data.local.dao.CourseDao
import com.attendify.app.data.local.dao.EnrollmentDao
import com.attendify.app.data.local.entity.CourseEntity
import com.attendify.app.data.model.Course
import com.attendify.app.data.model.toEntity
import com.attendify.app.data.model.toModel
import com.attendify.app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Course operations with backend integration
 * Uses offline-first approach with backend sync
 */
@Singleton
class CourseRepository @Inject constructor(
    private val courseDao: CourseDao,
    private val enrollmentDao: EnrollmentDao,
    private val networkRepository: NetworkRepository
) {
    
    /**
     * Get all courses from local database
     * Returns Flow for reactive updates
     */
    fun getAllCourses(): Flow<List<CourseEntity>> = courseDao.getAllCourses()
    
    /**
     * Get all courses with backend sync
     * Fetches from backend and updates local database
     */
    fun getAllCoursesWithSync(): Flow<Resource<List<Course>>> = kotlinx.coroutines.flow.flow {
        emit(Resource.Loading())
        
        // First emit local data
        courseDao.getAllCourses().collect { localCourses ->
            val courses = localCourses.map { it.toModel() }
            emit(Resource.Success(courses))
        }
        
        // Then try to sync with backend
        try {
            networkRepository.getAllCourses().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { backendCourses ->
                            // Update local database
                            backendCourses.forEach { course ->
                                courseDao.insertCourse(course.toEntity())
                            }
                            emit(Resource.Success(backendCourses))
                        }
                    }
                    is Resource.Error -> {
                        // Already have local data, just ignore backend error
                    }
                    is Resource.Loading -> { /* Keep showing local data */ }
                }
            }
        } catch (e: Exception) {
            // Silently fail - we already have local data
        }
    }
    
    fun getCourseById(courseId: String): Flow<CourseEntity?> = courseDao.getCourseById(courseId)
    
    fun getCoursesByInstructor(instructorId: String): Flow<List<CourseEntity>> = 
        courseDao.getCoursesByInstructor(instructorId)
    
    fun getCourseCount(): Flow<Int> = courseDao.getCourseCount()
    
    /**
     * Create course locally and sync with backend
     */
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
            
            // Save locally first
            courseDao.insertCourse(course)
            
            // Try to sync with backend
            try {
                networkRepository.createCourse(course.toModel()).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            // Backend created successfully, update local with backend ID if different
                            result.data?.let { backendCourse ->
                                if (backendCourse.id != course.id) {
                                    courseDao.insertCourse(backendCourse.toEntity())
                                }
                            }
                        }
                        is Resource.Error -> {
                            // Already saved locally, backend sync can happen later
                        }
                        is Resource.Loading -> { /* Loading */ }
                    }
                }
            } catch (e: Exception) {
                // Silently fail - course is saved locally
            }
            
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
