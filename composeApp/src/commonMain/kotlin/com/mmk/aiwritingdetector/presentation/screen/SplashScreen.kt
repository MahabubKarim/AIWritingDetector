package com.mmk.aiwritingdetector.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mmk.aiwritingdetector.data.auth.AuthRepository
import com.mmk.aiwritingdetector.presentation.theme.AppColors
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

/**
 * Animated Splash Screen.
 * 
 * Flow:
 * 1. Show animated logo and app name
 * 2. Check authentication status
 * 3. Navigate to Login or Home screen
 */
class SplashScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authRepository: AuthRepository = koinInject()
        
        // Animation states
        var startAnimation by remember { mutableStateOf(false) }
        val alphaAnim by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(durationMillis = 1000),
            label = "alpha"
        )
        val scaleAnim by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0.5f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "scale"
        )
        
        // Start animation and navigate
        LaunchedEffect(Unit) {
            startAnimation = true
            delay(2500) // Show splash for 2.5 seconds
            
            val isLoggedIn = authRepository.isLoggedIn()
            if (isLoggedIn) {
                navigator.replace(HomeScreen())
            } else {
                navigator.replace(LoginScreen())
            }
        }
        
        SplashContent(
            alpha = alphaAnim,
            scale = scaleAnim
        )
    }
}

@Composable
private fun SplashContent(
    alpha: Float,
    scale: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alpha)
                .scale(scale)
        ) {
            // Animated Logo
            AnimatedLogo()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App Name
            Text(
                text = "AI Writing",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Detector",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tagline
            Text(
                text = "Powered by PASETO Authentication",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Loading indicator at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            PulsingDots(alpha = alpha)
        }
    }
}

@Composable
private fun AnimatedLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "logo")
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            
            // Outer rotating ring
            rotate(rotation) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.3f),
                            primaryColor
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            
            // Inner circle
            drawCircle(
                color = surfaceVariant,
                radius = radius - strokeWidth - 8.dp.toPx()
            )
            
            // Center icon (magnifying glass shape)
            val centerX = size.width / 2
            val centerY = size.height / 2
            val iconRadius = radius * 0.35f
            
            drawCircle(
                color = primaryColor,
                radius = iconRadius,
                center = Offset(centerX - iconRadius * 0.2f, centerY - iconRadius * 0.2f),
                style = Stroke(width = 4.dp.toPx())
            )
            
            // Handle of magnifying glass
            drawLine(
                color = primaryColor,
                start = Offset(centerX + iconRadius * 0.4f, centerY + iconRadius * 0.4f),
                end = Offset(centerX + iconRadius * 1.2f, centerY + iconRadius * 1.2f),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        
        // AI text in center
        Text(
            text = "AI",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = primaryColor
        )
    }
}

@Composable
private fun PulsingDots(alpha: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.alpha(alpha)
    ) {
        repeat(3) { index ->
            val delay = index * 200
            
            val dotAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = delay),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )
            
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = delay),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_scale_$index"
            )
            
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .scale(scale)
                    .alpha(dotAlpha)
                    .background(primaryColor, shape = androidx.compose.foundation.shape.CircleShape)
            )
        }
    }
}
