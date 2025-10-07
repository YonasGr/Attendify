package com.attendify.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.attendify.app.data.model.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

/**
 * Repository for managing authentication state and session tokens
 */
@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val dataStore = context.dataStore
    
    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
    }
    
    /**
     * Save authentication token (session cookie or JWT)
     */
    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }
    
    /**
     * Get stored authentication token
     */
    fun getAuthToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY]
    }
    
    /**
     * Save current user data
     */
    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[USER_DATA_KEY] = gson.toJson(user)
        }
    }
    
    /**
     * Get current user data
     */
    fun getUser(): Flow<User?> = dataStore.data.map { preferences ->
        val userJson = preferences[USER_DATA_KEY]
        userJson?.let { gson.fromJson(it, User::class.java) }
    }
    
    /**
     * Clear all authentication data (logout)
     */
    suspend fun clearAuth() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY] != null && preferences[USER_DATA_KEY] != null
    }
}
