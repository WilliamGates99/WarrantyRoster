package com.xeniac.warrantyroster_manager.utils

import androidx.appcompat.app.AppCompatDelegate

object SettingsHelper {

    fun setAppTheme(themeIndex: Int) {
        when (themeIndex) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}