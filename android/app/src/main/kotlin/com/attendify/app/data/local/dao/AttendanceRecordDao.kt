package com.attendify.app.data.local.dao

import androidx.room.*
import com.attendify.app.data.local.entity.AttendanceRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Attendance Record operations
 */
@Dao
interface AttendanceRecordDao {
    
    @Query("SELECT * FROM attendance_records WHERE id = :recordId")
    fun getAttendanceRecordById(recordId: String): Flow<AttendanceRecordEntity?>
    
    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId")
    fun getAttendanceBySession(sessionId: String): Flow<List<AttendanceRecordEntity>>
    
    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId ORDER BY checkedInAt DESC")
    fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceRecordEntity>>
    
    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId AND studentId = :studentId LIMIT 1")
    suspend fun getAttendanceRecord(sessionId: String, studentId: String): AttendanceRecordEntity?
    
    @Query("SELECT * FROM attendance_records")
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecordEntity>>
    
    @Query("SELECT * FROM attendance_records")
    suspend fun getAllAttendanceRecordsOnce(): List<AttendanceRecordEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(record: AttendanceRecordEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(records: List<AttendanceRecordEntity>)
    
    @Update
    suspend fun updateAttendanceRecord(record: AttendanceRecordEntity)
    
    @Delete
    suspend fun deleteAttendanceRecord(record: AttendanceRecordEntity)
    
    @Query("DELETE FROM attendance_records WHERE sessionId = :sessionId")
    suspend fun deleteAttendanceBySession(sessionId: String)
    
    @Query("SELECT COUNT(*) FROM attendance_records WHERE sessionId = :sessionId")
    fun getAttendanceCountBySession(sessionId: String): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM attendance_records WHERE studentId = :studentId AND status = 'present'")
    fun getPresentCountByStudent(studentId: String): Flow<Int>
}
