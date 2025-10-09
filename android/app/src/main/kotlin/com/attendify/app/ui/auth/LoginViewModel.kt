package com.attendify.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendify.app.data.model.User
import com.attendify.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: String? = null
)

/**
 * ViewModel for handling authentication logic with local database
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    val isBiometricEnabledForUser: StateFlow<Boolean> = flow {
        while (true) {
            emit(authRepository.isBiometricEnabledForUser())
            kotlinx.coroutines.delay(1000) // Check every second
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
    
    init {
        checkAuthStatus()
    }
    
    /**
     * Check if user is already authenticated
     */
    private fun checkAuthStatus() {
        viewModelScope.launch {
            authRepository.isAuthenticated().collect { isAuth ->
                if (isAuth) {
                    // User is authenticated, fetch user data
                    val user = authRepository.getCurrentUser()
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        isLoading = false,
                        user = user
                    )
                } else {
                    _authState.value = AuthState(
                        isAuthenticated = false,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Login with username and password
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            val result = authRepository.login(username, password)
            
            result.onSuccess { user ->
                _loginState.value = LoginState.Success(user)
                _authState.value = AuthState(
                    isAuthenticated = true,
                    isLoading = false,
                    user = user
                )
            }.onFailure { error ->
                _loginState.value = LoginState.Error(error.message ?: "Login failed")
                _authState.value = _authState.value.copy(
                    error = error.message,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Login with biometrics
     */
    fun loginWithBiometrics() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val user = authRepository.getCurrentUser()
            if (user != null) {
                _loginState.value = LoginState.Success(user)
                _authState.value = AuthState(
                    isAuthenticated = true,
                    isLoading = false,
                    user = user
                )
            } else {
                _loginState.value = LoginState.Error("No user found for biometric login.")
                _authState.value = _authState.value.copy(
                    error = "No user found for biometric login.",
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * Logout user
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState(
                isAuthenticated = false,
                isLoading = false
            )
            _loginState.value = LoginState.Idle
        }
    }
}

/**
 * Sealed class representing login operation states
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
