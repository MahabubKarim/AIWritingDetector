package com.mmk.aiwritingdetector.data.auth

import com.mmk.aiwritingdetector.domain.auth.PasetoPayload
import com.mmk.aiwritingdetector.domain.auth.PasetoToken
import com.mmk.aiwritingdetector.domain.auth.TokenType
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * PASETO Token Manager.
 * 
 * PASETO (Platform-Agnostic Security Tokens) is a modern alternative to JWT.
 * 
 * Key differences from JWT:
 * 1. No algorithm confusion attacks - version determines algorithm
 * 2. Secure defaults - uses modern cryptographic primitives
 * 3. Two purposes: public (signed) and local (encrypted)
 * 
 * Version 4 (v4) uses:
 * - Public: Ed25519 signatures
 * - Local: XChaCha20-Poly1305 encryption
 * 
 * For this client-side implementation, we:
 * - Create local tokens for demonstration
 * - In production, tokens would be issued by a backend server
 */
class PasetoTokenManager {
    
    private val json = Json { 
        ignoreUnknownKeys = true 
        encodeDefaults = true
    }
    
    companion object {
        const val VERSION = "v4"
        const val PURPOSE_LOCAL = "local"
        const val PURPOSE_PUBLIC = "public"
        
        // Token expiration times
        const val ACCESS_TOKEN_EXPIRY_SECONDS = 3600L  // 1 hour
        const val REFRESH_TOKEN_EXPIRY_SECONDS = 604800L  // 7 days
    }
    
    /**
     * Create a local PASETO token (for demonstration).
     * In production, this would be done server-side with proper encryption.
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun createLocalToken(
        userId: String,
        email: String,
        name: String,
        tokenType: TokenType = TokenType.ACCESS
    ): PasetoToken {
        val now = kotlin.time.Clock.System.now().epochSeconds
        val expirySeconds = when (tokenType) {
            TokenType.ACCESS -> ACCESS_TOKEN_EXPIRY_SECONDS
            TokenType.REFRESH -> REFRESH_TOKEN_EXPIRY_SECONDS
        }
        val expiresAt = now + expirySeconds
        
        val payload = PasetoPayload(
            sub = userId,
            iat = now,
            exp = expiresAt,
            email = email,
            name = name
        )
        
        val payloadJson = json.encodeToString(payload)
        val encodedPayload = Base64.UrlSafe.encode(payloadJson.encodeToByteArray())
        
        // PASETO token format: version.purpose.payload
        // In production, the payload would be encrypted (local) or signed (public)
        val token = "$VERSION.$PURPOSE_LOCAL.$encodedPayload"
        
        return PasetoToken(
            token = token,
            expiresAt = expiresAt * 1000, // Convert to milliseconds
            tokenType = tokenType
        )
    }
    
    /**
     * Decode a PASETO token payload (for demonstration).
     * In production, this would verify the signature/decrypt the token.
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun decodeToken(token: String): PasetoPayload? {
        return try {
            val parts = token.split(".")
            if (parts.size < 3) return null
            
            val version = parts[0]
            val purpose = parts[1]
            val encodedPayload = parts[2]
            
            if (version != VERSION) return null
            
            val payloadBytes = Base64.UrlSafe.decode(encodedPayload)
            val payloadJson = payloadBytes.decodeToString()
            
            json.decodeFromString<PasetoPayload>(payloadJson)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Check if a token is expired.
     */
    fun isTokenExpired(token: PasetoToken): Boolean {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        return now >= token.expiresAt
    }
    
    /**
     * Check if a token needs refresh (within 5 minutes of expiry).
     */
    fun shouldRefreshToken(token: PasetoToken): Boolean {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val refreshThreshold = 5 * 60 * 1000 // 5 minutes in milliseconds
        return (token.expiresAt - now) <= refreshThreshold
    }
    
    /**
     * Validate token structure.
     */
    fun isValidTokenStructure(tokenString: String): Boolean {
        val parts = tokenString.split(".")
        return parts.size >= 3 && parts[0] == VERSION
    }
}
