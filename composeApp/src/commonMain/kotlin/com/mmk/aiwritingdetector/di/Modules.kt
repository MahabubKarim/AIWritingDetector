package com.mmk.aiwritingdetector.di

import com.mmk.aiwritingdetector.domain.analyzer.TextAnalyzer
import com.mmk.aiwritingdetector.domain.usecase.AnalyzeTextUseCase
import com.mmk.aiwritingdetector.presentation.viewmodel.DetectorViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Domain layer module.
 */
val domainModule = module {
    singleOf(::TextAnalyzer)
    factoryOf(::AnalyzeTextUseCase)
}

/**
 * Presentation layer module.
 */
val presentationModule = module {
    viewModelOf(::DetectorViewModel)
}

/**
 * All app modules combined.
 */
val appModules = listOf(
    domainModule,
    presentationModule
)
