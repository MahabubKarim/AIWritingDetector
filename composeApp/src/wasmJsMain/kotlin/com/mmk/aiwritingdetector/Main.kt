package com.mmk.aiwritingdetector

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Initialize Koin
/*    startKoin {
        modules(webPlatformModule)
    }*/

    ComposeViewport(document.body!!) {
        App()
    }
}
