package com.attendify.app.data.local.dao

import androidx.room.*
import com.attendify.app.data.local.entity.CourseEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Course operations
 */
@Dao
interface CourseDao {
    
    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun getCourseById(courseId: String): Flow<CourseEntity?>
    
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>
    
    @Query("SELECT * FROM courses")
    suspend fun getAllCoursesOnce(): List<CourseEntity>
    
    @Query("SELECT * FROM courses WHERE instructorId = :instructorId")
    fun getCoursesByInstructor(instructorId: String): Flow<List<CourseEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)
    
    @Update
    suspend fun updateCourse(course: CourseEntity)
    
    @Delete
    suspend fun deleteCourse(course: CourseEntity)
    
    @Query("DELETE FROM courses")
    suspend fun deleteAllCourses()
    
    @Query("SELECT COUNT(*) FROM courses")
    fun getCourseCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM courses WHERE instructorId = :instructorId")
    fun getCourseCountByInstructor(instructorId: String): Flow<Int>
}
