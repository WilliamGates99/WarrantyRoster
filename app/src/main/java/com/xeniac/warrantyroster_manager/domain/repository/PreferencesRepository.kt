package com.xeniac.warrantyroster_manager.domain.repository

interface PreferencesRepository {

    fun getCurrentAppThemeSynchronously(): Int

    fun isUserLoggedInSynchronously(): Boolean

    suspend fun getCurrentAppTheme(): Int

    suspend fun getRateAppDialogChoice(): Int

    suspend fun getPreviousRequestTimeInMillis(): Long

    suspend fun isUserLoggedIn(isLoggedIn: Boolean)

    suspend fun setCurrentAppTheme(index: Int)

    suspend fun setRateAppDialogChoice(value: Int)

    suspend fun setPreviousRequestTimeInMillis(timeInMillis: Long)
}