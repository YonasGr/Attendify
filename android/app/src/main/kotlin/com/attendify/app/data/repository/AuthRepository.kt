package com.attendify.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.attendify.app.data.local.dao.UserDao
import com.attendify.app.data.model.User
import com.attendify.app.data.model.toEntity
import com.attendify.app.data.model.toModel
import com.attendify.app.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

/**
 * Repository for managing authentication state with backend integration
 * Falls back to local database when offline
 */
@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: UserDao,
    private val networkRepository: NetworkRepository
) {
    private val dataStore = context.dataStore
    
    companion object {
        private val CURRENT_USER_ID_KEY = stringPreferencesKey("current_user_id")
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }
    
    /**
     * Authenticate user with username and password
     * Tries backend first, falls back to local database if offline
     */
    suspend fun login(username: String, password: String): Result<User> {
        return try {
            // Try backend login first
            var backendResult: User? = null
            networkRepository.login(username, password).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { loginData ->
                            // Save token
                            saveAuthToken(loginData.token)
                            // Convert user and save locally
                            val user = loginData.user.toDomainModel()
                            userDao.insertUser(user.toEntity())
                            saveCurrentUserId(user.id)
                            backendResult = user
                        }
                    }
                    is Resource.Error -> {
                        // Backend failed, try local database
                        val userEntity = userDao.getUserByUsername(username)
                        if (userEntity != null && userEntity.password == password) {
                            saveCurrentUserId(userEntity.id)
                            backendResult = userEntity.toModel()
                        }
                    }
                    is Resource.Loading -> { /* Loading state */ }
                }
            }
            
            backendResult?.let { 
                Result.success(it) 
            } ?: Result.failure(Exception("Invalid username or password"))
            
        } catch (e: Exception) {
            // On any error, try local database
            try {
                val userEntity = userDao.getUserByUsername(username)
                if (userEntity != null && userEntity.password == password) {
                    saveCurrentUserId(userEntity.id)
                    Result.success(userEntity.toModel())
                } else {
                    Result.failure(Exception("Invalid username or password"))
                }
            } catch (localError: Exception) {
                Result.failure(localError)
            }
        }
    }
    
    /**
     * Save authentication token
     */
    private suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }
    
    /**
     * Get authentication token
     */
    suspend fun getAuthToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }.first()
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
    suspend fun getCurrentUser(): User? {
        val userId = getCurrentUserId().first()
        return if (userId != null) {
            userDao.getUserById(userId).first()?.toModel()
        } else {
            null
        }
    }
    
    /**
     * Get current user as Flow
     */
    fun getCurrentUserFlow(): Flow<User?> {
        return dataStore.data.map { preferences ->
            val userId = preferences[CURRENT_USER_ID_KEY]
            if (userId != null) {
                userDao.getUserById(userId).first()?.toModel()
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
    
    /**
     * Enable or disable biometric authentication for current user
     */
    suspend fun setBiometricEnabled(enabled: Boolean) {
        val userId = getCurrentUserId().first()
        if (userId != null) {
            userDao.updateBiometricEnabled(userId, enabled)
        }
    }
    
    /**
     * Check if biometric is enabled for current user
     */
    suspend fun isBiometricEnabledForUser(): Boolean {
        val user = getCurrentUser()
        return user?.biometricEnabled ?: false
    }
}
