package com.mmk.aiwritingdetector.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Color Palette — Editorial/Magazine inspired
 * Warm neutrals with a refined teal accent
 */
object AppColors {
    // Light Theme
    val Ivory = Color(0xFFFAF8F5)
    val Cream = Color(0xFFF5F2ED)
    val WarmGray = Color(0xFFE8E4DE)
    val Stone = Color(0xFFB5AEA4)
    val Graphite = Color(0xFF5A5651)
    val Charcoal = Color(0xFF2D2A26)
    val Ink = Color(0xFF1A1816)

    // Accent Colors
    val Teal = Color(0xFF2A9D8F)
    val TealDark = Color(0xFF1F7A6E)
    val TealLight = Color(0xFF52B5A9)
    val Coral = Color(0xFFE07A5F)
    val CoralLight = Color(0xFFE89B85)

    // Semantic Colors
    val HumanGreen = Color(0xFF4A9F6E)
    val HumanGreenBg = Color(0xFFE8F5ED)
    val AIAmber = Color(0xFFD4A03A)
    val AIAmberBg = Color(0xFFFDF6E3)
    val NeutralBlue = Color(0xFF6B8CAE)
    val NeutralBlueBg = Color(0xFFEDF2F7)
    val ErrorRed = Color(0xFFBF4545)
    val ErrorRedBg = Color(0xFFFDEDED)

    // Dark Theme
    val DarkBg = Color(0xFF1A1816)
    val DarkSurface = Color(0xFF252220)
    val DarkSurfaceVariant = Color(0xFF302D2A)
    val DarkOnSurface = Color(0xFFE8E4DE)
    val DarkOnSurfaceVariant = Color(0xFFB5AEA4)

    // Dark Semantic Colors
    val DarkHumanGreen = Color(0xFF66BB6A)
    val DarkHumanGreenBg = Color(0xFF1B2E1E)
    val DarkAIAmber = Color(0xFFFFB74D)
    val DarkAIAmberBg = Color(0xFF2E241B)
    val DarkNeutralBlue = Color(0xFF64B5F6)
    val DarkNeutralBlueBg = Color(0xFF1B252E)
}

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Teal,
    onPrimary = Color.White,
    primaryContainer = AppColors.TealLight.copy(alpha = 0.15f),
    onPrimaryContainer = AppColors.TealDark,

    secondary = AppColors.Coral,
    onSecondary = Color.White,
    secondaryContainer = AppColors.CoralLight.copy(alpha = 0.15f),
    onSecondaryContainer = AppColors.Coral,

    surface = AppColors.Ivory,
    onSurface = AppColors.Ink,
    surfaceVariant = AppColors.Cream,
    onSurfaceVariant = AppColors.Graphite,

    background = AppColors.Ivory,
    onBackground = AppColors.Ink,

    outline = AppColors.Stone,
    outlineVariant = AppColors.WarmGray,

    error = AppColors.ErrorRed,
    onError = Color.White,
    errorContainer = AppColors.ErrorRedBg,
    onErrorContainer = AppColors.ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.TealLight,
    onPrimary = AppColors.DarkBg,
    primaryContainer = AppColors.Teal.copy(alpha = 0.2f),
    onPrimaryContainer = AppColors.TealLight,

    secondary = AppColors.CoralLight,
    onSecondary = AppColors.DarkBg,
    secondaryContainer = AppColors.Coral.copy(alpha = 0.2f),
    onSecondaryContainer = AppColors.CoralLight,

    surface = AppColors.DarkSurface,
    onSurface = AppColors.DarkOnSurface,
    surfaceVariant = AppColors.DarkSurfaceVariant,
    onSurfaceVariant = AppColors.DarkOnSurfaceVariant,

    background = AppColors.DarkBg,
    onBackground = AppColors.DarkOnSurface,

    outline = AppColors.Graphite,
    outlineVariant = AppColors.DarkSurfaceVariant,

    error = AppColors.Coral,
    onError = AppColors.DarkBg,
    errorContainer = AppColors.ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = AppColors.CoralLight
)

/**
 * Typography — Editorial style with confident hierarchy
 */
object AppTypography {
    // Using system fonts for KMP compatibility,
    // but structured for distinctive hierarchy

    val displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,
        lineHeight = 48.sp,
        letterSpacing = (-1.5).sp
    )

    val displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.5).sp
    )

    val displaySmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )

    val headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )

    val headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )

    val headlineSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    )

    val bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.15.sp
    )

    val bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    )

    val bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    )

    val labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    )

    val labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

    val labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )

    // Special styles
    val mono = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )

    val statNumber = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.5).sp
    )

    val statLabel = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.sp
    )
}

private val MaterialTypography = Typography(
    displayLarge = AppTypography.displayLarge,
    displayMedium = AppTypography.displayMedium,
    displaySmall = AppTypography.displaySmall,
    headlineLarge = AppTypography.headlineLarge,
    headlineMedium = AppTypography.headlineMedium,
    headlineSmall = AppTypography.headlineSmall,
    bodyLarge = AppTypography.bodyLarge,
    bodyMedium = AppTypography.bodyMedium,
    bodySmall = AppTypography.bodySmall,
    labelLarge = AppTypography.labelLarge,
    labelMedium = AppTypography.labelMedium,
    labelSmall = AppTypography.labelSmall
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTypography,
        content = content
    )
}
