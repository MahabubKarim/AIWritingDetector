package com.mmk.aiwritingdetector.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = AIWritingDetectorDatabase.Schema,
            name = DATABASE_NAME
        )
    }
}
