package com.xeniac.warrantyroster_manager.onboarding.presentation.onboarding

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class OnBoardingViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var testViewModel: OnBoardingViewModel

    @Before
    fun setUp() {
        testViewModel = OnBoardingViewModel(FakePreferencesRepository())
    }

    @Test
    fun getCurrentAppLocaleIndex_returnsDefaultAppLocaleIndex() {
        val defaultAppLocaleIndex = LOCALE_INDEX_ENGLISH_UNITED_STATES
        testViewModel.getCurrentAppLocaleIndex()

        val responseEvent = testViewModel.currentAppLocaleIndexLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()?.data).isEqualTo(defaultAppLocaleIndex)
    }

    @Test
    fun changeCurrentAppLocale_returnsNewAppLocaleIndex() {
        val testIndex = LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
        testViewModel.changeCurrentAppLocale(testIndex)
        testViewModel.getCurrentAppLocaleIndex()

        val responseEvent = testViewModel.currentAppLocaleIndexLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()?.data).isEqualTo(testIndex)
    }
}