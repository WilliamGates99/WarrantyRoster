package com.xeniac.warrantyroster_manager.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import java.util.*

object SettingsHelper {

    fun setAppTheme(index: Int) {
        when (index) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun setAppLocale(context: Context, currentLanguage: String, currentCountry: String) {
        val resources = context.resources
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        val newLocale = Locale(currentLanguage, currentCountry)
        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, displayMetrics)
    }
}