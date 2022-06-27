package com.xeniac.warrantyroster_manager.data.repository

import com.xeniac.warrantyroster_manager.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_COUNTRY_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_ENGLISH

class FakePreferencesRepository : PreferencesRepository {

    private var isUserLoggedIn = false
    private var currentAppTheme = 0
    private var currentAppLanguage = LOCALE_LANGUAGE_ENGLISH
    private var currentAppCountry = LOCALE_COUNTRY_UNITED_STATES

    override fun getIsUserLoggedInSynchronously(): Boolean = isUserLoggedIn

    override fun getCurrentAppLanguageSynchronously(): String = currentAppLanguage

    override fun getCurrentAppCountrySynchronously(): String = currentAppCountry

    override suspend fun getCurrentAppTheme(): Int = currentAppTheme

    override suspend fun getCurrentAppLanguage(): String = currentAppLanguage

    override suspend fun getCurrentAppCountry(): String = currentAppCountry

    override suspend fun getCategoryTitleMapKey(): String =
        "${getCurrentAppLanguage()}-${getCurrentAppCountry()}"

    override suspend fun setIsUserLoggedIn(value: Boolean) {
        isUserLoggedIn = value
    }

    override suspend fun setAppTheme(index: Int) {
        currentAppTheme = index
    }

    override suspend fun setCurrentAppLanguage(language: String) {
        currentAppLanguage = language
    }

    override suspend fun setCurrentAppCountry(country: String) {
        currentAppCountry = country
    }
}