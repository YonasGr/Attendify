package com.attendify.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.attendify.app.data.api.toDomainModel
import com.attendify.app.data.local.dao.*
import com.attendify.app.data.model.*
import com.attendify.app.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

private val Context.syncDataStore by preferencesDataStore(name = "sync_prefs")

/**
 * Manager for handling data synchronization between local and remote databases
 * Implements offline-first architecture with conflict resolution
 */
@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkRepository: NetworkRepository,
    private val userDao: UserDao,
    private val courseDao: CourseDao,
    private val sessionDao: SessionDao,
    private val enrollmentDao: EnrollmentDao,
    private val attendanceRecordDao: AttendanceRecordDao
) {
    
    private val dataStore = context.syncDataStore
    
    companion object {
        private val LAST_SYNC_TIME_KEY = longPreferencesKey("last_sync_time")
    }
    
    /**
     * Get last sync time
     */
    fun getLastSyncTime(): Flow<Long?> = dataStore.data.map { preferences ->
        preferences[LAST_SYNC_TIME_KEY]
    }
    
    /**
     * Save last sync time
     */
    private suspend fun saveLastSyncTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIME_KEY] = time
        }
    }
    
    /**
     * Perform full sync: upload local changes and download remote changes
     */
    fun performFullSync(): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        
        try {
            // Step 1: Upload local changes
            emit(Resource.Loading("Uploading local changes..."))
            uploadLocalChanges().collect { uploadResult ->
                when (uploadResult) {
                    is Resource.Error -> {
                        emit(Resource.Error("Upload failed: ${uploadResult.message}"))
                        return@collect
                    }
                    is Resource.Success -> {
                        // Continue to download
                    }
                    is Resource.Loading -> { /* Keep loading */ }
                }
            }
            
            // Step 2: Download remote changes
            emit(Resource.Loading("Downloading remote changes..."))
            downloadRemoteChanges().collect { downloadResult ->
                when (downloadResult) {
                    is Resource.Error -> {
                        emit(Resource.Error("Download failed: ${downloadResult.message}"))
                    }
                    is Resource.Success -> {
                        saveLastSyncTime(System.currentTimeMillis())
                        emit(Resource.Success("Sync completed successfully"))
                    }
                    is Resource.Loading -> { /* Keep loading */ }
                }
            }
            
        } catch (e: Exception) {
            emit(Resource.Error("Sync failed: ${e.message}"))
        }
    }
    
    /**
     * Upload local changes to server
     */
    private fun uploadLocalChanges(): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        
        try {
            // Gather local data
            val users = userDao.getAllUsersOnce().map { it.toModel() }
            val courses = courseDao.getAllCoursesOnce().map { it.toModel() }
            val sessions = sessionDao.getAllSessionsOnce().map { it.toModel() }
            val enrollments = enrollmentDao.getAllEnrollmentsOnce().map { it.toModel() }
            val attendance = attendanceRecordDao.getAllAttendanceRecordsOnce().map { it.toModel() }
            
            // Upload to server
            networkRepository.syncUpload(
                users = users,
                courses = courses,
                sessions = sessions,
                enrollments = enrollments,
                attendance = attendance
            ).collect { result ->
                emit(result)
            }
        } catch (e: Exception) {
            emit(Resource.Error("Failed to upload: ${e.message}"))
        }
    }
    
    /**
     * Download remote changes from server
     */
    private fun downloadRemoteChanges(): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        
        try {
            networkRepository.syncDownload().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { syncData ->
                            // Save users
                            syncData.users.forEach { userDto ->
                                val user = userDto.toDomainModel()
                                userDao.insertUser(user.toEntity())
                            }
                            
                            // Save courses
                            syncData.courses.forEach { courseDto ->
                                courseDao.insertCourse(courseDto.toDomainModel().toEntity())
                            }
                            
                            // Save sessions
                            syncData.sessions.forEach { sessionDto ->
                                sessionDao.insertSession(sessionDto.toDomainModel().toEntity())
                            }
                            
                            // Save enrollments
                            syncData.enrollments.forEach { enrollmentDto ->
                                enrollmentDao.insertEnrollment(enrollmentDto.toDomainModel().toEntity())
                            }
                            
                            // Save attendance
                            syncData.attendance.forEach { attendanceDto ->
                                attendanceRecordDao.insertAttendanceRecord(attendanceDto.toDomainModel().toEntity())
                            }
                            
                            emit(Resource.Success("Downloaded and saved remote changes"))
                        } ?: emit(Resource.Error("No data received"))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Download failed"))
                    }
                    is Resource.Loading -> {
                        emit(Resource.Loading())
                    }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("Failed to download: ${e.message}"))
        }
    }
    
    /**
     * Sync courses only
     */
    fun syncCourses(): Flow<Resource<List<Course>>> = flow {
        emit(Resource.Loading())
        
        try {
            networkRepository.getAllCourses().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { courses ->
                            // Save to local database
                            courses.forEach { course ->
                                courseDao.insertCourse(course.toEntity())
                            }
                            emit(Resource.Success(courses))
                        }
                    }
                    is Resource.Error -> {
                        // Fall back to local data
                        val localCourses = courseDao.getAllCoursesOnce().map { it.toModel() }
                        emit(Resource.Success(localCourses))
                    }
                    is Resource.Loading -> emit(Resource.Loading())
                }
            }
        } catch (e: Exception) {
            // Fall back to local data on error
            val localCourses = courseDao.getAllCoursesOnce().map { it.toModel() }
            emit(Resource.Success(localCourses))
        }
    }
    
    /**
     * Sync sessions for a course
     */
    fun syncSessions(courseId: String): Flow<Resource<List<Session>>> = flow {
        emit(Resource.Loading())
        
        try {
            networkRepository.getSessionsByCourse(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { sessions ->
                            // Save to local database
                            sessions.forEach { session ->
                                sessionDao.insertSession(session.toEntity())
                            }
                            emit(Resource.Success(sessions))
                        }
                    }
                    is Resource.Error -> {
                        // Fall back to local data
                        val localSessions = sessionDao.getSessionsForCourseOnce(courseId).map { it.toModel() }
                        emit(Resource.Success(localSessions))
                    }
                    is Resource.Loading -> emit(Resource.Loading())
                }
            }
        } catch (e: Exception) {
            // Fall back to local data on error
            val localSessions = sessionDao.getSessionsForCourseOnce(courseId).map { it.toModel() }
            emit(Resource.Success(localSessions))
        }
    }
}
