package com.xeniac.warrantyroster_manager

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_COUNTRY_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_LANGUAGE_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_SETTINGS
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_THEME_KEY
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setNightMode()
        setLocale()
    }

    private fun setNightMode() {
        val settingsPrefs = getSharedPreferences(PREFERENCE_SETTINGS, MODE_PRIVATE)

        when (settingsPrefs.getInt(PREFERENCE_THEME_KEY, 0)) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun setLocale() {
        val settingsPrefs = getSharedPreferences(PREFERENCE_SETTINGS, MODE_PRIVATE)
        val language = settingsPrefs.getString(PREFERENCE_LANGUAGE_KEY, "en").toString()
        val country = settingsPrefs.getString(PREFERENCE_COUNTRY_KEY, "US").toString()

        val resources = resources
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        val newLocale = Locale(language, country)
        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, displayMetrics)
    }
}