package com.xeniac.warrantyroster_manager.domain.repository

interface PreferencesRepository {

    fun getIsUserLoggedInSynchronously(): Boolean

    fun getCurrentAppLanguageSynchronously(): String

    fun getCurrentAppCountrySynchronously(): String

    suspend fun getCurrentAppTheme(): Int

    suspend fun getCurrentAppLanguage(): String

    suspend fun getCurrentAppCountry(): String

    suspend fun getCategoryTitleMapKey(): String

    suspend fun setIsUserLoggedIn(value: Boolean)

    suspend fun setAppTheme(index: Int)

    suspend fun setCurrentAppLanguage(language: String)

    suspend fun setCurrentAppCountry(country: String)
}