package com.attendify.app.di

import android.content.Context
import androidx.room.Room
import com.attendify.app.data.local.AttendifyDatabase
import com.attendify.app.data.local.DatabaseSeeder
import com.attendify.app.data.local.dao.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for dependency injection
 * Provides Room database, DAOs, and utility dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    
    @Provides
    @Singleton
    fun provideAttendifyDatabase(
        @ApplicationContext context: Context
    ): AttendifyDatabase {
        return Room.databaseBuilder(
            context,
            AttendifyDatabase::class.java,
            AttendifyDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: AttendifyDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    @Singleton
    fun provideCourseDao(database: AttendifyDatabase): CourseDao {
        return database.courseDao()
    }
    
    @Provides
    @Singleton
    fun provideSessionDao(database: AttendifyDatabase): SessionDao {
        return database.sessionDao()
    }
    
    @Provides
    @Singleton
    fun provideEnrollmentDao(database: AttendifyDatabase): EnrollmentDao {
        return database.enrollmentDao()
    }
    
    @Provides
    @Singleton
    fun provideAttendanceRecordDao(database: AttendifyDatabase): AttendanceRecordDao {
        return database.attendanceRecordDao()
    }
    
    @Provides
    @Singleton
    fun provideDatabaseSeeder(database: AttendifyDatabase): DatabaseSeeder {
        return DatabaseSeeder(database)
    }
}
