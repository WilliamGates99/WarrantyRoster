package com.xeniac.warrantyroster_manager.core.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.AppUpdateDialogShowCount
import com.xeniac.warrantyroster_manager.core.domain.models.PreviousRateAppRequestDateTime
import com.xeniac.warrantyroster_manager.core.domain.models.RateAppOption
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char

typealias IsAppUpdateDialogShownToday = Boolean

interface MiscellaneousDataStoreRepository {

    fun getAppUpdateDialogShowCount(): Flow<AppUpdateDialogShowCount>

    suspend fun storeAppUpdateDialogShowCount(count: Int)

    fun isAppUpdateDialogShownToday(
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
        dateTimeFormat: DateTimeFormat<DateTimeComponents> = DateTimeComponents.Format {
            /**
             * DateTime Format: yyyy-mm-ddThh:mm:ss.000000Z
             * Sample: 2024-09-24T18:51:35.000000Z
             */
            dateTime(format = LocalDateTime.Formats.ISO)
            char(value = 'Z') // 'Z' is the zone designator for the zero UTC offset
        }
    ): Flow<IsAppUpdateDialogShownToday>

    suspend fun storeAppUpdateDialogShowDateTime(
        dateTimeFormat: DateTimeFormat<DateTimeComponents> = DateTimeComponents.Format {
            /**
             * DateTime Format: yyyy-mm-ddThh:mm:ss.000000Z
             * Sample: 2024-09-24T18:51:35.000000Z
             */
            dateTime(format = LocalDateTime.Formats.ISO)
            char(value = 'Z') // 'Z' is the zone designator for the zero UTC offset
        }
    )

    suspend fun removeAppUpdateDialogShowDateTime()

    fun getSelectedRateAppOption(): Flow<RateAppOption>

    suspend fun storeSelectedRateAppOption(rateAppOption: RateAppOption)

    fun getPreviousRateAppRequestDateTime(): Flow<PreviousRateAppRequestDateTime?>

    suspend fun storePreviousRateAppRequestDateTime(
        dateTimeFormat: DateTimeFormat<DateTimeComponents> = DateTimeComponents.Format {
            /**
             * DateTime Format: yyyy-mm-ddThh:mm:ss.000000Z
             * Sample: 2024-09-24T18:51:35.000000Z
             */
            dateTime(format = LocalDateTime.Formats.ISO)
            char(value = 'Z') // 'Z' is the zone designator for the zero UTC offset
        }
    )
}