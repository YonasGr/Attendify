package com.attendify.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.attendify.app.data.api.toDomainModel
import com.attendify.app.data.local.dao.UserDao
import com.attendify.app.data.model.User
import com.attendify.app.data.model.toEntity
import com.attendify.app.data.model.toModel
import com.attendify.app.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: UserDao,
    private val networkRepository: NetworkRepository,
    private val applicationScope: CoroutineScope
) {
    private val dataStore = context.dataStore

    companion object {
        private val CURRENT_USER_ID_KEY = stringPreferencesKey("current_user_id")
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    val authState: StateFlow<Resource<User?>> = dataStore.data
        .map { preferences ->
            preferences[CURRENT_USER_ID_KEY]
        }
        .distinctUntilChanged()
        .flatMapLatest { userId ->
            if (userId == null) {
                flowOf<Resource<User?>>(Resource.Success(null))
            } else {
                userDao.getUserById(userId).map { entity ->
                    Resource.Success(entity?.toModel()) as Resource<User?>
                }
            }
        }
        .catch { e ->
            emit(Resource.Error("Failed to load auth state: ${e.message}"))
        }
        .stateIn(applicationScope, SharingStarted.Eagerly, Resource.Loading())

    suspend fun login(username: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            when (val resource = networkRepository.login(username, password).first { it !is Resource.Loading }) {
                is Resource.Success -> {
                    resource.data?.let {
                        saveAuthToken(it.token)
                        val user = it.user.toDomainModel()
                        userDao.insertUser(user.toEntity())
                        saveCurrentUserId(user.id)
                        Result.success(user)
                    } ?: Result.failure(Exception("Login failed: Empty response from server."))
                }
                is Resource.Error -> loginFromLocal(username, password)
                else -> Result.failure(Exception("Login failed: Unexpected state."))
            }
        } catch (e: Exception) {
            loginFromLocal(username, password)
        }
    }

    private suspend fun loginFromLocal(username: String, password: String): Result<User> {
        return try {
            val userEntity = userDao.getUserByUsername(username)
            if (userEntity != null && userEntity.password == password) {
                saveCurrentUserId(userEntity.id)
                Result.success(userEntity.toModel())
            } else {
                Result.failure(Exception("Invalid username or password."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            dataStore.edit { it.clear() }
        }
    }

    private suspend fun saveAuthToken(token: String) {
        dataStore.edit { it[AUTH_TOKEN_KEY] = token }
    }

    private suspend fun saveCurrentUserId(userId: String) {
        dataStore.edit { it[CURRENT_USER_ID_KEY] = userId }
    }

    suspend fun getCurrentUser(): User? {
        return (authState.first() as? Resource.Success)?.data
    }

    fun getCurrentUserFlow(): Flow<User?> {
        return authState.map { (it as? Resource.Success)?.data }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            getCurrentUser()?.let { user ->
                userDao.updateUser(user.toEntity().copy(biometricEnabled = enabled))
            }
        }
    }
}
