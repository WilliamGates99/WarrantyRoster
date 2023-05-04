package com.xeniac.warrantyroster_manager.core.data.repository

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
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.util.Constants.LANGUAGE_DEFAULT_OR_EMPTY
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_DEFAULT_OR_EMPTY
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Constants.THEME_INDEX_DARK
import com.xeniac.warrantyroster_manager.util.Constants.THEME_INDEX_DEFAULT
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

    private val testRepository: PreferencesRepository = PreferencesRepositoryImpl(testDataStore)

    @After
    fun tearDown() {
        testScope.cancel()
    }

    /**
     * Fetch Initial Preferences Test Cases:
     * getCurrentAppThemeIndexSynchronously -> THEME_INDEX_DEFAULT
     * isUserLoggedInSynchronously -> false
     * getCategoryTitleMapKey -> LOCALE_TAG_ENGLISH_UNITED_STATES
     * getCurrentAppLocaleIndex -> LOCALE_INDEX_DEFAULT_OR_EMPTY
     * getCurrentAppLanguage -> LANGUAGE_DEFAULT_OR_EMPTY
     * getCurrentAppLocaleUiText -> LOCALE_INDEX_DEFAULT_OR_EMPTY
     * getCurrentAppThemeIndex -> THEME_INDEX_DEFAULT
     * getCurrentAppThemeUiText -> R.string.settings_text_settings_theme_default
     * getRateAppDialogChoice -> 0
     * getPreviousRequestTimeInMillis -> 0L
     */

    @Test
    fun fetchInitialSynchronousPreferences() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val initialCurrentAppThemeIndex = testRepository.getCurrentAppThemeIndexSynchronously()
        val initialIsUserLoggedIn = testRepository.isUserLoggedInSynchronously()
        val initialCategoryTitleMapKey = testRepository.getCategoryTitleMapKey()

        assertThat(initialCurrentAppThemeIndex).isEqualTo(THEME_INDEX_DEFAULT)
        assertThat(initialIsUserLoggedIn).isFalse()
        assertThat(initialCategoryTitleMapKey).isEqualTo(LOCALE_TAG_ENGLISH_UNITED_STATES)
    }

    @Test
    fun fetchInitialPreferences() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val initialCurrentAppTheme = testRepository.getCurrentAppThemeIndex()
        val initialCurrentAppLocaleIndex = testRepository.getCurrentAppLocaleIndex()
        val initialCurrentAppLanguage = testRepository.getCurrentAppLanguage()
        val initialCurrentAppLocaleUiText = testRepository.getCurrentAppLocaleUiText()
        val initialCurrentAppThemeIndex = testRepository.getCurrentAppThemeIndex()
        val initialCurrentAppThemeUiText = testRepository.getCurrentAppThemeUiText()
        val initialRateAppDialogChoice = testRepository.getRateAppDialogChoice()
        val initialPreviousRequestTimeInMillis = testRepository.getPreviousRequestTimeInMillis()

        assertThat(initialCurrentAppTheme).isEqualTo(LOCALE_INDEX_DEFAULT_OR_EMPTY)
        assertThat(initialCurrentAppLocaleIndex).isEqualTo(LOCALE_INDEX_DEFAULT_OR_EMPTY)
        assertThat(initialCurrentAppLanguage).isEqualTo(LANGUAGE_DEFAULT_OR_EMPTY)
        assertThat(initialCurrentAppLocaleUiText.asString(context)).isEqualTo(
            LOCALE_INDEX_DEFAULT_OR_EMPTY
        )
        assertThat(initialCurrentAppThemeIndex).isEqualTo(THEME_INDEX_DEFAULT)
        assertThat(initialCurrentAppThemeUiText.asString(context)).isEqualTo(
            R.string.settings_text_settings_theme_default
        )
        assertThat(initialRateAppDialogChoice).isEqualTo(0)
        assertThat(initialPreviousRequestTimeInMillis).isEqualTo(0L)
    }

    /**
     * Write Preferences Test Cases:
     * isUserLoggedIn
     * setCurrentAppLocale
     * setCurrentAppTheme
     * setRateAppDialogChoice
     * setPreviousRequestTimeInMillis
     */

    @Test
    fun writeIsUserLoggedIn() = testScope.runTest {
        testDataStore.edit { it.clear() }

        testRepository.isUserLoggedIn(true)

        val isUserLoggedIn = testRepository.isUserLoggedInSynchronously()
        assertThat(isUserLoggedIn).isTrue()
    }

    @Test
    fun writeCurrentAppLocale() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val testValue = LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
        testRepository.setCurrentAppLocale(testValue)

        val currentAppLocaleIndex = testRepository.getCurrentAppLocaleIndex()
        assertThat(currentAppLocaleIndex).isEqualTo(testValue)
    }

    @Test
    fun writeCurrentAppTheme() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val testValue = THEME_INDEX_DARK
        testRepository.setCurrentAppTheme(testValue)

        val currentAppThemeIndex = testRepository.getCurrentAppThemeIndex()
        assertThat(currentAppThemeIndex).isEqualTo(testValue)
    }

    @Test
    fun writeRateAppDialogChoice() = testScope.runTest {
        testDataStore.edit { it.clear() }

        val testValue = 1
        testRepository.setRateAppDialogChoice(testValue)

        val rateAppDialogChoice = testRepository.getRateAppDialogChoice()
        assertThat(rateAppDialogChoice).isEqualTo(testValue)
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