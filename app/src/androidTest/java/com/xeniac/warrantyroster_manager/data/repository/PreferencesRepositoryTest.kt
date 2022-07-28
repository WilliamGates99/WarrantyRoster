package com.xeniac.warrantyroster_manager.data.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_COUNTRY_IRAN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_COUNTRY_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_ENGLISH
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_PERSIAN
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PreferencesRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val testScope: TestScope = TestScope(testDispatcher + Job())

    private val testDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = { context.preferencesDataStoreFile("settings_test") }
    )

    private val testRepository: PreferencesRepository = PreferencesRepositoryImp(testDataStore)

    @After
    fun tearDown() {
        testScope.cancel()
    }

    /**
     * Fetch Initial Preferences Test Cases:
     * getIsUserLoggedInSynchronously -> false
     * getCurrentAppLanguageSynchronously -> LOCALE_LANGUAGE_ENGLISH
     * getCurrentAppCountrySynchronously -> LOCALE_COUNTRY_UNITED_STATES
     * getCurrentAppTheme -> 0
     * getCurrentAppLanguage -> LOCALE_LANGUAGE_ENGLISH
     * getCurrentAppCountry -> LOCALE_COUNTRY_UNITED_STATES
     * getCategoryTitleMapKey -> "LOCALE_LANGUAGE_ENGLISH-LOCALE_COUNTRY_UNITED_STATES"
     */

    @Test
    fun fetchInitialSynchronousPreferences() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val initialIsUserLoggedIn = testRepository.isUserLoggedInSynchronously()
        val initialCurrentAppLanguage = testRepository.getCurrentAppLanguageSynchronously()
        val initialCurrentAppCountry = testRepository.getCurrentAppCountrySynchronously()

        assertThat(initialIsUserLoggedIn).isFalse()
        assertThat(initialCurrentAppLanguage).isEqualTo(LOCALE_LANGUAGE_ENGLISH)
        assertThat(initialCurrentAppCountry).isEqualTo(LOCALE_COUNTRY_UNITED_STATES)
    }

    @Test
    fun fetchInitialPreferences() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val initialCurrentAppTheme = testRepository.getCurrentAppTheme()
        val initialCurrentAppLanguage = testRepository.getCurrentAppLanguage()
        val initialCurrentAppCountry = testRepository.getCurrentAppCountry()
        val initialCategoryTitleMapKey = testRepository.getCategoryTitleMapKey()
        val initialRateAppDialogChoice = testRepository.getRateAppDialogChoice()
        val initialPreviousRequestTimeInMillis = testRepository.getPreviousRequestTimeInMillis()

        assertThat(initialCurrentAppTheme).isEqualTo(0)
        assertThat(initialCurrentAppLanguage).isEqualTo(LOCALE_LANGUAGE_ENGLISH)
        assertThat(initialCurrentAppCountry).isEqualTo(LOCALE_COUNTRY_UNITED_STATES)
        assertThat(initialCategoryTitleMapKey).isEqualTo("$LOCALE_LANGUAGE_ENGLISH-$LOCALE_COUNTRY_UNITED_STATES")
        assertThat(initialRateAppDialogChoice).isEqualTo(0)
        assertThat(initialPreviousRequestTimeInMillis).isEqualTo(0L)
    }

    /**
     * Write Preferences Test Cases:
     * setIsUserLoggedIn
     * setAppTheme
     * setCurrentAppLanguage
     * setCurrentAppCountry
     */

    @Test
    fun writeIsUserLoggedIn() = testScope.runTest {
        testDataStore.edit { it.clear() }

        testRepository.isUserLoggedIn(true)

        val isUserLoggedIn = testRepository.isUserLoggedInSynchronously()
        assertThat(isUserLoggedIn).isTrue()
    }

    @Test
    fun writeAppTheme() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val testValue = 1
        testRepository.setCurrentAppTheme(testValue)

        val currentAppTheme = testRepository.getCurrentAppTheme()
        assertThat(currentAppTheme).isEqualTo(testValue)
    }

    @Test
    fun writeCurrentAppLanguage() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val testValue = LOCALE_LANGUAGE_PERSIAN
        testRepository.setCurrentAppLanguage(testValue)

        val currentLanguage = testRepository.getCurrentAppLanguage()
        assertThat(currentLanguage).isEqualTo(testValue)
    }

    @Test
    fun writeCurrentAppCountry() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val testValue = LOCALE_COUNTRY_IRAN
        testRepository.setCurrentAppCountry(testValue)

        val currentCountry = testRepository.getCurrentAppCountry()
        assertThat(currentCountry).isEqualTo(testValue)
    }

    @Test
    fun writeRateAppDialogChoice() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val testValue = 1
        testRepository.setRateAppDialogChoice(testValue)

        val isInAppReviewsShown = testRepository.getRateAppDialogChoice()
        assertThat(isInAppReviewsShown).isEqualTo(testValue)
    }

    @Test
    fun writePreviousRequestTimeInMillis() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val testValue = 100L
        testRepository.setPreviousRequestTimeInMillis(testValue)

        val previousRequestTimeInMillis = testRepository.getPreviousRequestTimeInMillis()
        assertThat(previousRequestTimeInMillis).isEqualTo(testValue)
    }
}