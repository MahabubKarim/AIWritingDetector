package com.mmk.aiwritingdetector.domain.usecase

import com.mmk.aiwritingdetector.data.repository.AnalysisHistoryRepository
import com.mmk.aiwritingdetector.domain.model.AnalysisResult

/**
 * Use case for saving analysis results to history.
 */
class SaveAnalysisUseCase(
    private val repository: AnalysisHistoryRepository
) {
    suspend fun execute(fullText: String, result: AnalysisResult): Result<Long> {
        return try {
            val id = repository.saveAnalysis(fullText, result)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
