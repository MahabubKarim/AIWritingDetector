package com.mmk.aiwritingdetector.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.aiwritingdetector.domain.model.AnalysisHistory
import com.mmk.aiwritingdetector.domain.usecase.GetHistoryUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for History Screen following MVI pattern.
 */
class HistoryViewModel(
    private val getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    private val _effects = Channel<HistoryEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        loadHistory()
    }

    fun processIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.LoadHistory -> loadHistory()
            is HistoryIntent.SelectItem -> selectItem(intent.item)
            is HistoryIntent.ClearSelection -> clearSelection()
            is HistoryIntent.RequestDelete -> requestDelete(intent.item)
            is HistoryIntent.ConfirmDelete -> confirmDelete()
            is HistoryIntent.CancelDelete -> cancelDelete()
            is HistoryIntent.RequestClearAll -> requestClearAll()
            is HistoryIntent.ConfirmClearAll -> confirmClearAll()
            is HistoryIntent.CancelClearAll -> cancelClearAll()
            is HistoryIntent.Search -> search(intent.query)
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                getHistoryUseCase.execute().collect { historyList ->
                    _state.update { 
                        it.copy(
                            historyList = historyList,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load history"
                    )
                }
                _effects.send(HistoryEffect.ShowError(e.message ?: "Failed to load history"))
            }
        }
    }

    private fun selectItem(item: AnalysisHistory) {
        _state.update { it.copy(selectedItem = item) }
    }

    private fun clearSelection() {
        _state.update { it.copy(selectedItem = null) }
    }

    private fun requestDelete(item: AnalysisHistory) {
        _state.update { 
            it.copy(
                showDeleteConfirmation = true,
                itemToDelete = item
            )
        }
    }

    private fun confirmDelete() {
        val item = _state.value.itemToDelete ?: return
        
        viewModelScope.launch {
            try {
                getHistoryUseCase.deleteById(item.id)
                _state.update { 
                    it.copy(
                        showDeleteConfirmation = false,
                        itemToDelete = null,
                        selectedItem = if (it.selectedItem?.id == item.id) null else it.selectedItem
                    )
                }
                _effects.send(HistoryEffect.ItemDeleted)
            } catch (e: Exception) {
                _effects.send(HistoryEffect.ShowError(e.message ?: "Failed to delete"))
            }
        }
    }

    private fun cancelDelete() {
        _state.update { 
            it.copy(
                showDeleteConfirmation = false,
                itemToDelete = null
            )
        }
    }

    private fun requestClearAll() {
        _state.update { it.copy(showClearAllConfirmation = true) }
    }

    private fun confirmClearAll() {
        viewModelScope.launch {
            try {
                getHistoryUseCase.deleteAll()
                _state.update { 
                    it.copy(
                        showClearAllConfirmation = false,
                        selectedItem = null
                    )
                }
                _effects.send(HistoryEffect.AllCleared)
            } catch (e: Exception) {
                _effects.send(HistoryEffect.ShowError(e.message ?: "Failed to clear history"))
            }
        }
    }

    private fun cancelClearAll() {
        _state.update { it.copy(showClearAllConfirmation = false) }
    }

    private fun search(query: String) {
        _state.update { it.copy(searchQuery = query) }
        
        viewModelScope.launch {
            try {
                if (query.isBlank()) {
                    loadHistory()
                } else {
                    val results = getHistoryUseCase.search(query)
                    _state.update { it.copy(historyList = results) }
                }
            } catch (e: Exception) {
                _effects.send(HistoryEffect.ShowError(e.message ?: "Search failed"))
            }
        }
    }
}
