package com.attendify.app.data.api

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

/**
 * Interceptor that adds authentication token to requests
 */
@Singleton
class AuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {
    
    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            context.authDataStore.data.map { preferences ->
                preferences[AUTH_TOKEN_KEY]
            }.first()
        }
        
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        
        return chain.proceed(request)
    }
}

/**
 * Interceptor for handling errors and retries
 */
@Singleton
class ErrorInterceptor @Inject constructor() : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        // Handle specific HTTP error codes
        when (response.code) {
            401 -> {
                // Unauthorized - token expired or invalid
                // Could trigger token refresh here
            }
            403 -> {
                // Forbidden - insufficient permissions
            }
            404 -> {
                // Not found
            }
            500, 502, 503, 504 -> {
                // Server errors - could trigger retry logic
            }
        }
        
        return response
    }
}
