package com.mmk.aiwritingdetector.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databasePath = getDatabasePath()
        val driver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")
        
        // Create tables if database is new
        if (!File(databasePath).exists()) {
            AIWritingDetectorDatabase.Schema.create(driver)
        }
        
        return driver
    }
    
    private fun getDatabasePath(): String {
        val appDir = File(System.getProperty("user.home"), ".ai-writing-detector")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return File(appDir, DATABASE_NAME).absolutePath
    }
}
