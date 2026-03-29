package com.mmk.aiwritingdetector.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mmk.aiwritingdetector.domain.model.DetectionSignal
import com.mmk.aiwritingdetector.domain.model.Verdict
import com.mmk.aiwritingdetector.presentation.theme.AppColors
import com.mmk.aiwritingdetector.presentation.theme.AppTypography
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Live stats bar that shows character/word count as user types.
 */
@Composable
fun LiveStatsBar(
    characterCount: Int,
    wordCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        StatChip(
            value = characterCount.toString(),
            label = "CHARACTERS"
        )
        StatChip(
            value = wordCount.toString(),
            label = "WORDS"
        )
    }
}

@Composable
private fun StatChip(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = value,
            style = AppTypography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = AppTypography.statLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Circular score gauge — the hero visual element.
 */
@Composable
fun ScoreGauge(
    score: Double,
    verdict: Verdict,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ),
        label = "score_animation"
    )

    val gaugeColor = when {
        score < 0.35 -> AppColors.HumanGreen
        score < 0.65 -> AppColors.NeutralBlue
        else -> AppColors.AIAmber
    }

    val trackColor = MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Track and progress arc
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val radius = (size.toPx() - strokeWidth) / 2
            val center = Offset(size.toPx() / 2, size.toPx() / 2)

            // Background track
            drawArc(
                color = trackColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = androidx.compose.ui.geometry.Size(
                    size.toPx() - strokeWidth,
                    size.toPx() - strokeWidth
                )
            )

            // Progress arc
            drawArc(
                color = gaugeColor,
                startAngle = 135f,
                sweepAngle = 270f * animatedScore,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = androidx.compose.ui.geometry.Size(
                    size.toPx() - strokeWidth,
                    size.toPx() - strokeWidth
                )
            )
        }

        // Center content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${(score * 100).toInt()}%",
                style = AppTypography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = verdict.label.uppercase(),
                style = AppTypography.labelMedium,
                color = gaugeColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Verdict card with summary.
 */
@Composable
fun VerdictCard(
    verdict: Verdict,
    summary: String,
    confidence: Double,
    modifier: Modifier = Modifier
) {
    val (bgColor, borderColor) = when (verdict) {
        Verdict.LIKELY_HUMAN -> AppColors.HumanGreenBg to AppColors.HumanGreen
        Verdict.POSSIBLY_HUMAN -> AppColors.HumanGreenBg.copy(alpha = 0.3f) to AppColors.HumanGreen.copy(alpha = 0.6f)
        Verdict.INCONCLUSIVE -> AppColors.NeutralBlueBg to AppColors.NeutralBlue
        Verdict.POSSIBLY_AI -> AppColors.AIAmberBg.copy(alpha = 0.6f) to AppColors.AIAmber.copy(alpha = 0.6f)
        Verdict.LIKELY_AI -> AppColors.AIAmberBg to AppColors.AIAmber
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(borderColor)
            )
            Text(
                text = verdict.label,
                style = AppTypography.headlineMedium,
                color = borderColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = summary,
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            lineHeight = AppTypography.bodyMedium.lineHeight
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Confidence:",
                style = AppTypography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LinearProgressIndicator(
                progress = { confidence.toFloat() },
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = borderColor,
                trackColor = borderColor.copy(alpha = 0.2f)
            )
            Text(
                text = "${(confidence * 100).toInt()}%",
                style = AppTypography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Signal indicator card.
 */
@Composable
fun SignalCard(
    signal: DetectionSignal,
    modifier: Modifier = Modifier
) {
    val signalColor = when {
        signal.score < 0.35 -> AppColors.HumanGreen
        signal.score < 0.65 -> AppColors.NeutralBlue
        else -> AppColors.AIAmber
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = signal.name,
                style = AppTypography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            SignalBadge(score = signal.score, color = signalColor)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = signal.description,
            style = AppTypography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (signal.evidence.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                signal.evidence.forEach { evidence ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = " • ",
                            style = AppTypography.bodySmall,
                            color = signalColor
                        )
                        Text(
                            text = evidence,
                            style = AppTypography.mono,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SignalBadge(
    score: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    val label = when {
        score < 0.35 -> "Human"
        score < 0.65 -> "Mixed"
        else -> "AI"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Statistics grid for detailed metrics.
 */
@Composable
fun StatsGrid(
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                row.forEach { (label, value) ->
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = value,
                            style = AppTypography.statNumber,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = label.uppercase(),
                            style = AppTypography.statLabel,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Fill empty space if odd number
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Minimal text area with elegant styling.
 */
@Composable
fun ElegantTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    minHeight: Dp = 200.dp
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight),
        placeholder = {
            Text(
                text = placeholder,
                style = AppTypography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        textStyle = AppTypography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Primary action button with elegant styling.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.outlineVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = PaddingValues(horizontal = 32.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = AppTypography.labelLarge
            )
        }
    }
}

/**
 * Secondary/ghost button.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Text(
            text = text,
            style = AppTypography.labelLarge
        )
    }
}

/**
 * Section header with minimal styling.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = AppTypography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

/**
 * Animated analyzing indicator.
 */
@Composable
fun AnalyzingIndicator(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "analyzing")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .drawBehind {
                    val strokeWidth = 3.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2

                    for (i in 0..2) {
                        val angle = rotation + (i * 120f)
                        val alpha = 1f - (i * 0.3f)
                        val startAngle = angle - 30f
                        drawArc(
                            color = AppColors.Teal.copy(alpha = alpha),
                            startAngle = startAngle,
                            sweepAngle = 60f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }
        )

        Text(
            text = "Analyzing text patterns...",
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
