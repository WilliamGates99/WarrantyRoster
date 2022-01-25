package com.xeniac.warrantyroster_manager.utils

import android.content.Context

object CategoryHelper {
    fun getCategoryTitleMapKey(context: Context): String {
        val settingsPrefs = context
            .getSharedPreferences(Constants.PREFERENCE_SETTINGS, Context.MODE_PRIVATE)
        val currentLanguage = settingsPrefs
            .getString(Constants.PREFERENCE_LANGUAGE_KEY, "en").toString()
        val currentCountry = settingsPrefs
            .getString(Constants.PREFERENCE_COUNTRY_KEY, "US").toString()
        return "${currentLanguage}-${currentCountry}"
    }
}