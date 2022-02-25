package com.xeniac.warrantyroster_manager

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.xeniac.warrantyroster_manager.di.CurrentCountry
import com.xeniac.warrantyroster_manager.di.CurrentLanguage
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {

    @set:Inject
    var currentTheme = 0

    @Inject
    @CurrentLanguage
    lateinit var currentLanguage: String

    @Inject
    @CurrentCountry
    lateinit var currentCountry: String

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        setNightMode()
        setLocale()
    }

    private fun setNightMode() {
        when (currentTheme) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun setLocale() {
        val resources = resources
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        val newLocale = Locale(currentLanguage, currentCountry)
        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, displayMetrics)
    }
}