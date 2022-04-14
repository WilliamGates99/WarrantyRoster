package com.xeniac.warrantyroster_manager.utils

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
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

    fun setAppLocale(context: Context, language: String, country: String): ContextWrapper {
        /**
         * English (United States) -> "en-US"
         * English (Great Britain) -> "en-GB"
         * Persian (Iran) -> "fa-IR"
         */

        return ContextWrapper(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                updateResources(context, language, country)
            } else {
                updateResourcesLegacy(context, language, country)
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String, country: String): Context {
        val newLocaleList = LocaleList(Locale(language, country))
        LocaleList.setDefault(newLocaleList)

        val configuration = context.resources.configuration
        configuration.setLocales(newLocaleList)

        return context.createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(
        context: Context, language: String, country: String
    ): Context {
        val newLocale = Locale(language, country)
        Locale.setDefault(newLocale)

        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }
}