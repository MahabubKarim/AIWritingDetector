package com.mmk.aiwritingdetector.di

import com.mmk.aiwritingdetector.data.db.AIWritingDetectorDatabase
import com.mmk.aiwritingdetector.data.db.DatabaseDriverFactory
import com.mmk.aiwritingdetector.data.repository.AnalysisHistoryRepository
import com.mmk.aiwritingdetector.domain.analyzer.TextAnalyzer
import com.mmk.aiwritingdetector.domain.usecase.AnalyzeTextUseCase
import com.mmk.aiwritingdetector.domain.usecase.GetHistoryUseCase
import com.mmk.aiwritingdetector.domain.usecase.SaveAnalysisUseCase
import com.mmk.aiwritingdetector.presentation.viewmodel.DetectorViewModel
import com.mmk.aiwritingdetector.presentation.viewmodel.HistoryViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Platform-specific module.
 */
expect val platformModule: Module

/**
 * Database module - provides database and driver.
 */
val databaseModule = module {
    single {
        val driver = get<DatabaseDriverFactory>().createDriver()
        AIWritingDetectorDatabase(driver)
    }

    singleOf(::AnalysisHistoryRepository)
}

/**
 * Domain layer module.
 */
val domainModule = module {
    singleOf(::TextAnalyzer)
    factoryOf(::AnalyzeTextUseCase)
    factoryOf(::SaveAnalysisUseCase)
    factoryOf(::GetHistoryUseCase)
}

/**
 * Presentation layer module.
 */
val presentationModule = module {
    viewModelOf(::DetectorViewModel)
    viewModelOf(::HistoryViewModel)
}

/**
 * All app modules combined.
 */
val appModules = listOf(
    platformModule,
    databaseModule,
    domainModule,
    presentationModule
)
