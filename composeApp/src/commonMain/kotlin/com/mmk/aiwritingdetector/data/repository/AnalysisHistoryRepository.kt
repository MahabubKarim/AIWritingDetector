package com.mmk.aiwritingdetector.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.mmk.aiwritingdetector.data.db.AIWritingDetectorDatabase
import com.mmk.aiwritingdetector.data.db.AnalysisHistoryEntity
import com.mmk.aiwritingdetector.domain.model.AnalysisHistory
import com.mmk.aiwritingdetector.domain.model.AnalysisResult
import com.mmk.aiwritingdetector.domain.model.DetectionSignal
import com.mmk.aiwritingdetector.domain.model.SignalData
import com.mmk.aiwritingdetector.domain.model.Verdict
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Repository for managing analysis history persistence.
 */
class AnalysisHistoryRepository(
    private val database: AIWritingDetectorDatabase
) {
    private val queries = database.analysisHistoryQueries

    /**
     * Get all history as a Flow.
     */
    fun getAllHistory(): Flow<List<AnalysisHistory>> {
        return queries.selectAll()
            .asFlow()
            .mapToList<AnalysisHistoryEntity>(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }

    /**
     * Get single analysis by ID.
     */
    suspend fun getById(id: Long): AnalysisHistory? = withContext(Dispatchers.Default) {
        queries.selectById(id).executeAsOneOrNull()?.toDomain()
    }

    /**
     * Save analysis result to history.
     */
    suspend fun saveAnalysis(
        fullText: String,
        result: AnalysisResult
    ): Long = withContext(Dispatchers.Default) {
        val textPreview = fullText.take(100).let {
            if (fullText.length > 100) "$it..." else it
        }
        
        val signalsJson = serializeSignals(result.signals)
        val now = kotlin.time.Clock.System.now()

        queries.insert(
            textPreview = textPreview,
            fullText = fullText,
            overallScore = result.overallScore,
            confidence = result.confidence,
            verdict = result.verdict.name,
            summary = result.summary,
            wordCount = result.textStats.wordCount.toLong(),
            sentenceCount = result.textStats.sentenceCount.toLong(),
            signalsJson = signalsJson,
            createdAt = now.toEpochMilliseconds()
        )
        
        // Return the last inserted ID
        queries.selectAll().executeAsList().firstOrNull()?.id ?: 0L
    }

    /**
     * Delete analysis by ID.
     */
    suspend fun deleteById(id: Long) = withContext(Dispatchers.Default) {
        queries.deleteById(id)
    }

    /**
     * Delete all history.
     */
    suspend fun deleteAll() = withContext(Dispatchers.Default) {
        queries.deleteAll()
    }

    /**
     * Get history count.
     */
    suspend fun getCount(): Long = withContext(Dispatchers.Default) {
        queries.count().executeAsOne()
    }

    /**
     * Search history by text.
     */
    suspend fun searchByText(query: String): List<AnalysisHistory> = withContext(Dispatchers.Default) {
        queries.searchByText(query).executeAsList().map { it.toDomain() }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // MAPPING & SERIALIZATION
    // ═══════════════════════════════════════════════════════════════════════════

    private fun AnalysisHistoryEntity.toDomain(): AnalysisHistory {
        return AnalysisHistory(
            id = id,
            textPreview = textPreview,
            fullText = fullText,
            overallScore = overallScore,
            confidence = confidence,
            verdict = Verdict.valueOf(verdict),
            summary = summary,
            wordCount = wordCount.toInt(),
            sentenceCount = sentenceCount.toInt(),
            signals = deserializeSignals(signalsJson),
            createdAt = Instant.fromEpochMilliseconds(createdAt)
        )
    }

    /**
     * Simple JSON serialization for signals.
     * Format: name|description|score|weight|evidence1,evidence2;...
     */
    private fun serializeSignals(signals: List<DetectionSignal>): String {
        return signals.joinToString(";;") { signal ->
            val evidenceStr = signal.evidence.joinToString(",,")
            "${signal.name}||${signal.description}||${signal.score}||${signal.weight}||$evidenceStr"
        }
    }

    private fun deserializeSignals(json: String): List<DetectionSignal> {
        if (json.isBlank()) return emptyList()
        
        return json.split(";;").mapNotNull { signalStr ->
            val parts = signalStr.split("||")
            if (parts.size >= 4) {
                DetectionSignal(
                    name = parts[0],
                    description = parts[1],
                    score = parts[2].toDoubleOrNull() ?: 0.0,
                    weight = parts[3].toDoubleOrNull() ?: 0.0,
                    evidence = if (parts.size > 4 && parts[4].isNotBlank()) {
                        parts[4].split(",,")
                    } else emptyList()
                )
            } else null
        }
    }
}
