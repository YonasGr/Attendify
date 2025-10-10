package com.attendify.app.data.repository

import com.attendify.app.data.local.dao.AttendanceRecordDao
import com.attendify.app.data.local.entity.AttendanceRecordEntity
import com.attendify.app.data.model.AttendanceRecord
import com.attendify.app.data.model.toEntity
import com.attendify.app.data.model.toModel
import com.attendify.app.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Attendance Record operations with backend integration
 */
@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceRecordDao: AttendanceRecordDao,
    private val networkRepository: NetworkRepository
) {
    
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecordEntity>> = 
        attendanceRecordDao.getAllAttendanceRecords()
    
    fun getAttendanceRecordById(recordId: String): Flow<AttendanceRecordEntity?> = 
        attendanceRecordDao.getAttendanceRecordById(recordId)
    
    fun getAttendanceBySession(sessionId: String): Flow<List<AttendanceRecordEntity>> = 
        attendanceRecordDao.getAttendanceBySession(sessionId)
    
    /**
     * Get attendance for a session with backend sync
     */
    fun getAttendanceBySessionWithSync(sessionId: String): Flow<Resource<List<AttendanceRecord>>> = kotlinx.coroutines.flow.flow {
        emit(Resource.Loading())
        
        // First emit local data
        attendanceRecordDao.getAttendanceBySession(sessionId).collect { localRecords ->
            val records = localRecords.map { it.toModel() }
            emit(Resource.Success(records))
        }
        
        // Try to sync with backend
        try {
            networkRepository.getAttendanceBySession(sessionId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { backendRecords ->
                            // Update local database
                            backendRecords.forEach { record ->
                                attendanceRecordDao.insertAttendanceRecord(record.toEntity())
                            }
                            emit(Resource.Success(backendRecords))
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
    
    fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceRecordEntity>> = 
        attendanceRecordDao.getAttendanceByStudent(studentId)
    
    /**
     * Get attendance for a student with backend sync
     */
    fun getAttendanceByStudentWithSync(studentId: String): Flow<Resource<List<AttendanceRecord>>> = kotlinx.coroutines.flow.flow {
        emit(Resource.Loading())
        
        // First emit local data
        attendanceRecordDao.getAttendanceByStudent(studentId).collect { localRecords ->
            val records = localRecords.map { it.toModel() }
            emit(Resource.Success(records))
        }
        
        // Try to sync with backend
        try {
            networkRepository.getAttendanceByStudent(studentId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { backendRecords ->
                            // Update local database
                            backendRecords.forEach { record ->
                                attendanceRecordDao.insertAttendanceRecord(record.toEntity())
                            }
                            emit(Resource.Success(backendRecords))
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
    
    fun getAttendanceCountBySession(sessionId: String): Flow<Int> = 
        attendanceRecordDao.getAttendanceCountBySession(sessionId)
    
    fun getPresentCountByStudent(studentId: String): Flow<Int> = 
        attendanceRecordDao.getPresentCountByStudent(studentId)
    
    /**
     * Check in to a session locally and sync with backend
     */
    suspend fun checkIn(
        sessionId: String,
        studentId: String,
        qrCode: String? = null,
        status: String = "present"
    ): Result<AttendanceRecordEntity> {
        return try {
            // Check if already checked in
            val existing = attendanceRecordDao.getAttendanceRecord(sessionId, studentId)
            if (existing != null) {
                return Result.failure(Exception("Already checked in to this session"))
            }
            
            val record = AttendanceRecordEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                studentId = studentId,
                checkedInAt = System.currentTimeMillis(),
                status = status
            )
            
            // Save locally first
            attendanceRecordDao.insertAttendanceRecord(record)
            
            // Try to sync with backend
            try {
                networkRepository.markAttendance(sessionId, studentId, qrCode).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            // Backend marked successfully
                            result.data?.let { backendRecord ->
                                if (backendRecord.id != record.id) {
                                    attendanceRecordDao.insertAttendanceRecord(backendRecord.toEntity())
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
                // Silently fail - attendance is saved locally
            }
            
            Result.success(record)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateAttendanceRecord(record: AttendanceRecordEntity): Result<Unit> {
        return try {
            attendanceRecordDao.updateAttendanceRecord(record)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteAttendanceRecord(record: AttendanceRecordEntity): Result<Unit> {
        return try {
            attendanceRecordDao.deleteAttendanceRecord(record)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeAttendance(sessionId: String, studentId: String): Result<Unit> {
        return try {
            val record = attendanceRecordDao.getAttendanceRecord(sessionId, studentId)
            if (record != null) {
                attendanceRecordDao.deleteAttendanceRecord(record)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Attendance record not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
