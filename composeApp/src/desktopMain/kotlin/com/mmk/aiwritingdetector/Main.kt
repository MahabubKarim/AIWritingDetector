package com.mmk.aiwritingdetector

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(1024.dp, 768.dp),
        position = WindowPosition(Alignment.Center)
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "AI Writing Detector"
    ) {
        App()
    }
}
