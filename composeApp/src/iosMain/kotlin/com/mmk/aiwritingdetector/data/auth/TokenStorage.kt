package com.mmk.aiwritingdetector.data.auth

import com.mmk.aiwritingdetector.domain.auth.PasetoToken
import com.mmk.aiwritingdetector.domain.auth.TokenType
import com.mmk.aiwritingdetector.domain.auth.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

/**
 * iOS implementation using NSUserDefaults.
 * For production, consider using Keychain.
 */
class IosTokenStorage : TokenStorage {
    
    private val json = Json { ignoreUnknownKeys = true }
    private val defaults = NSUserDefaults.standardUserDefaults
    
    companion object {
        private const val KEY_ACCESS_TOKEN = "ai_detector_access_token"
        private const val KEY_ACCESS_TOKEN_EXPIRES = "ai_detector_access_expires"
        private const val KEY_REFRESH_TOKEN = "ai_detector_refresh_token"
        private const val KEY_REFRESH_TOKEN_EXPIRES = "ai_detector_refresh_expires"
        private const val KEY_USER = "ai_detector_user"
    }
    
    override fun saveAccessToken(token: PasetoToken) {
        defaults.setObject(token.token, KEY_ACCESS_TOKEN)
        defaults.setDouble(token.expiresAt.toDouble(), KEY_ACCESS_TOKEN_EXPIRES)
    }
    
    override fun getAccessToken(): PasetoToken? {
        val token = defaults.stringForKey(KEY_ACCESS_TOKEN) ?: return null
        val expires = defaults.doubleForKey(KEY_ACCESS_TOKEN_EXPIRES).toLong()
        if (expires == 0L) return null
        
        return PasetoToken(
            token = token,
            expiresAt = expires,
            tokenType = TokenType.ACCESS
        )
    }
    
    override fun saveRefreshToken(token: PasetoToken) {
        defaults.setObject(token.token, KEY_REFRESH_TOKEN)
        defaults.setDouble(token.expiresAt.toDouble(), KEY_REFRESH_TOKEN_EXPIRES)
    }
    
    override fun getRefreshToken(): PasetoToken? {
        val token = defaults.stringForKey(KEY_REFRESH_TOKEN) ?: return null
        val expires = defaults.doubleForKey(KEY_REFRESH_TOKEN_EXPIRES).toLong()
        if (expires == 0L) return null
        
        return PasetoToken(
            token = token,
            expiresAt = expires,
            tokenType = TokenType.REFRESH
        )
    }
    
    override fun saveUser(user: User) {
        val userJson = json.encodeToString(user)
        defaults.setObject(userJson, KEY_USER)
    }
    
    override fun getUser(): User? {
        val userJson = defaults.stringForKey(KEY_USER) ?: return null
        return try {
            json.decodeFromString<User>(userJson)
        } catch (e: Exception) {
            null
        }
    }
    
    override fun clearAll() {
        defaults.removeObjectForKey(KEY_ACCESS_TOKEN)
        defaults.removeObjectForKey(KEY_ACCESS_TOKEN_EXPIRES)
        defaults.removeObjectForKey(KEY_REFRESH_TOKEN)
        defaults.removeObjectForKey(KEY_REFRESH_TOKEN_EXPIRES)
        defaults.removeObjectForKey(KEY_USER)
    }
}

actual class TokenStorageFactory {
    actual fun create(): TokenStorage = IosTokenStorage()
}
