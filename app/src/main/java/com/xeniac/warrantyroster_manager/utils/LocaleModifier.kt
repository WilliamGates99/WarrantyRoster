package com.xeniac.warrantyroster_manager.utils

import android.content.Context
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_COUNTRY_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_LANGUAGE_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_SETTINGS
import java.util.*

object LocaleModifier {
    fun setLocale(context: Context) {
        val localePrefs = context
            .getSharedPreferences(PREFERENCE_SETTINGS, Context.MODE_PRIVATE)
        val language = localePrefs.getString(PREFERENCE_LANGUAGE_KEY, "en").toString()
        val country = localePrefs.getString(PREFERENCE_COUNTRY_KEY, "US").toString()

        val resources = context.resources
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        val newLocale = Locale(language, country)
        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, displayMetrics)
    }
}