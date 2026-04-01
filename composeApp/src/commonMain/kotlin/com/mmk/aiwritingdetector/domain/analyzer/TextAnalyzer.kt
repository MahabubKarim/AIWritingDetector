package com.mmk.aiwritingdetector.domain.analyzer

import com.mmk.aiwritingdetector.domain.model.*
import com.mmk.aiwritingdetector.domain.util.format
import kotlin.math.sqrt

/**
 * Core text analyzer that detects AI writing patterns.
 * 
 * Detection methods based on:
 * - Perplexity (word predictability)
 * - Burstiness (sentence length variation)
 * - Vocabulary patterns (AI-specific words)
 * - Structural regularity (paragraph uniformity)
 * - Template detection (PTE-specific patterns)
 */
class TextAnalyzer {

    // Track detected patterns during analysis for evidence display
    private var detectedAiVocabulary = mutableListOf<String>()
    private var detectedHumanIndicators = mutableListOf<String>()
    private var detectedDramaticPhrases = mutableListOf<String>()
    private var detectedTemplates = mutableListOf<String>()
    private var currentText = ""

    /**
     * Performs complete analysis on the input text.
     */
    fun analyze(text: String): AnalysisResult {
        // Reset state for new analysis
        currentText = text
        detectedAiVocabulary.clear()
        detectedHumanIndicators.clear()
        detectedDramaticPhrases.clear()
        detectedTemplates.clear()

        val textStats = calculateTextStats(text)
        val vocabularyAnalysis = analyzeVocabulary(text, textStats.wordCount)
        val sentenceAnalysis = analyzeSentences(text)
        val rhetoricalAnalysis = analyzeRhetoric(text)

        // Pre-scan for additional patterns
        scanForAiVocabulary(text)
        scanForHumanIndicators(text)
        scanForDramaticPhrases(text)
        scanForTemplates(text)

        val signals = buildSignals(
            textStats,
            vocabularyAnalysis,
            sentenceAnalysis,
            rhetoricalAnalysis
        )

        val overallScore = calculateOverallScore(signals)
        val confidence = calculateConfidence(textStats, signals)
        val verdict = determineVerdict(overallScore, confidence)
        val summary = generateSummary(overallScore, confidence, signals)

        return AnalysisResult(
            textStats = textStats,
            vocabularyAnalysis = vocabularyAnalysis,
            sentenceAnalysis = sentenceAnalysis,
            rhetoricalAnalysis = rhetoricalAnalysis,
            signals = signals,
            overallScore = overallScore,
            confidence = confidence,
            verdict = verdict,
            summary = summary
        )
    }

    /**
     * Calculate basic text statistics.
     */
    fun calculateTextStats(text: String): TextStats {
        val words = extractWords(text)
        val sentences = extractSentences(text)
        val paragraphs = text.split(Regex("\\n\\s*\\n")).filter { it.isNotBlank() }

        val wordCount = words.size
        val sentenceCount = sentences.size.coerceAtLeast(1)

        val avgWordsPerSentence = if (sentenceCount > 0) {
            wordCount.toDouble() / sentenceCount
        } else 0.0

        val avgWordLength = if (wordCount > 0) {
            words.sumOf { it.length }.toDouble() / wordCount
        } else 0.0

        val uniqueWords = words.map { it.lowercase() }.toSet()
        val uniqueWordRatio = if (wordCount > 0) {
            uniqueWords.size.toDouble() / wordCount
        } else 0.0

        return TextStats(
            characterCount = text.length,
            characterCountNoSpaces = text.replace(Regex("\\s"), "").length,
            wordCount = wordCount,
            sentenceCount = sentenceCount,
            paragraphCount = paragraphs.size.coerceAtLeast(1),
            avgWordsPerSentence = avgWordsPerSentence,
            avgWordLength = avgWordLength,
            uniqueWordRatio = uniqueWordRatio
        )
    }

