package com.xeniac.warrantyroster_manager.core.data.repositories

import androidx.datastore.core.DataStore
import com.xeniac.warrantyroster_manager.core.domain.models.WarrantyRosterPreferences
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class WarrantyRosterDataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<WarrantyRosterPreferences>
) : WarrantyRosterDataStoreRepository {

    override suspend fun isUserLoggedIn(): Boolean = try {
        dataStore.data.first().isUserLoggedIn
    } catch (e: Exception) {
        Timber.e("Get is user logged in failed:")
        e.printStackTrace()
        false
    }

    override suspend fun isUserLoggedIn(isLoggedIn: Boolean) {
        try {
            dataStore.updateData { it.copy(isUserLoggedIn = isLoggedIn) }
            Timber.i("Is user logged in edited to $isLoggedIn")
        } catch (e: Exception) {
            Timber.e("Store is user logged in failed:")
            e.printStackTrace()
        }
    }
}