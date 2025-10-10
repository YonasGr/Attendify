package com.attendify.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendify.app.data.model.User
import com.attendify.app.data.repository.AuthRepository
import com.attendify.app.data.repository.SyncManager
import com.attendify.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Settings screen with sync functionality
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager
) : ViewModel() {
    
    private val _settingsState = MutableStateFlow<SettingsState>(SettingsState.Idle)
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()
    
    val currentUser: StateFlow<User?> = authRepository.getCurrentUserFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    val lastSyncTime: StateFlow<Long?> = syncManager.getLastSyncTime()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    /**
     * Enable or disable biometric authentication
     */
    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                _settingsState.value = SettingsState.Loading
                authRepository.setBiometricEnabled(enabled)
                _settingsState.value = SettingsState.Success(
                    if (enabled) "Biometric login enabled successfully"
                    else "Biometric login disabled"
                )
                // Clear success message after delay
                kotlinx.coroutines.delay(3000)
                _settingsState.value = SettingsState.Idle
            } catch (e: Exception) {
                _settingsState.value = SettingsState.Error(
                    e.message ?: "Failed to update biometric settings"
                )
            }
        }
    }
    
    /**
     * Trigger data synchronization with backend
     */
    fun syncData() {
        viewModelScope.launch {
            syncManager.performFullSync().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _settingsState.value = SettingsState.Syncing(resource.message ?: "Syncing...")
                    }
                    is Resource.Success -> {
                        _settingsState.value = SettingsState.Success(
                            resource.data ?: "Data synchronized successfully"
                        )
                        // Clear success message after delay
                        kotlinx.coroutines.delay(3000)
                        _settingsState.value = SettingsState.Idle
                    }
                    is Resource.Error -> {
                        _settingsState.value = SettingsState.Error(
                            resource.message ?: "Sync failed"
                        )
                        // Clear error message after delay
                        kotlinx.coroutines.delay(5000)
                        _settingsState.value = SettingsState.Idle
                    }
                }
            }
        }
    }
    
    /**
     * Set error state
     */
    fun setError(message: String) {
        _settingsState.value = SettingsState.Error(message)
    }
}

/**
 * Sealed class representing settings operation states
 */
sealed class SettingsState {
    object Idle : SettingsState()
    object Loading : SettingsState()
    data class Syncing(val message: String) : SettingsState()
    data class Success(val message: String) : SettingsState()
    data class Error(val message: String) : SettingsState()
}
