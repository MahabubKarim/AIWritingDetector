package com.mmk.aiwritingdetector.domain.auth

import kotlinx.serialization.Serializable

/**
 * User data model.
 */
@Serializable
data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val createdAt: Long = 0L
)

/**
 * PASETO Token structure.
 * 
 * PASETO (Platform-Agnostic Security Tokens) is a modern alternative to JWT.
 * It addresses JWT's security flaws by implementing secure defaults:
 * - Public PASETO: Uses asymmetric cryptography (Ed25519)
 * - Local PASETO: Uses symmetric encryption (XChaCha20-Poly1305)
 * 
 * Structure: version.purpose.payload.footer (optional)
 * Example: v4.public.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZXhwIjoiMjAyNC0wMS0wMVQwMDowMDowMFoifQ
 */
@Serializable
data class PasetoToken(
    val token: String,
    val expiresAt: Long,
    val tokenType: TokenType = TokenType.ACCESS
)

/**
 * Token types for PASETO.
 */
@Serializable
enum class TokenType {
    ACCESS,
    REFRESH
}

/**
 * Authentication state.
 */
sealed class AuthState {
    data object Initial : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val user: User, val token: PasetoToken) : AuthState()
    data object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * Google Sign-In result.
 */
@Serializable
data class GoogleSignInResult(
    val idToken: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?
)

/**
 * Auth response from backend.
 */
@Serializable
data class AuthResponse(
    val user: User,
    val accessToken: PasetoToken,
    val refreshToken: PasetoToken
)

/**
 * PASETO token payload (decoded).
 */
@Serializable
data class PasetoPayload(
    val sub: String,        // Subject (user ID)
    val iat: Long,          // Issued at
    val exp: Long,          // Expiration
    val email: String,
    val name: String,
    val aud: String = "ai-writing-detector",  // Audience
    val iss: String = "ai-writing-detector-auth"  // Issuer
)
