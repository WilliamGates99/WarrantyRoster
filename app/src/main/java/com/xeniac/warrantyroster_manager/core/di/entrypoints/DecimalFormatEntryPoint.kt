package com.xeniac.warrantyroster_manager.core.di.entrypoints

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.DecimalFormat

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DecimalFormatEntryPoint {
    val decimalFormat: DecimalFormat
}