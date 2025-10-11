package com.attendify.app

import android.app.Application
import com.attendify.app.data.local.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for Attendify
 * Initializes Hilt and seeds the database on first run
 */
@HiltAndroidApp
class AttendifyApplication : Application() {

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    override fun onCreate() {
        super.onCreate()
        // Seed the database with initial data
        databaseSeeder.seedDatabase()
    }
}
