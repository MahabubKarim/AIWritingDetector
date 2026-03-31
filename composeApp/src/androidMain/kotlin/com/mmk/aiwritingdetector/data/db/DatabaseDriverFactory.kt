package com.mmk.aiwritingdetector.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = AIWritingDetectorDatabase.Schema,
            context = context,
            name = DATABASE_NAME
        )
    }
}
