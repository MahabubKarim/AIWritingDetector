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
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val showResults: Boolean = false
) {
    val canAnalyze: Boolean
        get() = inputText.isNotBlank() &&
                (textStats?.wordCount ?: 0) >= 10 &&
                !isAnalyzing

    val canSave: Boolean
        get() = analysisResult != null && !isSaving && !isSaved

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
    data object Analyze : DetectorIntent
    data object SaveToHistory : DetectorIntent
    data object ClearResults : DetectorIntent
    data object ClearError : DetectorIntent
    data object ClearAll : DetectorIntent
    data object NavigateToHistory : DetectorIntent
}

/**
 * One-time side effects.
 */
sealed interface DetectorEffect {
    data class ShowError(val message: String) : DetectorEffect
    data object ScrollToResults : DetectorEffect
    data object AnalysisComplete : DetectorEffect
    data object SavedToHistory : DetectorEffect
    data object NavigateToHistory : DetectorEffect
}
