package com.attendify.app.data.local.dao

import androidx.room.*
import com.attendify.app.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Session operations
 */
@Dao
interface SessionDao {
    
    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    fun getSessionById(sessionId: String): Flow<SessionEntity?>
    
    @Query("SELECT * FROM sessions WHERE courseId = :courseId ORDER BY scheduledDate DESC")
    fun getSessionsByCourse(courseId: String): Flow<List<SessionEntity>>
    
    @Query("SELECT * FROM sessions WHERE qrCode = :qrCode LIMIT 1")
    suspend fun getSessionByQrCode(qrCode: String): SessionEntity?
    
    @Query("SELECT * FROM sessions WHERE isActive = 1 ORDER BY scheduledDate DESC")
    fun getActiveSessions(): Flow<List<SessionEntity>>
    
    @Query("SELECT * FROM sessions ORDER BY scheduledDate DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<SessionEntity>)
    
    @Update
    suspend fun updateSession(session: SessionEntity)
    
    @Delete
    suspend fun deleteSession(session: SessionEntity)
    
    @Query("DELETE FROM sessions WHERE courseId = :courseId")
    suspend fun deleteSessionsByCourse(courseId: String)
    
    @Query("SELECT COUNT(*) FROM sessions WHERE courseId = :courseId")
    fun getSessionCountByCourse(courseId: String): Flow<Int>
}
