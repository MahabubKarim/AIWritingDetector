package com.mmk.aiwritingdetector.domain.model

import kotlinx.datetime.Instant

/**
 * Represents a saved analysis result in history.
 */
data class AnalysisHistory(
    val id: Long,
    val textPreview: String,
    val fullText: String,
    val overallScore: Double,
    val confidence: Double,
    val verdict: Verdict,
    val summary: String,
    val wordCount: Int,
    val sentenceCount: Int,
    val signals: List<DetectionSignal>,
    val createdAt: Instant
) {
    /**
     * Get formatted date string.
     */
    fun getFormattedDate(): String {
        val epochSeconds = createdAt.epochSeconds
        val date = Instant.fromEpochSeconds(epochSeconds)
        return date.toString().take(16).replace("T", " ")
    }
    
    /**
     * Get score as percentage.
     */
    fun getScorePercentage(): Int = (overallScore * 100).toInt()
}

/**
 * Signal data for JSON serialization.
 */
data class SignalData(
    val name: String,
    val description: String,
    val score: Double,
    val weight: Double,
    val evidence: List<String>
)
