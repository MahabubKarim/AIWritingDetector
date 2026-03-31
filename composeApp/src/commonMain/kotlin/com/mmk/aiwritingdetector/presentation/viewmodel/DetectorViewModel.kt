package com.mmk.aiwritingdetector.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.aiwritingdetector.domain.usecase.AnalyzeTextUseCase
import com.mmk.aiwritingdetector.domain.usecase.SaveAnalysisUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel implementing MVI pattern for the detector screen.
 */
class DetectorViewModel(
    private val analyzeTextUseCase: AnalyzeTextUseCase,
    private val saveAnalysisUseCase: SaveAnalysisUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetectorState())
    val state: StateFlow<DetectorState> = _state.asStateFlow()

    private val _effects = Channel<DetectorEffect>(Channel.BUFFERED)
    val effects: Flow<DetectorEffect> = _effects.receiveAsFlow()

    /**
     * Process user intents.
     */
    fun onIntent(intent: DetectorIntent) {
        when (intent) {
            is DetectorIntent.UpdateText -> updateText(intent.text)
            is DetectorIntent.Analyze -> analyze()
            is DetectorIntent.SaveToHistory -> saveToHistory()
            is DetectorIntent.ClearResults -> clearResults()
            is DetectorIntent.ClearError -> clearError()
            is DetectorIntent.ClearAll -> clearAll()
            is DetectorIntent.NavigateToHistory -> navigateToHistory()
        }
    }

    private fun updateText(text: String) {
        val stats = analyzeTextUseCase.getBasicStats(text)
        _state.update { currentState ->
            currentState.copy(
                inputText = text,
                textStats = stats,
                error = null,
                isSaved = false // Reset saved state when text changes
            )
        }
    }

    private fun analyze() {
        val currentText = _state.value.inputText

        viewModelScope.launch {
            _state.update { it.copy(isAnalyzing = true, error = null, isSaved = false) }

            val result = analyzeTextUseCase.execute(currentText)

            result.fold(
                onSuccess = { analysisResult ->
                    _state.update {
                        it.copy(
                            isAnalyzing = false,
                            analysisResult = analysisResult,
                            showResults = true
                        )
                    }
                    _effects.send(DetectorEffect.AnalysisComplete)
                    _effects.send(DetectorEffect.ScrollToResults)
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isAnalyzing = false,
                            error = error.message
                        )
                    }
                    _effects.send(DetectorEffect.ShowError(error.message ?: "Analysis failed"))
                }
            )
        }
    }

    private fun saveToHistory() {
        val currentState = _state.value
        val result = currentState.analysisResult ?: return
        val text = currentState.inputText

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            saveAnalysisUseCase.execute(text, result).fold(
                onSuccess = {
                    _state.update { it.copy(isSaving = false, isSaved = true) }
                    _effects.send(DetectorEffect.SavedToHistory)
                },
                onFailure = { error ->
                    _state.update { it.copy(isSaving = false) }
                    _effects.send(DetectorEffect.ShowError(error.message ?: "Failed to save"))
                }
            )
        }
    }

    private fun clearResults() {
        _state.update {
            it.copy(
                analysisResult = null,
                showResults = false,
                isSaved = false
            )
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun clearAll() {
        _state.value = DetectorState()
    }

    private fun navigateToHistory() {
        viewModelScope.launch {
            _effects.send(DetectorEffect.NavigateToHistory)
        }
    }
}
