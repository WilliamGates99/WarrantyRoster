package com.xeniac.warrantyroster_manager.core.domain.repository

import com.xeniac.warrantyroster_manager.util.UiText

interface PreferencesRepository {

    fun getCurrentAppThemeSynchronously(): Int

    fun isUserLoggedInSynchronously(): Boolean

    suspend fun getCurrentAppLocaleIndex(): Int

    suspend fun getCurrentAppLanguage(): String

    suspend fun getCurrentAppLocaleUiText(): UiText

    suspend fun getCurrentAppTheme(): Int

    suspend fun getRateAppDialogChoice(): Int

    suspend fun getPreviousRequestTimeInMillis(): Long

    suspend fun isUserLoggedIn(isLoggedIn: Boolean)

    suspend fun setCurrentAppLocale(index: Int): Boolean

    suspend fun setCurrentAppTheme(index: Int)

    suspend fun setRateAppDialogChoice(value: Int)

    suspend fun setPreviousRequestTimeInMillis(timeInMillis: Long)
}