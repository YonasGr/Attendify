package com.attendify.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for Attendify
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 */
@HiltAndroidApp
class AttendifyApplication : Application()
