package com.attendify.app.di

import android.content.Context
import androidx.room.Room
import com.attendify.app.data.local.AttendifyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAttendifyDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AttendifyDatabase::class.java,
        "attendify_db"
    ).build()

    @Singleton
    @Provides
    fun provideUserDao(db: AttendifyDatabase) = db.userDao()

    @Singleton
    @Provides
    fun provideCourseDao(db: AttendifyDatabase) = db.courseDao()

    @Singleton
    @Provides
    fun provideSessionDao(db: AttendifyDatabase) = db.sessionDao()

    @Singleton
    @Provides
    fun provideEnrollmentDao(db: AttendifyDatabase) = db.enrollmentDao()

    @Singleton
    @Provides
    fun provideAttendanceRecordDao(db: AttendifyDatabase) = db.attendanceRecordDao()
}
