package com.mmk.aiwritingdetector.presentation.viewmodel

import com.mmk.aiwritingdetector.domain.model.AnalysisResult
import com.mmk.aiwritingdetector.domain.model.TextStats

/**
 * UI State for the detector screen.
 */
data class DetectorState(
    val inputText: String = "",
    val textStats: TextStats? = null,
    val analysisResult: AnalysisResult? = null,
    val isAnalyzing: Boolean = false,
    val error: String? = null,
    val showResults: Boolean = false
) {
    val canAnalyze: Boolean
        get() = inputText.isNotBlank() &&
                (textStats?.wordCount ?: 0) >= 10 &&
                !isAnalyzing

    val wordCount: Int
        get() = textStats?.wordCount ?: 0

    val characterCount: Int
        get() = textStats?.characterCount ?: 0
}

/**
 * User intents (actions).
 */
sealed interface DetectorIntent {
    data class UpdateText(val text: String) : DetectorIntent
    object Analyze : DetectorIntent
    object ClearResults : DetectorIntent
    object ClearError : DetectorIntent
    object ClearAll : DetectorIntent
}

/**
 * One-time side effects.
 */
sealed interface DetectorEffect {
    data class ShowError(val message: String) : DetectorEffect
    object ScrollToResults : DetectorEffect
    object AnalysisComplete : DetectorEffect
}
