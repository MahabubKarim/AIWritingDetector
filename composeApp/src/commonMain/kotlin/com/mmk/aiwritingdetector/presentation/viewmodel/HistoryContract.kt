package com.mmk.aiwritingdetector.presentation.viewmodel

import com.mmk.aiwritingdetector.domain.model.AnalysisHistory

/**
 * MVI Contract for History Screen.
 */

data class HistoryState(
    val historyList: List<AnalysisHistory> = emptyList(),
    val isLoading: Boolean = true,
    val selectedItem: AnalysisHistory? = null,
    val showDeleteConfirmation: Boolean = false,
    val itemToDelete: AnalysisHistory? = null,
    val showClearAllConfirmation: Boolean = false,
    val searchQuery: String = "",
    val error: String? = null
)

sealed class HistoryIntent {
    data object LoadHistory : HistoryIntent()
    data class SelectItem(val item: AnalysisHistory) : HistoryIntent()
    data object ClearSelection : HistoryIntent()
    data class RequestDelete(val item: AnalysisHistory) : HistoryIntent()
    data object ConfirmDelete : HistoryIntent()
    data object CancelDelete : HistoryIntent()
    data object RequestClearAll : HistoryIntent()
    data object ConfirmClearAll : HistoryIntent()
    data object CancelClearAll : HistoryIntent()
    data class Search(val query: String) : HistoryIntent()
}

sealed class HistoryEffect {
    data class ShowError(val message: String) : HistoryEffect()
    data object ItemDeleted : HistoryEffect()
    data object AllCleared : HistoryEffect()
}
