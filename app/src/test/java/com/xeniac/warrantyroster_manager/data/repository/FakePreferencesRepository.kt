package com.xeniac.warrantyroster_manager.data.repository

import com.xeniac.warrantyroster_manager.core.domain.PreferencesRepository

class FakePreferencesRepository : PreferencesRepository {

    private var isUserLoggedIn = false
    private var currentAppTheme = 0
    private var rateAppDialogChoice = 0
    private var previousRequestTimeInMillis = 0L

    override fun getCurrentAppThemeSynchronously(): Int = currentAppTheme

    override fun isUserLoggedInSynchronously(): Boolean = isUserLoggedIn

    override suspend fun getCurrentAppTheme(): Int = currentAppTheme

    override suspend fun getRateAppDialogChoice(): Int = rateAppDialogChoice

    override suspend fun getPreviousRequestTimeInMillis(): Long = previousRequestTimeInMillis

    override suspend fun isUserLoggedIn(isLoggedIn: Boolean) {
        isUserLoggedIn = isLoggedIn
    }

    override suspend fun setCurrentAppTheme(index: Int) {
        currentAppTheme = index
    }

    override suspend fun setRateAppDialogChoice(value: Int) {
        rateAppDialogChoice = value
    }

    override suspend fun setPreviousRequestTimeInMillis(timeInMillis: Long) {
        previousRequestTimeInMillis = timeInMillis
    }
}