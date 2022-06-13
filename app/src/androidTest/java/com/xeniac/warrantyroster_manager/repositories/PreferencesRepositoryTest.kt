package com.xeniac.warrantyroster_manager.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.di.TestPreferencesRepository
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_COUNTRY_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_ENGLISH
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class PreferencesRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    @TestPreferencesRepository
    lateinit var testRepository: PreferencesRepository

    @Before
    fun setup() {
        hiltRule.inject()
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
    fun fetchInitialSynchronousPreferences() {
        val initialIsUserLoggedIn = testRepository.getIsUserLoggedInSynchronously()
        val initialCurrentAppLanguage = testRepository.getCurrentAppLanguageSynchronously()
        val initialCurrentAppCountry = testRepository.getCurrentAppCountrySynchronously()

        assertThat(initialIsUserLoggedIn).isFalse()
        assertThat(initialCurrentAppLanguage).isEqualTo(LOCALE_LANGUAGE_ENGLISH)
        assertThat(initialCurrentAppCountry).isEqualTo(LOCALE_COUNTRY_UNITED_STATES)
    }

    @Test
    fun fetchInitialPreferences() = runTest {
        val initialCurrentAppTheme = testRepository.getCurrentAppTheme()
        val initialCurrentAppLanguage = testRepository.getCurrentAppLanguage()
        val initialCurrentAppCountry = testRepository.getCurrentAppCountry()
        val initialCategoryTitleMapKey = testRepository.getCategoryTitleMapKey()

        assertThat(initialCurrentAppTheme).isEqualTo(0)
        assertThat(initialCurrentAppLanguage).isEqualTo(LOCALE_LANGUAGE_ENGLISH)
        assertThat(initialCurrentAppCountry).isEqualTo(LOCALE_COUNTRY_UNITED_STATES)
        assertThat(initialCategoryTitleMapKey).isEqualTo("$LOCALE_LANGUAGE_ENGLISH-$LOCALE_COUNTRY_UNITED_STATES")
    }

    /**
     * Write Preferences Test Cases:
     * setIsUserLoggedIn
     * setAppTheme
     * setCurrentAppLanguage
     * setCurrentAppCountry
     */

    @Test
    fun writeIsUserLoggedIn() = runTest {
        testRepository.setIsUserLoggedIn(true)

        val isUserLoggedIn = testRepository.getIsUserLoggedInSynchronously()
        assertThat(isUserLoggedIn).isTrue()
    }

    @Test
    fun writeAppTheme() = runTest {
        val testValue = 1
        testRepository.setAppTheme(testValue)

        val currentAppTheme = testRepository.getCurrentAppTheme()
        assertThat(currentAppTheme).isEqualTo(testValue)
    }

    @Test
    fun writeCurrentAppLanguage() = runTest {
        val testValue = "fa"
        testRepository.setCurrentAppLanguage(testValue)

        val currentLanguage = testRepository.getCurrentAppLanguage()
        assertThat(currentLanguage).isEqualTo(testValue)
    }

    @Test
    fun writeCurrentAppCountry() = runTest {
        val testValue = "IR"
        testRepository.setCurrentAppCountry(testValue)

        val currentCountry = testRepository.getCurrentAppCountry()
        assertThat(currentCountry).isEqualTo(testValue)
    }
}