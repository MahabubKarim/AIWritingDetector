package com.mmk.aiwritingdetector.data.auth

import com.mmk.aiwritingdetector.domain.auth.GoogleSignInResult

/**
 * Platform-specific Google Sign-In handler.
 */
expect class GoogleSignInHandler {
    suspend fun signIn(): Result<GoogleSignInResult>
    suspend fun signOut()
}
