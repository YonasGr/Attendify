package com.attendify.app.data.repository

import com.attendify.app.data.local.dao.SessionDao
import com.attendify.app.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Session operations
 */
@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao
) {
    
    fun getAllSessions(): Flow<List<SessionEntity>> = sessionDao.getAllSessions()
    
    fun getSessionById(sessionId: String): Flow<SessionEntity?> = sessionDao.getSessionById(sessionId)
    
    fun getSessionsByCourse(courseId: String): Flow<List<SessionEntity>> = 
        sessionDao.getSessionsByCourse(courseId)
    
    fun getActiveSessions(): Flow<List<SessionEntity>> = sessionDao.getActiveSessions()
    
    suspend fun getSessionByQrCode(qrCode: String): SessionEntity? = 
        sessionDao.getSessionByQrCode(qrCode)
    
    fun getSessionCountByCourse(courseId: String): Flow<Int> = 
        sessionDao.getSessionCountByCourse(courseId)
    
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
            sessionDao.insertSession(session)
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
