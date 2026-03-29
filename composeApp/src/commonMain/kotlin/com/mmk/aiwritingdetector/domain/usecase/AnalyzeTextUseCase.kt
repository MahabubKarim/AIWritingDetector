package com.mmk.aiwritingdetector.domain.usecase

import com.mmk.aiwritingdetector.domain.analyzer.TextAnalyzer
import com.mmk.aiwritingdetector.domain.model.AnalysisResult
import com.mmk.aiwritingdetector.domain.model.TextStats

/**
 * Use case for analyzing text to detect AI writing patterns.
 */
class AnalyzeTextUseCase(
    private val textAnalyzer: TextAnalyzer
) {
    /**
     * Analyze the input text and return full analysis results.
     * Returns null if the text is invalid (empty or too short).
     */
    fun execute(text: String): Result<AnalysisResult> {
        return try {
            val trimmedText = text.trim()

            if (trimmedText.isBlank()) {
                return Result.failure(ValidationError.EmptyText)
            }

            val wordCount = trimmedText.split(Regex("\\s+")).size
            if (wordCount < 10) {
                return Result.failure(ValidationError.TextTooShort(wordCount, 10))
            }

            val result = textAnalyzer.analyze(trimmedText)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(AnalysisError(e.message ?: "Unknown error"))
        }
    }

    /**
     * Get basic text statistics without full analysis.
     * Useful for real-time stats display while typing.
     */
    fun getBasicStats(text: String): TextStats {
        return textAnalyzer.calculateTextStats(text)
    }
}

/**
 * Validation errors.
 */
sealed class ValidationError : Exception() {
    object EmptyText : ValidationError() {
        private fun readResolve(): Any = EmptyText
        override val message: String = "Please enter some text to analyze."
    }

    data class TextTooShort(
        val currentWords: Int,
        val minimumWords: Int
    ) : ValidationError() {
        override val message: String = "Text is too short ($currentWords words). Please enter at least $minimumWords words."
    }
}

/**
 * Analysis execution error.
 */
class AnalysisError(override val message: String) : Exception(message)
