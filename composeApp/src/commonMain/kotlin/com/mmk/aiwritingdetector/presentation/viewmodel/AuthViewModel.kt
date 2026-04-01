package com.mmk.aiwritingdetector.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.aiwritingdetector.data.auth.AuthRepository
import com.mmk.aiwritingdetector.data.auth.GoogleSignInHandler
import com.mmk.aiwritingdetector.domain.auth.AuthState
import com.mmk.aiwritingdetector.domain.auth.User
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * MVI Contract for Authentication.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)

sealed interface AuthIntent {
    data object SignInWithGoogle : AuthIntent
    data object SignOut : AuthIntent
    data object ClearError : AuthIntent
    data object CheckAuthStatus : AuthIntent
}

sealed interface AuthEffect {
    data object NavigateToHome : AuthEffect
    data object NavigateToLogin : AuthEffect
    data class ShowError(val message: String) : AuthEffect
}

/**
 * ViewModel for handling authentication.
 */
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val googleSignInHandler: GoogleSignInHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _effects = Channel<AuthEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    
    init {
        observeAuthState()
    }
    
    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authState.collect { authState ->
                when (authState) {
                    is AuthState.Initial -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is AuthState.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is AuthState.Authenticated -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                user = authState.user,
                                isAuthenticated = true,
                                error = null
                            )
                        }
                    }
                    is AuthState.Unauthenticated -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                user = null,
                                isAuthenticated = false
                            )
                        }
                    }
                    is AuthState.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = authState.message
                            )
                        }
                        _effects.send(AuthEffect.ShowError(authState.message))
                    }
                }
            }
        }
    }
    
    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignInWithGoogle -> signInWithGoogle()
            is AuthIntent.SignOut -> signOut()
            is AuthIntent.ClearError -> clearError()
            is AuthIntent.CheckAuthStatus -> checkAuthStatus()
        }
    }
    
    private fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            googleSignInHandler.signIn()
                .onSuccess { googleResult ->
                    authRepository.signInWithGoogle(googleResult)
                        .onSuccess {
                            _effects.send(AuthEffect.NavigateToHome)
                        }
                        .onFailure { error ->
                            _uiState.update { it.copy(isLoading = false, error = error.message) }
                            _effects.send(AuthEffect.ShowError(error.message ?: "Sign-in failed"))
                        }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    _effects.send(AuthEffect.ShowError(error.message ?: "Google Sign-In failed"))
                }
        }
    }
    
    private fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            googleSignInHandler.signOut()
            _effects.send(AuthEffect.NavigateToLogin)
        }
    }
    
    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    private fun checkAuthStatus() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.isLoggedIn()
            if (isLoggedIn) {
                _effects.send(AuthEffect.NavigateToHome)
            } else {
                _effects.send(AuthEffect.NavigateToLogin)
            }
        }
    }
}
