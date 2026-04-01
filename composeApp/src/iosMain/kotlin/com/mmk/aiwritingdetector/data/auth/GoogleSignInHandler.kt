package com.mmk.aiwritingdetector.data.auth

import com.mmk.aiwritingdetector.domain.auth.GoogleSignInResult

/**
 * iOS Google Sign-In implementation.
 * In production, integrate with Google Sign-In SDK for iOS.
 */
actual class GoogleSignInHandler {
    
    actual suspend fun signIn(): Result<GoogleSignInResult> {
        // For iOS, you would integrate with Google Sign-In SDK
        // For demo, return a mock result or error
        return Result.failure(Exception("Google Sign-In not implemented for iOS. Please use the Android version."))
    }
    
    actual suspend fun signOut() {
        // Clear Google Sign-In session
    }
}
