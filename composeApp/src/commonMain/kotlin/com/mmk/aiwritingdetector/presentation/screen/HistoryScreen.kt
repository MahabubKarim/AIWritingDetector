package com.mmk.aiwritingdetector.presentation.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
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
import com.mmk.aiwritingdetector.domain.model.AnalysisHistory
import com.mmk.aiwritingdetector.domain.model.Verdict
import com.mmk.aiwritingdetector.domain.util.format
import com.mmk.aiwritingdetector.presentation.components.*
import com.mmk.aiwritingdetector.presentation.theme.AppColors
import com.mmk.aiwritingdetector.presentation.viewmodel.HistoryEffect
import com.mmk.aiwritingdetector.presentation.viewmodel.HistoryIntent
import com.mmk.aiwritingdetector.presentation.viewmodel.HistoryViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * History Screen - displays list of previously analyzed texts.
 */
class HistoryScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: HistoryViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        // Handle effects
        LaunchedEffect(Unit) {
            viewModel.effects.collectLatest { effect ->
                when (effect) {
                    is HistoryEffect.ShowError -> {
                        // Could show snackbar
                    }
                    is HistoryEffect.ItemDeleted -> {
                        // Item deleted successfully
                    }
                    is HistoryEffect.AllCleared -> {
                        // All cleared successfully
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Ivory)
        ) {
            // Header
            HistoryHeader(
                itemCount = state.historyList.size,
                onBack = { navigator.pop() },
                onClearAll = { viewModel.processIntent(HistoryIntent.RequestClearAll) }
            )

            // Search bar
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.processIntent(HistoryIntent.Search(it)) }
            )

            // Content
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.Teal)
                    }
                }
                state.historyList.isEmpty() -> {
                    EmptyHistoryState()
                }
                else -> {
                    // Two-pane layout for selected item detail
                    if (state.selectedItem != null) {
                        HistoryDetailView(
                            item = state.selectedItem!!,
                            onBack = { viewModel.processIntent(HistoryIntent.ClearSelection) },
                            onDelete = { viewModel.processIntent(HistoryIntent.RequestDelete(state.selectedItem!!)) }
                        )
                    } else {
                        HistoryList(
                            items = state.historyList,
                            onItemClick = { viewModel.processIntent(HistoryIntent.SelectItem(it)) },
                            onItemDelete = { viewModel.processIntent(HistoryIntent.RequestDelete(it)) }
                        )
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (state.showDeleteConfirmation) {
            DeleteConfirmationDialog(
                onConfirm = { viewModel.processIntent(HistoryIntent.ConfirmDelete) },
                onDismiss = { viewModel.processIntent(HistoryIntent.CancelDelete) }
            )
        }

        // Clear all confirmation dialog
        if (state.showClearAllConfirmation) {
            ClearAllConfirmationDialog(
                onConfirm = { viewModel.processIntent(HistoryIntent.ConfirmClearAll) },
                onDismiss = { viewModel.processIntent(HistoryIntent.CancelClearAll) }
            )
        }
    }
}

@Composable
private fun HistoryHeader(
    itemCount: Int,
    onBack: () -> Unit,
    onClearAll: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppColors.Cream,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.Charcoal
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = "Analysis History",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Charcoal
                    )
                    Text(
                        text = "$itemCount saved ${if (itemCount == 1) "analysis" else "analyses"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.Stone
                    )
                }
            }

            if (itemCount > 0) {
                TextButton(
                    onClick = onClearAll,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = AppColors.Coral
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All")
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Search history...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = AppColors.Stone
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = AppColors.Stone
                    )
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.Teal,
            unfocusedBorderColor = AppColors.Stone.copy(alpha = 0.3f),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
private fun HistoryList(
    items: List<AnalysisHistory>,
    onItemClick: (AnalysisHistory) -> Unit,
    onItemDelete: (AnalysisHistory) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item ->
            HistoryItemCard(
                item = item,
                onClick = { onItemClick(item) },
                onDelete = { onItemDelete(item) }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun HistoryItemCard(
    item: AnalysisHistory,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Score indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(item.verdict.getColor().copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${item.getScorePercentage()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = item.verdict.getColor()
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.verdict.getShortLabel(),
                    style = MaterialTheme.typography.labelSmall,
                    color = item.verdict.getColor()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.textPreview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.Charcoal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatChip(label = "${item.wordCount} words")
                    StatChip(label = "${item.sentenceCount} sentences")
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.getFormattedDate(),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.Stone
                )
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = AppColors.Stone,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun StatChip(label: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = AppColors.Stone.copy(alpha = 0.1f)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = AppColors.Stone,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun HistoryDetailView(
    item: AnalysisHistory,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Ivory)
    ) {
        // Detail header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AppColors.Cream
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.Charcoal
                    )
                }
                
                Text(
                    text = "Analysis Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Charcoal
                )
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = AppColors.Coral
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Score card
            item {
                VerdictCard(
                    verdict = item.verdict,
                    summary = item.summary,
                    confidence = item.confidence
                )
            }

            // Original text
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Original Text",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Charcoal
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.fullText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.Charcoal.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Detection signals
            item {
                SectionHeader(title = "Detection Signals")
            }

            items(item.signals) { signal ->
                SignalCard(signal = signal)
            }

            // Stats
            item {
                val avgWordsPerSentence = if (item.sentenceCount > 0) 
                    item.wordCount.toDouble() / item.sentenceCount 
                else 0.0
                    
                StatsGrid(
                    items = listOf(
                        "Words" to item.wordCount.toString(),
                        "Sentences" to item.sentenceCount.toString(),
                        "Avg. Words/Sentence" to avgWordsPerSentence.format(1),
                        "AI Score" to "${item.getScorePercentage()}%"
                    )
                )
            }

            // Date
            item {
                Text(
                    text = "Analyzed on ${item.getFormattedDate()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppColors.Stone,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun EmptyHistoryState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📝",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No analyses yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AppColors.Charcoal
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Analyzed texts will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.Stone
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Analysis?") },
        text = { Text("This action cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = AppColors.Coral)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ClearAllConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clear All History?") },
        text = { Text("This will delete all saved analyses. This action cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = AppColors.Coral)
            ) {
                Text("Clear All")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Extension functions
private fun Verdict.getColor(): Color = when (this) {
    Verdict.LIKELY_HUMAN -> AppColors.HumanGreen
    Verdict.POSSIBLY_HUMAN -> AppColors.HumanGreen.copy(alpha = 0.7f)
    Verdict.INCONCLUSIVE -> AppColors.NeutralBlue
    Verdict.POSSIBLY_AI -> AppColors.AIAmber
    Verdict.LIKELY_AI -> AppColors.Coral
}

private fun Verdict.getShortLabel(): String = when (this) {
    Verdict.LIKELY_HUMAN -> "Human"
    Verdict.POSSIBLY_HUMAN -> "Likely Human"
    Verdict.INCONCLUSIVE -> "Mixed"
    Verdict.POSSIBLY_AI -> "Likely AI"
    Verdict.LIKELY_AI -> "AI"
}
