package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_PERSIAN_IRAN
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LandingViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var testViewModel: LandingViewModel

    @Before
    fun setUp() {
        testViewModel = LandingViewModel(FakePreferencesRepository())
    }

    @Test
    fun isUserLoggedIn_returnsDefaultValue() {
        val isUserLoggedIn = testViewModel.isUserLoggedIn()
        assertThat(isUserLoggedIn).isFalse()
    }

    @Test
    fun getCurrentLanguage_returnsDefaultLanguageIndex() {
        testViewModel.getCurrentLanguage()

        val responseEvent = testViewModel.currentLocaleIndexLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isEqualTo(
            LOCALE_INDEX_ENGLISH_UNITED_STATES
        )
    }

    @Test
    fun changeCurrentLocaleToEnglishGb_returnsFalse() {
        testViewModel.changeCurrentLocale(LOCALE_INDEX_ENGLISH_GREAT_BRITAIN)

        val responseEvent = testViewModel.changeCurrentLocaleLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isFalse()
    }

    @Test
    fun changeCurrentLocaleToPersian_returnsTrue() {
        testViewModel.changeCurrentLocale(LOCALE_INDEX_PERSIAN_IRAN)

        val responseEvent = testViewModel.changeCurrentLocaleLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isTrue()
    }
}