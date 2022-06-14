package com.xeniac.warrantyroster_manager.repositories

class FakePreferencesRepository : PreferencesRepository {

    private var shouldReturnTrueValue = false

    fun setShouldReturnTrueValue(value: Boolean) {
        shouldReturnTrueValue = value
    }

    override fun getIsUserLoggedInSynchronously(): Boolean = shouldReturnTrueValue

    override fun getCurrentAppLanguageSynchronously(): String {
        TODO("Not yet implemented")
    }

    override fun getCurrentAppCountrySynchronously(): String {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentAppTheme(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentAppLanguage(): String {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentAppCountry(): String {
        TODO("Not yet implemented")
    }

    override suspend fun getCategoryTitleMapKey(): String {
        TODO("Not yet implemented")
    }

    override suspend fun setIsUserLoggedIn(value: Boolean) {
        shouldReturnTrueValue = value
    }

    override suspend fun setAppTheme(index: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun setCurrentAppLanguage(language: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setCurrentAppCountry(country: String) {
        TODO("Not yet implemented")
    }
}