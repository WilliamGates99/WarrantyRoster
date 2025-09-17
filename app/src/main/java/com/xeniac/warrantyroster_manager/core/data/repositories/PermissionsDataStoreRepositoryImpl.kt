package com.xeniac.warrantyroster_manager.core.data.repositories

import androidx.datastore.core.DataStore
import com.xeniac.warrantyroster_manager.core.domain.models.PermissionsPreferences
import com.xeniac.warrantyroster_manager.core.domain.repositories.PermissionsDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class PermissionsDataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<PermissionsPreferences>
) : PermissionsDataStoreRepository {

    override fun getNotificationPermissionCount(): Flow<Int> = dataStore.data.map {
        it.notificationPermissionCount
    }.catch { e ->
        Timber.e("Get notification permission count failed:")
        e.printStackTrace()
    }

    override suspend fun storeNotificationPermissionCount(count: Int) {
        try {
            dataStore.updateData { it.copy(notificationPermissionCount = count) }
            Timber.i("Notification permission count edited to $count")
        } catch (e: Exception) {
            Timber.e("Store notification permission count failed:")
            e.printStackTrace()
        }
    }
}