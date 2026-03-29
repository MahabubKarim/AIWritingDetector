package com.mmk.aiwritingdetector.domain.model

/**
 * Basic text statistics — the foundation of analysis.
 */
data class TextStats(
    val characterCount: Int,
    val characterCountNoSpaces: Int,
    val wordCount: Int,
    val sentenceCount: Int,
    val paragraphCount: Int,
    val avgWordsPerSentence: Double,
    val avgWordLength: Double,
    val uniqueWordRatio: Double // Type-token ratio
)

/**
 * Individual signal detected during analysis.
 */
data class DetectionSignal(
    val name: String,
    val description: String,
    val score: Double, // 0.0 = human, 1.0 = AI
    val weight: Double,
    val evidence: List<String> = emptyList()
)

/**
 * Vocabulary analysis results.
 */
data class VocabularyAnalysis(
    val hedgingWordCount: Int,
    val hedgingWordDensity: Double,
    val transitionPhraseCount: Int,
    val transitionPhraseDensity: Double,
    val vagueAttributionCount: Int,
    val fillerPhraseCount: Int,
    val detectedHedgingWords: List<String>,
    val detectedTransitionPhrases: List<String>,
    val detectedVagueAttributions: List<String>,
    val detectedFillerPhrases: List<String>
)

/**
 * Sentence structure analysis results.
 */
data class SentenceAnalysis(
    val sentenceLengths: List<Int>,
    val sentenceLengthVariance: Double,
    val sentenceLengthStdDev: Double,
    val passiveVoiceCount: Int,
    val passiveVoiceRatio: Double,
    val repetitiveSentenceStarters: Map<String, Int>,
    val uniformityScore: Double // Higher = more uniform (AI signal)
)

/**
 * Rhetorical pattern analysis.
 */
data class RhetoricalAnalysis(
    val formulaicOpeningCount: Int,
    val conclusionTelegraphCount: Int,
    val balancedArgumentCount: Int,
    val listParallelismScore: Double,
    val detectedPatterns: List<String>
)

/**
 * Complete analysis result.
 */
data class AnalysisResult(
    val textStats: TextStats,
    val vocabularyAnalysis: VocabularyAnalysis,
    val sentenceAnalysis: SentenceAnalysis,
    val rhetoricalAnalysis: RhetoricalAnalysis,
    val signals: List<DetectionSignal>,
    val overallScore: Double, // 0.0 = definitely human, 1.0 = definitely AI
    val confidence: Double, // How confident we are in the score
    val verdict: Verdict,
    val summary: String
)

/**
 * Final verdict categories.
 */
enum class Verdict(val label: String, val description: String) {
    LIKELY_HUMAN(
        "Likely Human",
        "The text shows characteristics typical of human writing."
    ),
    POSSIBLY_HUMAN(
        "Possibly Human",
        "The text leans toward human writing but has some AI-like patterns."
    ),
    INCONCLUSIVE(
        "Inconclusive",
        "The analysis couldn't determine the origin with confidence."
    ),
    POSSIBLY_AI(
        "Possibly AI",
        "The text shows several patterns common in AI-generated content."
    ),
    LIKELY_AI(
        "Likely AI",
        "The text exhibits multiple strong indicators of AI generation."
    )
}
