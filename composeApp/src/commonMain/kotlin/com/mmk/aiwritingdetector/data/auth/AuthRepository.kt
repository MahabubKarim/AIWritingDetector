package com.mmk.aiwritingdetector.data.auth

import com.mmk.aiwritingdetector.domain.auth.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for handling authentication operations.
 */
interface AuthRepository {
    val authState: Flow<AuthState>
    
    suspend fun signInWithGoogle(googleResult: GoogleSignInResult): Result<AuthResponse>
    suspend fun signOut()
    suspend fun refreshToken(): Result<PasetoToken>
    suspend fun getCurrentUser(): User?
    suspend fun isLoggedIn(): Boolean
    fun getAccessToken(): PasetoToken?
}

/**
 * Implementation of AuthRepository.
 * 
 * In a production app, this would communicate with a backend server.
 * For demonstration, we handle authentication locally.
 */
class AuthRepositoryImpl(
    private val tokenManager: PasetoTokenManager,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    override val authState: Flow<AuthState> = _authState.asStateFlow()
    
    private var currentUser: User? = null
    private var accessToken: PasetoToken? = null
    private var refreshToken: PasetoToken? = null
    
    init {
        // Try to restore session on init
        restoreSession()
    }
    
    private fun restoreSession() {
        val savedToken = tokenStorage.getAccessToken()
        val savedUser = tokenStorage.getUser()
        
        if (savedToken != null && savedUser != null) {
            if (!tokenManager.isTokenExpired(savedToken)) {
                currentUser = savedUser
                accessToken = savedToken
                refreshToken = tokenStorage.getRefreshToken()
                _authState.value = AuthState.Authenticated(savedUser, savedToken)
            } else {
                // Token expired, try refresh
                val savedRefreshToken = tokenStorage.getRefreshToken()
                if (savedRefreshToken != null && !tokenManager.isTokenExpired(savedRefreshToken)) {
                    // In production, would call refresh endpoint
                    // For now, just clear and require re-login
                    tokenStorage.clearAll()
                    _authState.value = AuthState.Unauthenticated
                } else {
                    tokenStorage.clearAll()
                    _authState.value = AuthState.Unauthenticated
                }
            }
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }
    
    /**
     * Sign in with Google.
     * 
     * Flow:
     * 1. Receive Google ID token from Google Sign-In
     * 2. In production: Send to backend, backend verifies and returns PASETO tokens
     * 3. For demo: Create local PASETO tokens
     */
    override suspend fun signInWithGoogle(googleResult: GoogleSignInResult): Result<AuthResponse> {
        _authState.value = AuthState.Loading
        
        return try {
            // In production, you would:
            // 1. Send googleResult.idToken to your backend
            // 2. Backend verifies with Google
            // 3. Backend creates user if needed
            // 4. Backend generates and returns PASETO tokens
            
            // For demonstration, we create tokens locally
            val userId = generateUserId(googleResult.email)
            
            val user = User(
                id = userId,
                email = googleResult.email,
                displayName = googleResult.displayName,
                photoUrl = googleResult.photoUrl,
                createdAt = kotlin.time.Clock.System.now().toEpochMilliseconds()
            )
            
            val newAccessToken = tokenManager.createLocalToken(
                userId = userId,
                email = googleResult.email,
                name = googleResult.displayName,
                tokenType = TokenType.ACCESS
            )
            
            val newRefreshToken = tokenManager.createLocalToken(
                userId = userId,
                email = googleResult.email,
                name = googleResult.displayName,
                tokenType = TokenType.REFRESH
            )
            
            // Save to storage
            tokenStorage.saveAccessToken(newAccessToken)
            tokenStorage.saveRefreshToken(newRefreshToken)
            tokenStorage.saveUser(user)
            
            // Update state
            currentUser = user
            accessToken = newAccessToken
            refreshToken = newRefreshToken
            
            val response = AuthResponse(
                user = user,
                accessToken = newAccessToken,
                refreshToken = newRefreshToken
            )
            
            _authState.value = AuthState.Authenticated(user, newAccessToken)
            
            Result.success(response)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            Result.failure(e)
        }
    }
    
    override suspend fun signOut() {
        currentUser = null
        accessToken = null
        refreshToken = null
        tokenStorage.clearAll()
        _authState.value = AuthState.Unauthenticated
    }
    
    override suspend fun refreshToken(): Result<PasetoToken> {
        val currentRefreshToken = refreshToken ?: return Result.failure(
            Exception("No refresh token available")
        )
        
        if (tokenManager.isTokenExpired(currentRefreshToken)) {
            signOut()
            return Result.failure(Exception("Refresh token expired"))
        }
        
        return try {
            // In production, would call refresh endpoint
            val payload = tokenManager.decodeToken(currentRefreshToken.token)
                ?: return Result.failure(Exception("Invalid refresh token"))
            
            val newAccessToken = tokenManager.createLocalToken(
                userId = payload.sub,
                email = payload.email,
                name = payload.name,
                tokenType = TokenType.ACCESS
            )
            
            accessToken = newAccessToken
            tokenStorage.saveAccessToken(newAccessToken)
            
            currentUser?.let { user ->
                _authState.value = AuthState.Authenticated(user, newAccessToken)
            }
            
            Result.success(newAccessToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUser(): User? = currentUser
    
    override suspend fun isLoggedIn(): Boolean {
        return accessToken?.let { !tokenManager.isTokenExpired(it) } ?: false
    }
    
    override fun getAccessToken(): PasetoToken? = accessToken
    
    private fun generateUserId(email: String): String {
        // Simple hash-based ID generation
        // In production, backend would generate this
        return "user_${email.hashCode().toUInt()}"
    }
}

/**
 * Interface for token storage.
 */
interface TokenStorage {
    fun saveAccessToken(token: PasetoToken)
    fun getAccessToken(): PasetoToken?
    fun saveRefreshToken(token: PasetoToken)
    fun getRefreshToken(): PasetoToken?
    fun saveUser(user: User)
    fun getUser(): User?
    fun clearAll()
}
