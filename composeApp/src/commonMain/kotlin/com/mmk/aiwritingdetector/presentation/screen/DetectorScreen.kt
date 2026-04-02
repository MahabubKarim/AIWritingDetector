package com.mmk.aiwritingdetector.presentation.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mmk.aiwritingdetector.domain.model.AnalysisResult
import com.mmk.aiwritingdetector.domain.util.format
import com.mmk.aiwritingdetector.presentation.components.*
import com.mmk.aiwritingdetector.presentation.theme.AppColors
import com.mmk.aiwritingdetector.presentation.theme.AppTypography
import com.mmk.aiwritingdetector.presentation.viewmodel.DetectorEffect
import com.mmk.aiwritingdetector.presentation.viewmodel.DetectorIntent
import com.mmk.aiwritingdetector.presentation.viewmodel.DetectorState
import com.mmk.aiwritingdetector.presentation.viewmodel.DetectorViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main detector screen — the hero of our app.
 */
class DetectorScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: DetectorViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        // Handle effects
        LaunchedEffect(Unit) {
            viewModel.effects.collectLatest { effect ->
                when (effect) {
                    is DetectorEffect.ScrollToResults -> {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    }
                    is DetectorEffect.NavigateToHistory -> {
                        navigator.push(HistoryScreen())
                    }
                    is DetectorEffect.SavedToHistory -> {
                        // Could show snackbar
                    }
                    else -> {}
                }
            }
        }

        DetectorContent(
            state = state,
            onIntent = viewModel::onIntent,
            scrollState = scrollState
        )
    }
}

