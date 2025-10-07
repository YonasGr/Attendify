package com.attendify.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.attendify.app.data.local.dao.UserDao
import com.attendify.app.data.local.entity.UserEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

/**
 * Repository for managing authentication state with local database
 */
@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: UserDao
) {
    private val dataStore = context.dataStore
    
    companion object {
        private val CURRENT_USER_ID_KEY = stringPreferencesKey("current_user_id")
    }
    
    /**
     * Authenticate user with username and password
     */
    suspend fun login(username: String, password: String): Result<UserEntity> {
        return try {
            val user = userDao.getUserByUsername(username)
            
            if (user != null && user.password == password) {
                // Save current user ID
                saveCurrentUserId(user.id)
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid username or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Save current user ID
     */
    private suspend fun saveCurrentUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID_KEY] = userId
        }
    }
    
    /**
     * Get current user ID
     */
    fun getCurrentUserId(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[CURRENT_USER_ID_KEY]
    }
    
    /**
     * Get current user
     */
    suspend fun getCurrentUser(): UserEntity? {
        val userId = getCurrentUserId().first()
        return if (userId != null) {
            userDao.getUserById(userId).first()
        } else {
            null
        }
    }
    
    /**
     * Get current user as Flow
     */
    fun getCurrentUserFlow(): Flow<UserEntity?> {
        return dataStore.data.map { preferences ->
            val userId = preferences[CURRENT_USER_ID_KEY]
            if (userId != null) {
                userDao.getUserById(userId).first()
            } else {
                null
            }
        }
    }
    
    /**
     * Clear all authentication data (logout)
     */
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[CURRENT_USER_ID_KEY] != null
    }
}