    /**
     * Analyze vocabulary patterns.
     */
    private fun analyzeVocabulary(text: String, wordCount: Int): VocabularyAnalysis {
        val lowerText = text.lowercase()

        val detectedHedging = PatternDictionaries.hedgingWords.filter { lowerText.contains(it) }
        val hedgingCount = detectedHedging.sumOf { word ->
            Regex("\\b${Regex.escape(word)}\\b", RegexOption.IGNORE_CASE)
                .findAll(text).count()
        }

        val detectedTransitions = PatternDictionaries.transitionPhrases.filter { lowerText.contains(it) }
        val transitionCount = detectedTransitions.sumOf { phrase ->
            Regex(Regex.escape(phrase), RegexOption.IGNORE_CASE)
                .findAll(text).count()
        }

        val detectedVague = PatternDictionaries.vagueAttributions.filter { lowerText.contains(it) }
        val vagueCount = detectedVague.sumOf { phrase ->
            Regex(Regex.escape(phrase), RegexOption.IGNORE_CASE)
                .findAll(text).count()
        }

        val detectedFillers = PatternDictionaries.fillerPhrases.filter { lowerText.contains(it) }
        val fillerCount = detectedFillers.sumOf { phrase ->
            Regex(Regex.escape(phrase), RegexOption.IGNORE_CASE)
                .findAll(text).count()
        }

        val effectiveWordCount = wordCount.coerceAtLeast(1)

        return VocabularyAnalysis(
            hedgingWordCount = hedgingCount,
            hedgingWordDensity = hedgingCount.toDouble() / effectiveWordCount * 100,
            transitionPhraseCount = transitionCount,
            transitionPhraseDensity = transitionCount.toDouble() / effectiveWordCount * 100,
            vagueAttributionCount = vagueCount,
            fillerPhraseCount = fillerCount,
            detectedHedgingWords = detectedHedging,
            detectedTransitionPhrases = detectedTransitions,
            detectedVagueAttributions = detectedVague,
            detectedFillerPhrases = detectedFillers
        )
    }

    /**
     * Analyze sentence structure patterns.
     */
    private fun analyzeSentences(text: String): SentenceAnalysis {
        val sentences = extractSentences(text)
        val sentenceLengths = sentences.map { extractWords(it).size }

        val avgLength = if (sentenceLengths.isNotEmpty()) {
            sentenceLengths.average()
        } else 0.0

        val variance = if (sentenceLengths.size > 1) {
            sentenceLengths.map { (it - avgLength) * (it - avgLength) }.average()
        } else 0.0

        val stdDev = sqrt(variance)

        // Passive voice detection
        val passiveCount = PatternDictionaries.passiveIndicators.sumOf { indicator ->
            Regex("\\b${Regex.escape(indicator)}\\b", RegexOption.IGNORE_CASE)
                .findAll(text).count()
        }
        val passiveRatio = if (sentences.isNotEmpty()) {
            passiveCount.toDouble() / sentences.size
        } else 0.0

        // Sentence starter analysis
        val starters = sentences.map { sentence ->
            val words = extractWords(sentence)
            if (words.size >= 2) {
                "${words[0].lowercase()} ${words[1].lowercase()}"
            } else if (words.isNotEmpty()) {
                words[0].lowercase()
            } else ""
        }.filter { it.isNotBlank() }

        val starterFrequency = starters.groupingBy { it }.eachCount()
            .filter { it.value > 1 }

        // Uniformity score — how similar are sentence lengths?
        // Low burstiness = AI signature
        val uniformityScore = if (avgLength > 0 && sentenceLengths.isNotEmpty()) {
            1.0 - (stdDev / avgLength).coerceIn(0.0, 1.0)
        } else 0.0

        return SentenceAnalysis(
            sentenceLengths = sentenceLengths,
            sentenceLengthVariance = variance,
            sentenceLengthStdDev = stdDev,
            passiveVoiceCount = passiveCount,
            passiveVoiceRatio = passiveRatio,
            repetitiveSentenceStarters = starterFrequency,
            uniformityScore = uniformityScore
        )
    }

