package com.xeniac.warrantyroster_manager.core.data.repository

import android.os.Build
import android.util.LayoutDirection
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.text.layoutDirection
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.xeniac.warrantyroster_manager.core.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.util.Constants.DATASTORE_IS_LOGGED_IN_KEY
import com.xeniac.warrantyroster_manager.util.Constants.DATASTORE_PREVIOUS_REQUEST_TIME_IN_MILLIS_KEY
import com.xeniac.warrantyroster_manager.util.Constants.DATASTORE_RATE_APP_DIALOG_CHOICE_KEY
import com.xeniac.warrantyroster_manager.util.Constants.DATASTORE_THEME_KEY
import com.xeniac.warrantyroster_manager.util.Constants.LANGUAGE_DEFAULT_OR_EMPTY
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_DEFAULT_OR_EMPTY
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_PERSIAN_IRAN
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val settingsDataStore: DataStore<Preferences>
) : PreferencesRepository {

    private object PreferencesKeys {
        val IS_USER_LOGGED_IN = booleanPreferencesKey(DATASTORE_IS_LOGGED_IN_KEY)
        val CURRENT_APP_THEME = intPreferencesKey(DATASTORE_THEME_KEY)
        val RATE_APP_DIALOG_CHOICE = intPreferencesKey(DATASTORE_RATE_APP_DIALOG_CHOICE_KEY)
        val PREVIOUS_REQUEST_TIME_IN_MILLIS = longPreferencesKey(
            DATASTORE_PREVIOUS_REQUEST_TIME_IN_MILLIS_KEY
        )
    }

    override fun getCurrentAppThemeSynchronously(): Int = runBlocking {
        try {
            settingsDataStore.data.first()[PreferencesKeys.CURRENT_APP_THEME] ?: 0
        } catch (e: Exception) {
            Timber.e("getCurrentAppThemeSynchronously Exception: $e")
            0
        }
    }

    override fun isUserLoggedInSynchronously(): Boolean = runBlocking {
        try {
            settingsDataStore.data.first()[PreferencesKeys.IS_USER_LOGGED_IN] ?: false
        } catch (e: Exception) {
            Timber.e("isUserLoggedInSynchronously Exception: $e")
            false
        }
    }

    override suspend fun getCurrentAppLocaleIndex(): Int = try {
        val localeList = AppCompatDelegate.getApplicationLocales()

        if (localeList.isEmpty) {
            Timber.i("Locale list is Empty.")
            LOCALE_INDEX_DEFAULT_OR_EMPTY
        } else {
            val localeString = localeList[0].toString()
            Timber.i("Current locale is $localeString")

            when (localeString) {
                "en_US" -> {
                    Timber.i("Current locale index is 0 (en_US).")
                    LOCALE_INDEX_ENGLISH_UNITED_STATES
                }
                "en_GB" -> {
                    Timber.i("Current locale index is 1 (en_GB).")
                    LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
                }
                "fa_IR" -> {
                    Timber.i("Current locale index is 2 (fa_IR).")
                    LOCALE_INDEX_PERSIAN_IRAN
                }
                else -> {
                    Timber.i("Current language is System Default.")
                    LOCALE_INDEX_DEFAULT_OR_EMPTY
                }
            }
        }
    } catch (e: Exception) {
        Timber.e("getCurrentAppLocaleIndex Exception: $e")
        LOCALE_INDEX_DEFAULT_OR_EMPTY
    }

    override suspend fun getCurrentAppLanguage(): String = try {
        val localeList = AppCompatDelegate.getApplicationLocales()

        if (localeList.isEmpty) {
            Timber.i("Locale list is Empty. -> Current app language is $LANGUAGE_DEFAULT_OR_EMPTY")
            LANGUAGE_DEFAULT_OR_EMPTY
        } else {
            val currentAppLanguage = localeList[0]!!.language
            Timber.i("Current app language is $currentAppLanguage")
            currentAppLanguage
        }
    } catch (e: Exception) {
        Timber.e("getCurrentAppLocaleString Exception: $e")
        LANGUAGE_DEFAULT_OR_EMPTY
    }

    override suspend fun getCurrentAppTheme(): Int = try {
        settingsDataStore.data.first()[PreferencesKeys.CURRENT_APP_THEME] ?: 0
    } catch (e: Exception) {
        Timber.e("getCurrentAppTheme Exception: $e")
        0
    }

    override suspend fun getRateAppDialogChoice(): Int = try {
        settingsDataStore.data.first()[PreferencesKeys.RATE_APP_DIALOG_CHOICE] ?: 0
    } catch (e: Exception) {
        Timber.e("getRateAppDialogChoice Exception: $e")
        0
    }

    override suspend fun getPreviousRequestTimeInMillis(): Long = try {
        settingsDataStore.data.first()[PreferencesKeys.PREVIOUS_REQUEST_TIME_IN_MILLIS] ?: 0L
    } catch (e: Exception) {
        Timber.e("getPreviousRequestTimeInMillis Exception: $e")
        0L
    }

    override suspend fun isUserLoggedIn(isLoggedIn: Boolean) {
        try {
            settingsDataStore.edit {
                it[PreferencesKeys.IS_USER_LOGGED_IN] = isLoggedIn
                Timber.i("isUserLoggedIn edited to $isLoggedIn")
            }
        } catch (e: Exception) {
            Timber.e("isUserLoggedIn Exception: $e")
        }
    }

    override suspend fun setCurrentAppLocale(index: Int): Boolean = try {
        var isActivityRestartNeeded = false

        when (index) {
            LOCALE_INDEX_ENGLISH_UNITED_STATES -> {
                isActivityRestartNeeded = isActivityRestartNeeded(LayoutDirection.LTR)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(LOCALE_TAG_ENGLISH_UNITED_STATES)
                )
            }
            LOCALE_INDEX_ENGLISH_GREAT_BRITAIN -> {
                isActivityRestartNeeded = isActivityRestartNeeded(LayoutDirection.LTR)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(LOCALE_TAG_ENGLISH_GREAT_BRITAIN)
                )
            }
            LOCALE_INDEX_PERSIAN_IRAN -> {
                isActivityRestartNeeded = isActivityRestartNeeded(LayoutDirection.RTL)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(LOCALE_TAG_PERSIAN_IRAN)
                )
            }
        }

        Timber.i("isActivityRestartNeeded = $isActivityRestartNeeded}")
        Timber.i("App locale index changed to $index")
        isActivityRestartNeeded
    } catch (e: Exception) {
        Timber.e("setCurrentAppLocale Exception: $e")
        false
    }

    override suspend fun setCurrentAppTheme(index: Int) {
        try {
            settingsDataStore.edit {
                it[PreferencesKeys.CURRENT_APP_THEME] = index
                Timber.i("AppTheme edited to $index")
            }
        } catch (e: Exception) {
            Timber.e("setAppTheme Exception: $e")
        }
    }

    override suspend fun setRateAppDialogChoice(value: Int) {
        try {
            settingsDataStore.edit {
                it[PreferencesKeys.RATE_APP_DIALOG_CHOICE] = value
                Timber.i("RateAppDialogChoice edited to $value")
            }
        } catch (e: Exception) {
            Timber.e("setRateAppDialogChoice Exception: $e")
        }
    }

    override suspend fun setPreviousRequestTimeInMillis(timeInMillis: Long) {
        try {
            settingsDataStore.edit {
                it[PreferencesKeys.PREVIOUS_REQUEST_TIME_IN_MILLIS] = timeInMillis
                Timber.i("PreviousRequestTimeInMillis edited to $timeInMillis")
            }
        } catch (e: Exception) {
            Timber.e("setPreviousRequestTimeInMillis Exception: $e")
        }
    }

    private fun isActivityRestartNeeded(newLayoutDirection: Int): Boolean {
        val currentLocale = AppCompatDelegate.getApplicationLocales()[0]
        val currentLayoutDirection = currentLocale?.layoutDirection

        return if (Build.VERSION.SDK_INT >= 33) {
            false
        } else currentLayoutDirection != newLayoutDirection
    }
}