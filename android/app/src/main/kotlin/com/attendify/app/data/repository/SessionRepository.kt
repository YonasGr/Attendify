package com.attendify.app.data.repository

import com.attendify.app.data.local.dao.SessionDao
import com.attendify.app.data.local.entity.SessionEntity
import com.attendify.app.data.model.Session
import com.attendify.app.data.model.toEntity
import com.attendify.app.data.model.toModel
import com.attendify.app.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Session operations with backend integration
 */
@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao,
    private val networkRepository: NetworkRepository
) {
    
    fun getAllSessions(): Flow<List<SessionEntity>> = sessionDao.getAllSessions()
    
    fun getSessionById(sessionId: String): Flow<SessionEntity?> = sessionDao.getSessionById(sessionId)
    
    fun getSessionsByCourse(courseId: String): Flow<List<SessionEntity>> = 
        sessionDao.getSessionsByCourse(courseId)
    
    /**
     * Get sessions for a course with backend sync
     */
    fun getSessionsByCourseWithSync(courseId: String): Flow<Resource<List<Session>>> = kotlinx.coroutines.flow.flow {
        emit(Resource.Loading())
        
        // First emit local data
        sessionDao.getSessionsByCourse(courseId).collect { localSessions ->
            val sessions = localSessions.map { it.toModel() }
            emit(Resource.Success(sessions))
        }
        
        // Try to sync with backend
        try {
            networkRepository.getSessionsByCourse(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { backendSessions ->
                            // Update local database
                            backendSessions.forEach { session ->
                                sessionDao.insertSession(session.toEntity())
                            }
                            emit(Resource.Success(backendSessions))
                        }
                    }
                    is Resource.Error -> {
                        // Already have local data
                    }
                    is Resource.Loading -> { /* Keep showing local data */ }
                }
            }
        } catch (e: Exception) {
            // Silently fail - we already have local data
        }
    }
    
    fun getActiveSessions(): Flow<List<SessionEntity>> = sessionDao.getActiveSessions()
    
    suspend fun getSessionByQrCode(qrCode: String): SessionEntity? = 
        sessionDao.getSessionByQrCode(qrCode)
    
    fun getSessionCountByCourse(courseId: String): Flow<Int> = 
        sessionDao.getSessionCountByCourse(courseId)
    
    /**
     * Create session locally and sync with backend
     */
    suspend fun createSession(
        courseId: String,
        title: String,
        scheduledDate: Long,
        startTime: String,
        endTime: String,
        isActive: Boolean = true
    ): Result<SessionEntity> {
        return try {
            val session = SessionEntity(
                id = UUID.randomUUID().toString(),
                courseId = courseId,
                title = title,
                scheduledDate = scheduledDate,
                startTime = startTime,
                endTime = endTime,
                qrCode = generateQRCode(courseId),
                isActive = isActive
            )
            
            // Save locally first
            sessionDao.insertSession(session)
            
            // Try to sync with backend
            try {
                networkRepository.createSession(session.toModel()).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            // Backend created successfully
                            result.data?.let { backendSession ->
                                if (backendSession.id != session.id) {
                                    sessionDao.insertSession(backendSession.toEntity())
                                }
                            }
                        }
                        is Resource.Error -> {
                            // Already saved locally
                        }
                        is Resource.Loading -> { /* Loading */ }
                    }
                }
            } catch (e: Exception) {
                // Silently fail - session is saved locally
            }
            
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateSession(session: SessionEntity): Result<Unit> {
        return try {
            sessionDao.updateSession(session)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteSession(session: SessionEntity): Result<Unit> {
        return try {
            sessionDao.deleteSession(session)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateQRCode(courseId: String): String {
        return "ATD-${courseId.take(6)}-${UUID.randomUUID().toString().take(8)}"
    }
}
