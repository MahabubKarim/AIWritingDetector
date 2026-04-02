package com.mmk.aiwritingdetector.data.auth

import com.mmk.aiwritingdetector.domain.auth.PasetoToken
import com.mmk.aiwritingdetector.domain.auth.TokenType
import com.mmk.aiwritingdetector.domain.auth.User
import kotlinx.browser.localStorage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Web implementation using localStorage.
 */
class WebTokenStorage : TokenStorage {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    companion object {
        private const val KEY_ACCESS_TOKEN = "ai_detector_access_token"
        private const val KEY_ACCESS_TOKEN_EXPIRES = "ai_detector_access_expires"
        private const val KEY_REFRESH_TOKEN = "ai_detector_refresh_token"
        private const val KEY_REFRESH_TOKEN_EXPIRES = "ai_detector_refresh_expires"
        private const val KEY_USER = "ai_detector_user"
    }
    
    override fun saveAccessToken(token: PasetoToken) {
        localStorage.setItem(KEY_ACCESS_TOKEN, token.token)
        localStorage.setItem(KEY_ACCESS_TOKEN_EXPIRES, token.expiresAt.toString())
    }
    
    override fun getAccessToken(): PasetoToken? {
        val token = localStorage.getItem(KEY_ACCESS_TOKEN) ?: return null
        val expires = localStorage.getItem(KEY_ACCESS_TOKEN_EXPIRES)?.toLongOrNull() ?: return null
        
        return PasetoToken(
            token = token,
            expiresAt = expires,
            tokenType = TokenType.ACCESS
        )
    }
    
    override fun saveRefreshToken(token: PasetoToken) {
        localStorage.setItem(KEY_REFRESH_TOKEN, token.token)
        localStorage.setItem(KEY_REFRESH_TOKEN_EXPIRES, token.expiresAt.toString())
    }
    
    override fun getRefreshToken(): PasetoToken? {
        val token = localStorage.getItem(KEY_REFRESH_TOKEN) ?: return null
        val expires = localStorage.getItem(KEY_REFRESH_TOKEN_EXPIRES)?.toLongOrNull() ?: return null
        
        return PasetoToken(
            token = token,
            expiresAt = expires,
            tokenType = TokenType.REFRESH
        )
    }
    
    override fun saveUser(user: User) {
        val userJson = json.encodeToString(user)
        localStorage.setItem(KEY_USER, userJson)
    }
    
    override fun getUser(): User? {
        val userJson = localStorage.getItem(KEY_USER) ?: return null
        return try {
            json.decodeFromString<User>(userJson)
        } catch (e: Exception) {
            null
        }
    }
    
    override fun clearAll() {
        localStorage.removeItem(KEY_ACCESS_TOKEN)
        localStorage.removeItem(KEY_ACCESS_TOKEN_EXPIRES)
        localStorage.removeItem(KEY_REFRESH_TOKEN)
        localStorage.removeItem(KEY_REFRESH_TOKEN_EXPIRES)
        localStorage.removeItem(KEY_USER)
    }
}

actual class TokenStorageFactory {
    actual fun create(): TokenStorage = WebTokenStorage()
}
