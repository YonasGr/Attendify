package com.attendify.app.data.repository

import com.attendify.app.data.local.dao.AttendanceRecordDao
import com.attendify.app.data.local.entity.AttendanceRecordEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Attendance Record operations
 */
@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceRecordDao: AttendanceRecordDao
) {
    
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecordEntity>> = 
        attendanceRecordDao.getAllAttendanceRecords()
    
    fun getAttendanceRecordById(recordId: String): Flow<AttendanceRecordEntity?> = 
        attendanceRecordDao.getAttendanceRecordById(recordId)
    
    fun getAttendanceBySession(sessionId: String): Flow<List<AttendanceRecordEntity>> = 
        attendanceRecordDao.getAttendanceBySession(sessionId)
    
    fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceRecordEntity>> = 
        attendanceRecordDao.getAttendanceByStudent(studentId)
    
    fun getAttendanceCountBySession(sessionId: String): Flow<Int> = 
        attendanceRecordDao.getAttendanceCountBySession(sessionId)
    
    fun getPresentCountByStudent(studentId: String): Flow<Int> = 
        attendanceRecordDao.getPresentCountByStudent(studentId)
    
    suspend fun checkIn(
        sessionId: String,
        studentId: String,
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
            attendanceRecordDao.insertAttendanceRecord(record)
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
