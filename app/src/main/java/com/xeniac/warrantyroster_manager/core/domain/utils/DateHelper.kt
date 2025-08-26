package com.xeniac.warrantyroster_manager.core.domain.utils

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

typealias EpochMilliseconds = Long
typealias EpochSeconds = Long

@OptIn(ExperimentalTime::class)
object DateHelper {

    fun getCurrentTimeInMillis(): EpochMilliseconds = Clock.System.now().toEpochMilliseconds()

    fun getCurrentTimeInSeconds(): EpochSeconds = Clock.System.now().epochSeconds
}