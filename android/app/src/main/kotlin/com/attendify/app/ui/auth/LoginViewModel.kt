package com.attendify.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendify.app.data.model.User
import com.attendify.app.data.repository.AttendifyRepository
import com.attendify.app.data.repository.AuthRepository
import com.attendify.app.utils.Resource
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
 * ViewModel for handling authentication logic
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val attendifyRepository: AttendifyRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _loginState = MutableStateFlow<Resource<User>?>(null)
    val loginState: StateFlow<Resource<User>?> = _loginState.asStateFlow()
    
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
                    authRepository.getUser().collect { user ->
                        _authState.value = AuthState(
                            isAuthenticated = true,
                            isLoading = false,
                            user = user
                        )
                    }
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
     * Simulate login with session token
     * In a real implementation, this would integrate with the Replit Auth flow
     * or another OAuth provider
     */
    fun login(sessionToken: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            
            try {
                // Save the session token
                authRepository.saveAuthToken(sessionToken)
                
                // Fetch current user
                attendifyRepository.getCurrentUser().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { user ->
                                authRepository.saveUser(user)
                                _loginState.value = Resource.Success(user)
                                _authState.value = AuthState(
                                    isAuthenticated = true,
                                    isLoading = false,
                                    user = user
                                )
                            }
                        }
                        is Resource.Error -> {
                            _loginState.value = Resource.Error(result.message ?: "Login failed")
                            _authState.value = _authState.value.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                        is Resource.Loading -> {
                            _loginState.value = Resource.Loading()
                        }
                    }
                }
            } catch (e: Exception) {
                _loginState.value = Resource.Error(e.message ?: "Login failed")
                _authState.value = _authState.value.copy(
                    error = e.message,
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
            authRepository.clearAuth()
            _authState.value = AuthState(
                isAuthenticated = false,
                isLoading = false
            )
            _loginState.value = null
        }
    }
}
