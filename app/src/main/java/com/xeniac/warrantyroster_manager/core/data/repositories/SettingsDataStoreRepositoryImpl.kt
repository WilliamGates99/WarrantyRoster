package com.xeniac.warrantyroster_manager.core.data.repositories

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.AppTheme
import com.xeniac.warrantyroster_manager.core.domain.models.SettingsPreferences
import com.xeniac.warrantyroster_manager.core.domain.repositories.IsActivityRestartNeeded
import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

class SettingsDataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<SettingsPreferences>
) : SettingsDataStoreRepository {

    override fun getCurrentAppThemeSynchronously(): AppTheme = try {
        val appThemeIndex = runBlocking {
            dataStore.data.first().themeIndex
        }

        when (appThemeIndex) {
            AppTheme.DEFAULT.index -> AppTheme.DEFAULT
            AppTheme.LIGHT.index -> AppTheme.LIGHT
            AppTheme.DARK.index -> AppTheme.DARK
            else -> AppTheme.DEFAULT
        }
    } catch (e: Exception) {
        Timber.e("Get current app theme synchronously failed:")
        e.printStackTrace()
        AppTheme.DEFAULT
    }

    override fun getCurrentAppTheme(): Flow<AppTheme> = dataStore.data.map {
        val appThemeIndex = it.themeIndex

        when (appThemeIndex) {
            AppTheme.DEFAULT.index -> AppTheme.DEFAULT
            AppTheme.LIGHT.index -> AppTheme.LIGHT
            AppTheme.DARK.index -> AppTheme.DARK
            else -> AppTheme.DEFAULT
        }
    }.catch { e ->
        Timber.e("Get current app theme failed:")
        e.printStackTrace()
    }

    override suspend fun storeCurrentAppTheme(
        appTheme: AppTheme
    ) {
        try {
            dataStore.updateData { it.copy(themeIndex = appTheme.index) }
            Timber.i("Current app theme edited to ${appTheme.index}")
        } catch (e: Exception) {
            Timber.e("Store current app theme failed:")
            e.printStackTrace()
        }
    }

    override fun getCurrentAppLocale(): AppLocale {
        return try {
            val appLocaleList = AppCompatDelegate.getApplicationLocales()

            if (appLocaleList.isEmpty) {
                Timber.i("App locale list is Empty.")
                return AppLocale.DEFAULT
            }

            val localeString = appLocaleList[0].toString()
            Timber.i("Current app locale string is $localeString")

            when (localeString) {
                AppLocale.ENGLISH_US.localeString -> AppLocale.ENGLISH_US
                AppLocale.ENGLISH_GB.localeString -> AppLocale.ENGLISH_GB
                AppLocale.FARSI_IR.localeString -> AppLocale.FARSI_IR
                else -> AppLocale.DEFAULT
            }
        } catch (e: Exception) {
            Timber.e("Get current app locale failed:")
            e.printStackTrace()
            AppLocale.DEFAULT
        }
    }

    override suspend fun storeCurrentAppLocale(
        newAppLocale: AppLocale
    ): IsActivityRestartNeeded = try {
        val isActivityRestartNeeded = isActivityRestartNeeded(newAppLocale)

        AppCompatDelegate.setApplicationLocales(
            /* locales = */ LocaleListCompat.forLanguageTags(newAppLocale.languageTag)
        )

        isActivityRestartNeeded
    } catch (e: Exception) {
        Timber.e("Store current app locale failed:")
        e.printStackTrace()
        false
    }

    private fun isActivityRestartNeeded(
        newLocale: AppLocale
    ): Boolean = getCurrentAppLocale().layoutDirectionCompose != newLocale.layoutDirectionCompose
}