package com.xeniac.warrantyroster_manager.data.repository

import com.xeniac.warrantyroster_manager.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_ENGLISH_UNITED_STATES

class FakePreferencesRepository : PreferencesRepository {

    private var isUserLoggedIn = false
    private var currentAppTheme = 0
    private var rateAppDialogChoice = 0
    private var previousRequestTimeInMillis = 0L
    private var categoryTitleMapKey = LOCALE_ENGLISH_UNITED_STATES

    override fun getCurrentAppThemeSynchronously(): Int = currentAppTheme

    override fun isUserLoggedInSynchronously(): Boolean = isUserLoggedIn

    override suspend fun getCurrentAppTheme(): Int = currentAppTheme

    override suspend fun getRateAppDialogChoice(): Int = rateAppDialogChoice

    override suspend fun getPreviousRequestTimeInMillis(): Long = previousRequestTimeInMillis

    override suspend fun getCategoryTitleMapKey(): String = categoryTitleMapKey

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

    override suspend fun setCategoryTitleMapKey(mapKey: String) {
        categoryTitleMapKey = mapKey
    }
}