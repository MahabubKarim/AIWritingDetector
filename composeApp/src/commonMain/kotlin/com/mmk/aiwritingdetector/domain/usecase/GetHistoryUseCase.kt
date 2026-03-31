package com.mmk.aiwritingdetector.domain.usecase

import com.mmk.aiwritingdetector.data.repository.AnalysisHistoryRepository
import com.mmk.aiwritingdetector.domain.model.AnalysisHistory
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving analysis history.
 */
class GetHistoryUseCase(
    private val repository: AnalysisHistoryRepository
) {
    fun execute(): Flow<List<AnalysisHistory>> {
        return repository.getAllHistory()
    }
    
    suspend fun getById(id: Long): AnalysisHistory? {
        return repository.getById(id)
    }
    
    suspend fun deleteById(id: Long) {
        repository.deleteById(id)
    }
    
    suspend fun deleteAll() {
        repository.deleteAll()
    }
    
    suspend fun search(query: String): List<AnalysisHistory> {
        return repository.searchByText(query)
    }
}
