package com.mmk.aiwritingdetector.presentation.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mmk.aiwritingdetector.domain.auth.User
import com.mmk.aiwritingdetector.domain.util.format
import com.mmk.aiwritingdetector.presentation.theme.AppColors
import com.mmk.aiwritingdetector.presentation.viewmodel.AuthEffect
import com.mmk.aiwritingdetector.presentation.viewmodel.AuthIntent
import com.mmk.aiwritingdetector.presentation.viewmodel.AuthViewModel
import com.mmk.aiwritingdetector.presentation.viewmodel.DetectorIntent
import com.mmk.aiwritingdetector.presentation.viewmodel.DetectorViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Home Screen - Main screen after authentication.
 * Shows user info, navigation options, and the detector functionality.
 */
class HomeScreen : Screen {
    
    @Composable
    override fun Content() {
        val authViewModel: AuthViewModel = koinViewModel()
        val detectorViewModel: DetectorViewModel = koinViewModel()
        val authState by authViewModel.uiState.collectAsState()
        val detectorState by detectorViewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        
        var showProfileMenu by remember { mutableStateOf(false) }
        var showSignOutDialog by remember { mutableStateOf(false) }
        
        // Handle auth effects
        LaunchedEffect(Unit) {
            authViewModel.effects.collectLatest { effect ->
                when (effect) {
                    is AuthEffect.NavigateToLogin -> {
                        navigator.replaceAll(LoginScreen())
                    }
                    else -> {}
                }
            }
        }
        
        Scaffold(
            topBar = {
                HomeTopBar(
                    user = authState.user,
                    onProfileClick = { showProfileMenu = true },
                    onHistoryClick = { navigator.push(HistoryScreen()) }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(AppColors.Ivory)
            ) {
                // Main content - reuse DetectorScreen content
                DetectorScreenContent(
                    viewModel = detectorViewModel,
                    state = detectorState,
                    onNavigateToHistory = { navigator.push(HistoryScreen()) }
                )
                
                // Profile dropdown menu
                DropdownMenu(
                    expanded = showProfileMenu,
                    onDismissRequest = { showProfileMenu = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    authState.user?.let { user ->
                        // User info header
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .widthIn(min = 200.dp)
                        ) {
                            Text(
                                text = user.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Charcoal
                            )
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.Stone
                            )
                        }
                        
                        HorizontalDivider()
                        
                        // Token info
                        DropdownMenuItem(
                            text = { 
                                Column {
                                    Text("PASETO Token Active", color = AppColors.HumanGreen)
                                    Text(
                                        "Secure authentication",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppColors.Stone
                                    )
                                }
                            },
                            onClick = { },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = null,
                                    tint = AppColors.HumanGreen
                                )
                            }
                        )
                        
                        HorizontalDivider()
                        
                        // Sign out
                        DropdownMenuItem(
                            text = { Text("Sign Out", color = AppColors.Coral) },
                            onClick = {
                                showProfileMenu = false
                                showSignOutDialog = true
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = null,
                                    tint = AppColors.Coral
                                )
                            }
                        )
                    }
                }
            }
        }
        
        // Sign out confirmation dialog
        if (showSignOutDialog) {
            AlertDialog(
                onDismissRequest = { showSignOutDialog = false },
                title = { Text("Sign Out") },
                text = { Text("Are you sure you want to sign out?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSignOutDialog = false
                            authViewModel.onIntent(AuthIntent.SignOut)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = AppColors.Coral
                        )
                    ) {
                        Text("Sign Out")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSignOutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    user: User?,
    onProfileClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // App icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AppColors.Teal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AI",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Column {
                    Text(
                        text = "AI Writing Detector",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Charcoal
                    )
                    user?.let {
                        Text(
                            text = "Welcome, ${it.displayName.split(" ").first()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.Stone
                        )
                    }
                }
            }
        },
        actions = {
            // History button
            IconButton(onClick = onHistoryClick) {
                Icon(
                    Icons.Default.History,
                    contentDescription = "History",
                    tint = AppColors.Charcoal
                )
            }
            
            // Profile button
            IconButton(onClick = onProfileClick) {
                user?.let {
                    // User avatar
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AppColors.Teal.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it.displayName.first().uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Teal
                        )
                    }
                } ?: Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = AppColors.Charcoal
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppColors.Cream
        )
    )
}

