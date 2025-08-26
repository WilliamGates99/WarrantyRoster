package com.xeniac.warrantyroster_manager.core.data.repositories

import androidx.datastore.core.DataStore
import com.xeniac.warrantyroster_manager.core.domain.models.AppUpdateDialogShowCount
import com.xeniac.warrantyroster_manager.core.domain.models.MiscellaneousPreferences
import com.xeniac.warrantyroster_manager.core.domain.models.PreviousRateAppRequestDateTime
import com.xeniac.warrantyroster_manager.core.domain.models.RateAppOption
import com.xeniac.warrantyroster_manager.core.domain.repositories.IsAppUpdateDialogShownToday
import com.xeniac.warrantyroster_manager.core.domain.repositories.MiscellaneousDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.parse
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class MiscellaneousDataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<MiscellaneousPreferences>
) : MiscellaneousDataStoreRepository {

    override fun getAppUpdateDialogShowCount(
    ): Flow<AppUpdateDialogShowCount> = dataStore.data.map {
        it.appUpdateDialogShowCount
    }.catch { e ->
        Timber.e("Get app update dialog show count failed:")
        e.printStackTrace()
    }

    override suspend fun storeAppUpdateDialogShowCount(count: Int) {
        try {
            dataStore.updateData { it.copy(appUpdateDialogShowCount = count) }
            Timber.i("App update dialog show count edited to $count")
        } catch (e: Exception) {
            Timber.e("Store app update dialog show count failed:")
            e.printStackTrace()
        }
    }

    override fun isAppUpdateDialogShownToday(
        timeZone: TimeZone,
        dateTimeFormat: DateTimeFormat<DateTimeComponents>
    ): Flow<IsAppUpdateDialogShownToday> = dataStore.data.map { preferences ->
        preferences.appUpdateDialogShowDateTime?.let { dialogShowDateTime ->
            val today = Clock.System.now().toLocalDateTime(
                timeZone = timeZone
            ).date

            val dialogShowLocaleDate = Instant.parse(
                input = dialogShowDateTime,
                format = dateTimeFormat
            ).toLocalDateTime(timeZone = timeZone).date

            val isShownToday = today == dialogShowLocaleDate

            isShownToday
        } ?: false
    }.catch { e ->
        Timber.e("Get app update dialog show date failed:")
        e.printStackTrace()
    }

    override suspend fun storeAppUpdateDialogShowDateTime(
        dateTimeFormat: DateTimeFormat<DateTimeComponents>
    ) {
        try {
            val now = Clock.System.now()
            dataStore.updateData {
                it.copy(appUpdateDialogShowDateTime = now.format(format = dateTimeFormat))
            }
            Timber.i("App update dialog show date time stored.")
        } catch (e: Exception) {
            Timber.e("Store app update dialog show date time failed:")
            e.printStackTrace()
        }
    }

    override suspend fun removeAppUpdateDialogShowDateTime() {
        try {
            dataStore.updateData { it.copy(appUpdateDialogShowDateTime = null) }
            Timber.i("App update dialog show date time removed")
        } catch (e: Exception) {
            Timber.e("Remove app update dialog show date time failed:")
            e.printStackTrace()
        }
    }

    override fun getSelectedRateAppOption(): Flow<RateAppOption> = dataStore.data.map {
        it.selectedRateAppOption
    }.catch { e ->
        Timber.e("Get selected rate app option failed:")
        e.printStackTrace()
    }

    override suspend fun storeSelectedRateAppOption(
        rateAppOption: RateAppOption
    ) {
        try {
            dataStore.updateData { it.copy(selectedRateAppOption = rateAppOption) }
            Timber.i("Selected rate app option edited to $rateAppOption")
        } catch (e: Exception) {
            Timber.e("Store selected rate app option failed:")
            e.printStackTrace()
        }
    }

    override fun getPreviousRateAppRequestDateTime(
    ): Flow<PreviousRateAppRequestDateTime?> = dataStore.data.map {
        it.previousRateAppRequestDateTime
    }.catch { e ->
        Timber.e("Get previous rate app request date time failed:")
        e.printStackTrace()
    }

    override suspend fun storePreviousRateAppRequestDateTime(
        dateTimeFormat: DateTimeFormat<DateTimeComponents>
    ) {
        try {
            val now = Clock.System.now()
            dataStore.updateData {
                it.copy(previousRateAppRequestDateTime = now.format(format = dateTimeFormat))
            }
            Timber.i("Previous rate app request date time stored.")
        } catch (e: Exception) {
            Timber.e("Store previous rate app request date time failed:")
            e.printStackTrace()
        }
    }
}