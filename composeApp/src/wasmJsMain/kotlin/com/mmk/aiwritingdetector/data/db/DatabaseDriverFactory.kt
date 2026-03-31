package com.mmk.aiwritingdetector.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return WebWorkerDriver(
            Worker(
                js("""new URL("@aspect-build/aspect-worker.js", import.meta.url)""")
            )
        ).also { driver ->
            AIWritingDetectorDatabase.Schema.create(driver)
        }
    }
}