@Composable
private fun DetectorScreenContent(
    viewModel: DetectorViewModel,
    state: com.mmk.aiwritingdetector.presentation.viewmodel.DetectorState,
    onNavigateToHistory: () -> Unit
) {
    // Import and reuse the detector content
    // This avoids code duplication while maintaining the flow
    
    val scrollState = androidx.compose.foundation.rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    
    // Handle scroll to results effect
    LaunchedEffect(state.showResults) {
        if (state.showResults && state.analysisResult != null) {
            launch {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
    }
    
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Ivory)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Simplified header (main header is in TopBar now)
        Text(
            text = "Paste or type text below to analyze",
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.Stone
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Input section
        InputSection(
            text = state.inputText,
            onTextChange = { viewModel.onIntent(DetectorIntent.UpdateText(it)) },
            characterCount = state.characterCount,
            wordCount = state.wordCount,
            canAnalyze = state.canAnalyze,
            isAnalyzing = state.isAnalyzing,
            onAnalyze = { viewModel.onIntent(DetectorIntent.Analyze) },
            onClear = { viewModel.onIntent(DetectorIntent.ClearAll) },
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
                com.mmk.aiwritingdetector.presentation.components.AnalyzingIndicator()
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
                        onSave = { viewModel.onIntent(DetectorIntent.SaveToHistory) }
                    )
                }
            }
        }
    }
}

// Reuse InputSection from DetectorScreen
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
        com.mmk.aiwritingdetector.presentation.components.ElegantTextArea(
            value = text,
            onValueChange = onTextChange,
            placeholder = "Paste or type the text you want to analyze...\n\nFor best results, use at least 100 words.",
            minHeight = 200.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        com.mmk.aiwritingdetector.presentation.components.LiveStatsBar(
            characterCount = characterCount,
            wordCount = wordCount
        )

        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            error?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = wordCount in 1..9,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "${10 - wordCount} more words needed",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.Stone,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            com.mmk.aiwritingdetector.presentation.components.PrimaryButton(
                text = "Analyze Text",
                onClick = onAnalyze,
                enabled = canAnalyze,
                isLoading = isAnalyzing,
                modifier = Modifier.weight(1f)
            )

            if (text.isNotEmpty()) {
                com.mmk.aiwritingdetector.presentation.components.SecondaryButton(
                    text = "Clear",
                    onClick = onClear
                )
            }
        }
    }
}

// Reuse ResultsSection from DetectorScreen
@Composable
private fun ResultsSection(
    result: com.mmk.aiwritingdetector.domain.model.AnalysisResult,
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
        HorizontalDivider(color = AppColors.Stone.copy(alpha = 0.3f))

        com.mmk.aiwritingdetector.presentation.components.ScoreGauge(
            score = result.overallScore,
            verdict = result.verdict
        )

        com.mmk.aiwritingdetector.presentation.components.VerdictCard(
            verdict = result.verdict,
            summary = result.summary,
            confidence = result.confidence
        )

        // Save button
        Button(
            onClick = onSave,
            enabled = canSave && !isSaving && !isSaved,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSaved) AppColors.HumanGreen else AppColors.Teal,
                disabledContainerColor = if (isSaved) AppColors.HumanGreen.copy(alpha = 0.8f) else AppColors.Stone.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Saving...")
            } else if (isSaved) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Saved to History", color = Color.White)
            } else {
                Text("Save to History")
            }
        }

        com.mmk.aiwritingdetector.presentation.components.SectionHeader(title = "Text Statistics")

        com.mmk.aiwritingdetector.presentation.components.StatsGrid(
            items = listOf(
                "Characters" to result.textStats.characterCount.toString(),
                "Words" to result.textStats.wordCount.toString(),
                "Sentences" to result.textStats.sentenceCount.toString(),
                "Paragraphs" to result.textStats.paragraphCount.toString(),
                "Avg. Words/Sentence" to result.textStats.avgWordsPerSentence.format(1),
                "Vocabulary Diversity" to "${(result.textStats.uniqueWordRatio * 100).format(0)}%"
            )
        )

        if (result.signals.isNotEmpty()) {
            com.mmk.aiwritingdetector.presentation.components.SectionHeader(title = "Detection Signals")

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                result.signals
                    .sortedByDescending { it.score * it.weight }
                    .forEach { signal ->
                        com.mmk.aiwritingdetector.presentation.components.SignalCard(signal = signal)
                    }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
