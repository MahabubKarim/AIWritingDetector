package com.mmk.aiwritingdetector.data.db

import app.cash.sqldelight.db.SqlDriver

/**
 * Factory for creating platform-specific SQLDelight drivers.
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

/**
 * Database name constant.
 */
const val DATABASE_NAME = "ai_writing_detector.db"
