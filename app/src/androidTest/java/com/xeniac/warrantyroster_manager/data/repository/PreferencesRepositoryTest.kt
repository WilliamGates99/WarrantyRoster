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
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_PERSIAN_IRAN
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
     * getCurrentAppThemeSynchronously -> 0
     * getIsUserLoggedInSynchronously -> false
     * getCurrentAppTheme -> 0
     * getRateAppDialogChoice -> 0
     * getPreviousRequestTimeInMillis -> 0L
     * getCategoryTitleMapKey -> LOCALE_ENGLISH_UNITED_STATES
     */

    @Test
    fun fetchInitialSynchronousPreferences() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val initialCurrentAppTheme = testRepository.getCurrentAppThemeSynchronously()
        val initialIsUserLoggedIn = testRepository.isUserLoggedInSynchronously()

        assertThat(initialCurrentAppTheme).isEqualTo(0)
        assertThat(initialIsUserLoggedIn).isFalse()
    }

    @Test
    fun fetchInitialPreferences() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val initialCurrentAppTheme = testRepository.getCurrentAppTheme()
        val initialRateAppDialogChoice = testRepository.getRateAppDialogChoice()
        val initialPreviousRequestTimeInMillis = testRepository.getPreviousRequestTimeInMillis()
        val initialCategoryTitleMapKey = testRepository.getCategoryTitleMapKey()

        assertThat(initialCurrentAppTheme).isEqualTo(0)
        assertThat(initialRateAppDialogChoice).isEqualTo(0)
        assertThat(initialPreviousRequestTimeInMillis).isEqualTo(0L)
        assertThat(initialCategoryTitleMapKey).isEqualTo(LOCALE_ENGLISH_UNITED_STATES)
    }

    /**
     * Write Preferences Test Cases:
     * setIsUserLoggedIn
     * setAppTheme
     * setRateAppDialogChoice
     * setPreviousRequestTimeInMillis
     * setCategoryTitleMapKey
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

    @Test
    fun writeCategoryTitleMapKey() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val testValue = LOCALE_PERSIAN_IRAN
        testRepository.setCategoryTitleMapKey(testValue)

        val categoryTitleMapKey = testRepository.getCategoryTitleMapKey()
        assertThat(categoryTitleMapKey).isEqualTo(testValue)
    }
}