@Composable
private fun DetectorContent(
    state: DetectorState,
    onIntent: (DetectorIntent) -> Unit,
    scrollState: androidx.compose.foundation.ScrollState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Hero header
            HeaderSection(onHistoryClick = { onIntent(DetectorIntent.NavigateToHistory) })

            Spacer(modifier = Modifier.height(40.dp))

            // Input section
            InputSection(
                text = state.inputText,
                onTextChange = { onIntent(DetectorIntent.UpdateText(it)) },
                characterCount = state.characterCount,
                wordCount = state.wordCount,
                canAnalyze = state.canAnalyze,
                isAnalyzing = state.isAnalyzing,
                onAnalyze = { onIntent(DetectorIntent.Analyze) },
                onClear = { onIntent(DetectorIntent.ClearAll) },
                error = state.error
            )

            // Analyzing state
            AnimatedVisibility(
                visible = state.isAnalyzing,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(48.dp))
                    AnalyzingIndicator()
                }
            }

            // Results section
            AnimatedVisibility(
                visible = state.showResults && state.analysisResult != null,
                enter = fadeIn(animationSpec = tween(400)) +
                        expandVertically(animationSpec = tween(500)),
                exit = fadeOut() + shrinkVertically()
            ) {
                state.analysisResult?.let { result ->
                    Column {
                        Spacer(modifier = Modifier.height(48.dp))
                        ResultsSection(
                            result = result,
                            canSave = state.canSave,
                            isSaving = state.isSaving,
                            isSaved = state.isSaved,
                            onSave = { onIntent(DetectorIntent.SaveToHistory) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(onHistoryClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            FilledTonalButton(
                onClick = onHistoryClick,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("History")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "AI Writing",
            style = AppTypography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Detector",
            style = AppTypography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Analyze text patterns to determine if content was written by a human or generated by AI.",
            style = AppTypography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.widthIn(max = 480.dp)
        )
    }
}

@Composable
private fun InputSection(
    text: String,
    onTextChange: (String) -> Unit,
    characterCount: Int,
    wordCount: Int,
    canAnalyze: Boolean,
    isAnalyzing: Boolean,
    onAnalyze: () -> Unit,
    onClear: () -> Unit,
    error: String?
) {
    Column(
        modifier = Modifier.widthIn(max = 720.dp)
    ) {
        ElegantTextArea(
            value = text,
            onValueChange = onTextChange,
            placeholder = "Paste or type the text you want to analyze...\n\nFor best results, use at least 100 words.",
            minHeight = 240.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Stats bar
        LiveStatsBar(
            characterCount = characterCount,
            wordCount = wordCount
        )

        // Error message
        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            error?.let {
                Text(
                    text = it,
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Minimum word indicator
        AnimatedVisibility(
            visible = wordCount in 1..9,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "${10 - wordCount} more words needed",
                style = AppTypography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryButton(
                text = "Analyze Text",
                onClick = onAnalyze,
                enabled = canAnalyze,
                isLoading = isAnalyzing,
                modifier = Modifier.weight(1f)
            )

            if (text.isNotEmpty()) {
                SecondaryButton(
                    text = "Clear",
                    onClick = onClear
                )
            }
        }
    }
}

@Composable
private fun ResultsSection(
    result: AnalysisResult,
    canSave: Boolean,
    isSaving: Boolean,
    isSaved: Boolean,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.widthIn(max = 720.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Divider
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Score gauge
        ScoreGauge(
            score = result.overallScore,
            verdict = result.verdict
        )

        // Verdict card
        VerdictCard(
            verdict = result.verdict,
            summary = result.summary,
            confidence = result.confidence
        )

        // Save button
        SaveToHistoryButton(
            canSave = canSave,
            isSaving = isSaving,
            isSaved = isSaved,
            onSave = onSave
        )

        // Text Statistics
        SectionHeader(title = "Text Statistics")

        StatsGrid(
            items = listOf(
                "Characters" to result.textStats.characterCount.toString(),
                "Words" to result.textStats.wordCount.toString(),
                "Sentences" to result.textStats.sentenceCount.toString(),
                "Paragraphs" to result.textStats.paragraphCount.toString(),
                "Avg. Words/Sentence" to result.textStats.avgWordsPerSentence.format(1),
                "Vocabulary Diversity" to "${(result.textStats.uniqueWordRatio * 100).format(0)}%"
            )
        )

        // Detection Signals
        if (result.signals.isNotEmpty()) {
            SectionHeader(title = "Detection Signals")

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                result.signals
                    .sortedByDescending { it.score * it.weight }
                    .forEach { signal ->
                        SignalCard(signal = signal)
                    }
            }
        }

        // Pattern Details
        if (result.vocabularyAnalysis.detectedHedgingWords.isNotEmpty() ||
            result.vocabularyAnalysis.detectedTransitionPhrases.isNotEmpty() ||
            result.vocabularyAnalysis.detectedVagueAttributions.isNotEmpty()
        ) {
            SectionHeader(title = "Detected Patterns")

            PatternDetailsCard(result = result)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SaveToHistoryButton(
    canSave: Boolean,
    isSaving: Boolean,
    isSaved: Boolean,
    onSave: () -> Unit
) {
    val darkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val savedColor = if (darkTheme) AppColors.DarkHumanGreen else AppColors.HumanGreen
    
    Button(
        onClick = onSave,
        enabled = canSave && !isSaving && !isSaved,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSaved) savedColor else MaterialTheme.colorScheme.primary,
            disabledContainerColor = if (isSaved) savedColor.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Saving...")
        } else if (isSaved) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Saved to History", color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text("Save to History")
        }
    }
}

@Composable
private fun PatternDetailsCard(result: AnalysisResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (result.vocabularyAnalysis.detectedHedgingWords.isNotEmpty()) {
            PatternGroup(
                title = "Hedging Words",
                items = result.vocabularyAnalysis.detectedHedgingWords
            )
        }

        if (result.vocabularyAnalysis.detectedTransitionPhrases.isNotEmpty()) {
            PatternGroup(
                title = "Transition Phrases",
                items = result.vocabularyAnalysis.detectedTransitionPhrases
            )
        }

        if (result.vocabularyAnalysis.detectedVagueAttributions.isNotEmpty()) {
            PatternGroup(
                title = "Vague Attributions",
                items = result.vocabularyAnalysis.detectedVagueAttributions
            )
        }

        if (result.vocabularyAnalysis.detectedFillerPhrases.isNotEmpty()) {
            PatternGroup(
                title = "Filler Phrases",
                items = result.vocabularyAnalysis.detectedFillerPhrases
            )
        }
    }
}

@Composable
private fun PatternGroup(
    title: String,
    items: List<String>
) {
    Column {
        Text(
            text = title,
            style = AppTypography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = items.joinToString(" • "),
            style = AppTypography.mono,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}