    /**
     * Analyze rhetorical patterns.
     */
    private fun analyzeRhetoric(text: String): RhetoricalAnalysis {
        val lowerText = text.lowercase()

        val formulaicCount = PatternDictionaries.formulaicOpenings.count { lowerText.contains(it) }
        val conclusionCount = PatternDictionaries.conclusionTelegraphs.count { lowerText.contains(it) }
        val balancedCount = PatternDictionaries.balancedArgumentMarkers.count { lowerText.contains(it) }

        val detectedPatterns = mutableListOf<String>()

        if (formulaicCount > 0) {
            detectedPatterns.add("Formulaic openings detected ($formulaicCount)")
        }
        if (conclusionCount > 0) {
            detectedPatterns.add("Conclusion telegraphing ($conclusionCount)")
        }
        if (balancedCount > 0) {
            detectedPatterns.add("Balanced argument formula ($balancedCount)")
        }

        // List parallelism — check for bullet-point-like structures
        val listPatterns = Regex("^\\s*[-•*]\\s+", RegexOption.MULTILINE).findAll(text).count()
        val parallelismScore = if (listPatterns > 3) 0.8 else listPatterns * 0.15

        return RhetoricalAnalysis(
            formulaicOpeningCount = formulaicCount,
            conclusionTelegraphCount = conclusionCount,
            balancedArgumentCount = balancedCount,
            listParallelismScore = parallelismScore.coerceIn(0.0, 1.0),
            detectedPatterns = detectedPatterns
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PATTERN SCANNING METHODS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Scan for AI-specific vocabulary (era-specific overused words).
     */
    private fun scanForAiVocabulary(text: String) {
        val lowerText = text.lowercase()
        PatternDictionaries.allAiVocabulary.forEach { word ->
            if (Regex("\\b${Regex.escape(word)}\\b").containsMatchIn(lowerText)) {
                detectedAiVocabulary.add(word)
            }
        }
    }

    /**
     * Scan for human writing indicators (increases burstiness/perplexity).
     */
    private fun scanForHumanIndicators(text: String) {
        val lowerText = text.lowercase()
        PatternDictionaries.humanWritingIndicators.forEach { indicator ->
            if (Regex("\\b${Regex.escape(indicator)}\\b").containsMatchIn(lowerText)) {
                detectedHumanIndicators.add(indicator)
            }
        }
    }

    /**
     * Scan for dramatic/emphatic AI phrases.
     */
    private fun scanForDramaticPhrases(text: String) {
        val lowerText = text.lowercase()
        PatternDictionaries.dramaticPhrases.forEach { phrase ->
            if (lowerText.contains(phrase)) {
                detectedDramaticPhrases.add(phrase)
            }
        }
    }

    /**
     * Scan for PTE and other templates.
     */
    private fun scanForTemplates(text: String) {
        val lowerText = text.lowercase()
        PatternDictionaries.allTemplates.forEach { template ->
            if (lowerText.contains(template)) {
                detectedTemplates.add(template)
            }
        }
    }

    /**
     * Count contractions in text.
     * AI tends to avoid contractions in formal writing.
     */
    private fun countContractions(text: String): Int {
        val lowerText = text.lowercase()
        return PatternDictionaries.contractionWords.sumOf { contraction ->
            Regex("\\b${Regex.escape(contraction)}\\b").findAll(lowerText).count()
        }
    }

    /**
     * Calculate paragraph uniformity (structural regularity).
     * High uniformity = AI signature (low burstiness).
     */
    private fun calculateParagraphUniformity(text: String): Double {
        val paragraphs = text.split(Regex("\\n\\s*\\n"))
            .filter { it.isNotBlank() }
            .map { extractWords(it).size }

        if (paragraphs.size < 2) return 0.5

        val avgLength = paragraphs.average()
        if (avgLength == 0.0) return 0.5

        val variance = paragraphs.map { (it - avgLength) * (it - avgLength) }.average()
        val stdDev = sqrt(variance)
        val cv = stdDev / avgLength  // Coefficient of variation

        // Lower CV = more uniform = higher AI probability
        return (1.0 - cv).coerceIn(0.0, 1.0)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SIGNAL BUILDING
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Build detection signals from analysis results.
     */
    private fun buildSignals(
        stats: TextStats,
        vocab: VocabularyAnalysis,
        sentences: SentenceAnalysis,
        rhetoric: RhetoricalAnalysis
    ): List<DetectionSignal> {
        val signals = mutableListOf<DetectionSignal>()

        // Signal 1: Sentence length uniformity (Burstiness measure)
        // Low burstiness = uniform sentences = AI
        val uniformityScore = when {
            sentences.sentenceLengthStdDev < 3.0 -> 0.9
            sentences.sentenceLengthStdDev < 5.0 -> 0.7
            sentences.sentenceLengthStdDev < 8.0 -> 0.4
            sentences.sentenceLengthStdDev < 12.0 -> 0.2
            else -> 0.1
        }
        signals.add(
            DetectionSignal(
                name = "Sentence Uniformity",
                description = "AI text tends to have uniform sentence lengths (low burstiness)",
                score = uniformityScore,
                weight = 0.12,
                evidence = listOf("Standard deviation: ${sentences.sentenceLengthStdDev.format(1)} words")
            )
        )

        // Signal 2: Hedging word density
        val hedgingScore = when {
            vocab.hedgingWordDensity > 3.0 -> 0.9
            vocab.hedgingWordDensity > 2.0 -> 0.7
            vocab.hedgingWordDensity > 1.0 -> 0.4
            vocab.hedgingWordDensity > 0.5 -> 0.2
            else -> 0.1
        }
        signals.add(
            DetectionSignal(
                name = "Hedging Words",
                description = "Excessive qualifiers like 'generally', 'typically', 'often'",
                score = hedgingScore,
                weight = 0.10,
                evidence = vocab.detectedHedgingWords.take(5)
            )
        )

        // Signal 3: Transition phrase density
        val transitionScore = when {
            vocab.transitionPhraseDensity > 2.0 -> 0.9
            vocab.transitionPhraseDensity > 1.0 -> 0.6
            vocab.transitionPhraseDensity > 0.5 -> 0.3
            else -> 0.1
        }
        signals.add(
            DetectionSignal(
                name = "Transition Phrases",
                description = "Overuse of formal connectors like 'Furthermore', 'Moreover'",
                score = transitionScore,
                weight = 0.10,
                evidence = vocab.detectedTransitionPhrases.take(5)
            )
        )

        // Signal 4: Vague attributions
        val vagueScore = when {
            vocab.vagueAttributionCount > 3 -> 0.9
            vocab.vagueAttributionCount > 1 -> 0.6
            vocab.vagueAttributionCount > 0 -> 0.3
            else -> 0.0
        }
        signals.add(
            DetectionSignal(
                name = "Vague Attributions",
                description = "Citations without sources like 'Studies show', 'Experts believe'",
                score = vagueScore,
                weight = 0.10,
                evidence = vocab.detectedVagueAttributions.take(3)
            )
        )

        // Signal 5: Passive voice ratio
        val passiveScore = when {
            sentences.passiveVoiceRatio > 0.4 -> 0.8
            sentences.passiveVoiceRatio > 0.25 -> 0.5
            sentences.passiveVoiceRatio > 0.15 -> 0.3
            else -> 0.1
        }
        signals.add(
            DetectionSignal(
                name = "Passive Voice",
                description = "Higher passive voice usage than typical human writing",
                score = passiveScore,
                weight = 0.06,
                evidence = listOf("Ratio: ${ (sentences.passiveVoiceRatio * 100).format(0) }%")
            )
        )

        // Signal 6: Vocabulary diversity (type-token ratio / Perplexity proxy)
        val diversityScore = when {
            stats.uniqueWordRatio < 0.35 -> 0.8
            stats.uniqueWordRatio < 0.45 -> 0.5
            stats.uniqueWordRatio < 0.55 -> 0.3
            else -> 0.1
        }
        signals.add(
            DetectionSignal(
                name = "Vocabulary Diversity",
                description = "Low unique word ratio suggests repetitive vocabulary",
                score = diversityScore,
                weight = 0.08,
                evidence = listOf("Unique word ratio: ${ (stats.uniqueWordRatio * 100).format(0) }%")
            )
        )

        // Signal 7: AI Vocabulary (era-specific overused words)
        val aiVocabCount = detectedAiVocabulary.size
        val aiVocabScore = when {
            aiVocabCount > 8 -> 0.9
            aiVocabCount > 5 -> 0.7
            aiVocabCount > 3 -> 0.5
            aiVocabCount > 1 -> 0.3
            else -> 0.1
        }
        signals.add(
            DetectionSignal(
                name = "AI Vocabulary",
                description = "Era-specific AI words like 'delve', 'tapestry', 'multifaceted'",
                score = aiVocabScore,
                weight = 0.12,
                evidence = detectedAiVocabulary.take(5)
            )
        )

        // Signal 8: Formulaic patterns
        val formulaicScore = when {
            rhetoric.formulaicOpeningCount > 2 -> 0.9
            rhetoric.formulaicOpeningCount > 0 -> 0.5
            else -> 0.0
        }
        if (rhetoric.formulaicOpeningCount > 0) {
            signals.add(
                DetectionSignal(
                    name = "Formulaic Openings",
                    description = "Clichéd openings like 'In today's world', 'Throughout history'",
                    score = formulaicScore,
                    weight = 0.08,
                    evidence = rhetoric.detectedPatterns.filter { it.contains("Formulaic") }
                )
            )
        }

        // Signal 9: Conclusion telegraphing
        val conclusionScore = when {
            rhetoric.conclusionTelegraphCount > 2 -> 0.8
            rhetoric.conclusionTelegraphCount > 0 -> 0.4
            else -> 0.0
        }
        if (rhetoric.conclusionTelegraphCount > 0) {
            signals.add(
                DetectionSignal(
                    name = "Conclusion Telegraphing",
                    description = "Explicit markers like 'In conclusion', 'To summarize'",
                    score = conclusionScore,
                    weight = 0.06,
                    evidence = rhetoric.detectedPatterns.filter { it.contains("Conclusion") }
                )
            )
        }

        // Signal 10: Filler phrases
        val fillerScore = when {
            vocab.fillerPhraseCount > 3 -> 0.8
            vocab.fillerPhraseCount > 1 -> 0.5
            vocab.fillerPhraseCount > 0 -> 0.2
            else -> 0.0
        }
        if (vocab.fillerPhraseCount > 0) {
            signals.add(
                DetectionSignal(
                    name = "Filler Phrases",
                    description = "Padding phrases that add no value",
                    score = fillerScore,
                    weight = 0.06,
                    evidence = vocab.detectedFillerPhrases.take(3)
                )
            )
        }

        // Signal 11: Lack of Contractions (formal style)
        val contractionCount = countContractions(currentText)
        val expectedContractions = (stats.wordCount / 50).coerceAtLeast(1)
        val contractionRatio = contractionCount.toDouble() / expectedContractions
        val contractionScore = when {
            contractionRatio < 0.1 && stats.wordCount > 100 -> 0.7
            contractionRatio < 0.3 -> 0.5
            contractionRatio < 0.6 -> 0.3
            else -> 0.1
        }
        signals.add(
            DetectionSignal(
                name = "Formal Style",
                description = "Lack of contractions suggests overly formal AI writing",
                score = contractionScore,
                weight = 0.05,
                evidence = listOf("Contractions found: $contractionCount")
            )
        )

        // Signal 12: Human Writing Indicators (negative signal - reduces AI score)
        val humanIndicatorCount = detectedHumanIndicators.size
        val humanScore = when {
            humanIndicatorCount > 5 -> 0.1  // Very likely human
            humanIndicatorCount > 3 -> 0.2
            humanIndicatorCount > 1 -> 0.3
            humanIndicatorCount > 0 -> 0.4
            else -> 0.6  // Absence of human markers is slightly suspicious
        }
        signals.add(
            DetectionSignal(
                name = "Human Markers",
                description = "Personal expressions, colloquialisms, informal language",
                score = humanScore,
                weight = 0.07,
                evidence = if (detectedHumanIndicators.isEmpty()) 
                    listOf("None detected") 
                else 
                    detectedHumanIndicators.take(3)
            )
        )

        return signals
    }

    /**
     * Calculate weighted overall score.
     */
    private fun calculateOverallScore(signals: List<DetectionSignal>): Double {
        if (signals.isEmpty()) return 0.5

        val weightedSum = signals.sumOf { it.score * it.weight }
        val totalWeight = signals.sumOf { it.weight }

        return if (totalWeight > 0) {
            (weightedSum / totalWeight).coerceIn(0.0, 1.0)
        } else 0.5
    }

    /**
     * Calculate confidence based on text length and signal agreement.
     */
    private fun calculateConfidence(stats: TextStats, signals: List<DetectionSignal>): Double {
        // More text = higher confidence (up to a point)
        val lengthFactor = when {
            stats.wordCount < 50 -> 0.3
            stats.wordCount < 100 -> 0.5
            stats.wordCount < 200 -> 0.7
            stats.wordCount < 500 -> 0.85
            else -> 1.0
        }

        // Signal agreement — if signals point the same direction, higher confidence
        val scores = signals.map { it.score }
        val avgScore = scores.average()
        val scoreVariance = if (scores.size > 1) {
            scores.map { (it - avgScore) * (it - avgScore) }.average()
        } else 0.0

        val agreementFactor = 1.0 - sqrt(scoreVariance).coerceIn(0.0, 0.5)

        return (lengthFactor * 0.6 + agreementFactor * 0.4).coerceIn(0.0, 1.0)
    }

    /**
     * Determine verdict from score and confidence.
     */
    private fun determineVerdict(score: Double, confidence: Double): Verdict {
        return when {
            confidence < 0.4 -> Verdict.INCONCLUSIVE
            score < 0.25 -> Verdict.LIKELY_HUMAN
            score < 0.40 -> Verdict.POSSIBLY_HUMAN
            score < 0.60 -> Verdict.INCONCLUSIVE
            score < 0.75 -> Verdict.POSSIBLY_AI
            else -> Verdict.LIKELY_AI
        }
    }

    /**
     * Generate a human-readable summary.
     */
    private fun generateSummary(
        score: Double,
        confidence: Double,
        signals: List<DetectionSignal>
    ): String {
        val topSignals = signals
            .filter { it.score > 0.5 }
            .sortedByDescending { it.score * it.weight }
            .take(3)

        val intro = when {
            score < 0.3 -> "This text shows strong characteristics of human writing."
            score < 0.5 -> "This text leans toward human writing with some AI-like patterns."
            score < 0.7 -> "This text has mixed signals that make it difficult to classify."
            else -> "This text exhibits multiple patterns commonly seen in AI-generated content."
        }

        val evidencePart = if (topSignals.isNotEmpty()) {
            "\n\nKey factors: " + topSignals.joinToString("; ") { it.name.lowercase() }
        } else ""

        val caveat = if (confidence < 0.6) {
            "\n\nNote: Analysis confidence is limited due to text length or mixed signals."
        } else ""

        return intro + evidencePart + caveat
    }

    // --- Utility Functions ---

    private fun extractWords(text: String): List<String> {
        return text.split(Regex("[\\s\\p{Punct}]+"))
            .filter { it.isNotBlank() && it.any { c -> c.isLetter() } }
    }

    private fun extractSentences(text: String): List<String> {
        return text.split(Regex("(?<=[.!?])\\s+"))
            .map { it.trim() }
            .filter { it.isNotBlank() && it.length > 2 }
    }
}
