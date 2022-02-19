package com.xeniac.warrantyroster_manager.utils

import android.content.Context
import com.xeniac.warrantyroster_manager.di.CurrentCountry
import com.xeniac.warrantyroster_manager.di.CurrentLanguage
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.*

object LocaleModifier {

    private lateinit var currentLanguage: String
    private lateinit var currentCountry: String

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface CurrentLanguageProviderEntryPoint {
        @CurrentLanguage
        fun getCurrentLanguage(): String
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface CurrentCountryProviderEntryPoint {
        @CurrentCountry
        fun getCurrentCountry(): String
    }

    fun setLocale(context: Context) {
        val languageProviderEntryPoint = EntryPointAccessors
            .fromApplication(context, CurrentLanguageProviderEntryPoint::class.java)
        val countryProviderEntryPoint = EntryPointAccessors
            .fromApplication(context, CurrentCountryProviderEntryPoint::class.java)

        currentLanguage = languageProviderEntryPoint.getCurrentLanguage()
        currentCountry = countryProviderEntryPoint.getCurrentCountry()

        val resources = context.resources
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        val newLocale = Locale(currentLanguage, currentCountry)
        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, displayMetrics)
    }
}