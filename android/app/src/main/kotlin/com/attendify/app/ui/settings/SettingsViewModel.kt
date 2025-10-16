package com.attendify.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendify.app.data.model.User
import com.attendify.app.data.repository.AuthRepository
import com.attendify.app.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SettingsState {
    object Idle : SettingsState()
    object Loading : SettingsState()
    object Syncing : SettingsState()
    data class Success(val message: String) : SettingsState()
    data class Error(val message: String) : SettingsState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _settingsState = MutableStateFlow<SettingsState>(SettingsState.Idle)
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getCurrentUserFlow().collectLatest {
                _user.value = it
            }
        }
    }

    fun onBiometricAuthToggled(enabled: Boolean) {
        viewModelScope.launch {
            _settingsState.value = SettingsState.Loading
            try {
                authRepository.setBiometricEnabled(enabled)
                _settingsState.value = SettingsState.Success("Biometric settings updated.")
            } catch (e: Exception) {
                _settingsState.value = SettingsState.Error(e.message ?: "Failed to update biometric setting.")
            }
        }
    }

    fun syncData() {
        viewModelScope.launch {
            _settingsState.value = SettingsState.Syncing
            // In a real app, you would fetch data from the local DB and send it to the server
            networkRepository.syncDownload().collectLatest { 
                // a placeholder for the real sync logic
            }
            _lastSyncTime.value = System.currentTimeMillis()
            _settingsState.value = SettingsState.Success("Data synced successfully.")
        }
    }

    fun updateProfile(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _settingsState.value = SettingsState.Loading
            try {
                val currentUser = _user.value!!
                val updatedUser = currentUser.copy(
                    firstName = firstName,
                    lastName = lastName,
                    email = email
                )
                authRepository.updateUser(updatedUser, password)
                _settingsState.value = SettingsState.Success("Profile updated successfully.")
            } catch (e: Exception) {
                _settingsState.value = SettingsState.Error(e.message ?: "Failed to update profile.")
            }
        }
    }

    fun setError(message: String) {
        _settingsState.value = SettingsState.Error(message)
    }
}
