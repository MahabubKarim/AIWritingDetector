package com.mmk.aiwritingdetector.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver

/**
 * Platform-specific SQLDelight driver factory for Wasm/JS.
 */
actual class DatabaseDriverFactory {
    /**
     * Creates a SqlDriver using a Web Worker.
     */
    actual fun createDriver(): SqlDriver {
        return createDefaultWebWorkerDriver()
    }
}
