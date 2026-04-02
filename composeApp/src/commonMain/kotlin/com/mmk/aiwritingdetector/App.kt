package com.mmk.aiwritingdetector

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.mmk.aiwritingdetector.di.appModules
import com.mmk.aiwritingdetector.presentation.screen.SplashScreen
import com.mmk.aiwritingdetector.presentation.theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration

/**
 * Main application entry point.
 *
 * Navigation Flow:
 * SplashScreen → (check auth) → LoginScreen or HomeScreen
 *                                    ↓
 *                              DetectorScreen (embedded)
 *                                    ↓
 *                              HistoryScreen
 */
@Composable
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {
    KoinApplication(
        application = {
            koinAppDeclaration?.invoke(this)
            modules(appModules)
        }
    ) {
        AppTheme {
            Navigator(
                screen = SplashScreen(),
                content = { navigator ->
                    SlideTransition(navigator = navigator)
                }
            )
        }
    }
}

