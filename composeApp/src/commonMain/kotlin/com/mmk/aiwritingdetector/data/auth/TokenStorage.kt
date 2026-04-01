package com.mmk.aiwritingdetector.data.auth

import com.mmk.aiwritingdetector.domain.auth.PasetoToken
import com.mmk.aiwritingdetector.domain.auth.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * In-memory token storage implementation.
 * Used as fallback and for platforms without persistent storage.
 */
class InMemoryTokenStorage : TokenStorage {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    private var accessToken: PasetoToken? = null
    private var refreshToken: PasetoToken? = null
    private var user: User? = null
    
    override fun saveAccessToken(token: PasetoToken) {
        accessToken = token
    }
    
    override fun getAccessToken(): PasetoToken? = accessToken
    
    override fun saveRefreshToken(token: PasetoToken) {
        refreshToken = token
    }
    
    override fun getRefreshToken(): PasetoToken? = refreshToken
    
    override fun saveUser(user: User) {
        this.user = user
    }
    
    override fun getUser(): User? = user
    
    override fun clearAll() {
        accessToken = null
        refreshToken = null
        user = null
    }
}

/**
 * Platform-specific token storage factory.
 */
expect class TokenStorageFactory {
    fun create(): TokenStorage
}
