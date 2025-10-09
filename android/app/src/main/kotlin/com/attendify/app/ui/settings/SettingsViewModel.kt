package com.attendify.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendify.app.data.model.User
import com.attendify.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Settings screen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _settingsState = MutableStateFlow<SettingsState>(SettingsState.Idle)
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()
    
    val currentUser: StateFlow<User?> = authRepository.getCurrentUserFlow()
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
    data class Success(val message: String) : SettingsState()
    data class Error(val message: String) : SettingsState()
}
