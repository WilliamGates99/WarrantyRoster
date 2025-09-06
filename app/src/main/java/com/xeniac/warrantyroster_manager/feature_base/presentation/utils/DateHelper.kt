package com.xeniac.warrantyroster_manager.feature_base.presentation.utils

import com.xeniac.warrantyroster_manager.core.domain.models.PreviousRateAppRequestDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import kotlinx.datetime.parse
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object DateHelper {

    fun getDaysFromFirstInstallTime(
        firstInstallTimeInMs: Long
    ): Int = Instant.fromEpochMilliseconds(epochMilliseconds = firstInstallTimeInMs).daysUntil(
        other = Clock.System.now(),
        timeZone = TimeZone.currentSystemDefault()
    )

    fun getDaysFromPreviousRequestTime(
        previousRequestTime: PreviousRateAppRequestDateTime,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
        dateTimeFormat: DateTimeFormat<DateTimeComponents> = DateTimeComponents.Format {
            /**
             * DateTime Format: yyyy-mm-ddThh:mm:ss.000000Z
             * Sample: 2024-09-24T18:51:35.000000Z
             */
            dateTime(format = LocalDateTime.Formats.ISO)
            char(value = 'Z') // 'Z' is the zone designator for the zero UTC offset
        }
    ): Int = Instant.parse(
        input = previousRequestTime,
        format = dateTimeFormat
    ).daysUntil(
        other = Clock.System.now(),
        timeZone = timeZone
    )
}