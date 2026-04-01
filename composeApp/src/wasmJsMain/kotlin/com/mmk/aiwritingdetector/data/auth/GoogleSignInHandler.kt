package com.mmk.aiwritingdetector.data.auth

import com.mmk.aiwritingdetector.domain.auth.GoogleSignInResult
import kotlinx.datetime.Clock

/**
 * Web Google Sign-In implementation.
 * In production, integrate with Google Identity Services.
 */
actual class GoogleSignInHandler {
    
    actual suspend fun signIn(): Result<GoogleSignInResult> {
        // For Web, you would use Google Identity Services
        // For demo, return a mock result
        val mockResult = GoogleSignInResult(
            idToken = "mock_web_token_${kotlin.time.Clock.System.now().toEpochMilliseconds()}",
            email = "web.user@example.com",
            displayName = "Web User",
            photoUrl = null
        )
        
        return Result.success(mockResult)
    }
    
    actual suspend fun signOut() {
        // Clear session
    }
}
