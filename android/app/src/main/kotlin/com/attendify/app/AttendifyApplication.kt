package com.attendify.app

import android.app.Application
import com.attendify.app.data.local.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main Application class for Attendify
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 * Seeds the database with initial data on first launch
 */
@HiltAndroidApp
class AttendifyApplication : Application() {
    
    @Inject
    lateinit var databaseSeeder: DatabaseSeeder
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // Seed database on first launch
        applicationScope.launch {
            databaseSeeder.seedDatabaseIfEmpty()
        }
    }
}
