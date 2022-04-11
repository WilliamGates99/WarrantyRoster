package com.xeniac.warrantyroster_manager.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.xeniac.warrantyroster_manager.utils.Constants.DATASTORE_COUNTRY_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.DATASTORE_IS_LOGGED_IN_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.DATASTORE_LANGUAGE_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.DATASTORE_THEME_KEY
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val settingsDataStore: DataStore<Preferences>
) {

    private object PreferencesKeys {
        val IS_USER_LOGGED_IN = booleanPreferencesKey(DATASTORE_IS_LOGGED_IN_KEY)
        val CURRENT_THEME = intPreferencesKey(DATASTORE_THEME_KEY)
        val CURRENT_LANGUAGE = stringPreferencesKey(DATASTORE_LANGUAGE_KEY)
        val CURRENT_COUNTRY = stringPreferencesKey(DATASTORE_COUNTRY_KEY)
    }

    suspend fun getIsUserLoggedIn(): Boolean =
        settingsDataStore.data.first()[PreferencesKeys.IS_USER_LOGGED_IN] ?: false

    suspend fun setIsUserLoggedIn(value: Boolean) = settingsDataStore.edit { loginPreferences ->
        loginPreferences[PreferencesKeys.IS_USER_LOGGED_IN] = value
    }

    suspend fun setIsUserLoggedIn2(value: Boolean) {
        settingsDataStore.edit { loginPreferences ->
            when (value) {
                true -> loginPreferences[PreferencesKeys.IS_USER_LOGGED_IN] = value
                false -> loginPreferences.remove(PreferencesKeys.IS_USER_LOGGED_IN)
            }
        }
    }
}