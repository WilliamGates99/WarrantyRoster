package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeUserRepository: FakeUserRepository

    private lateinit var testViewModel: MainViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        testViewModel = MainViewModel(FakePreferencesRepository())
    }

    @Test
    fun getRateAppDialogChoice_returnsDefaultRateAppDialogChoice() {
        val defaultRateAppDialogChoice = 0
        testViewModel.getRateAppDialogChoice()

        val responseEvent = testViewModel.rateAppDialogChoiceLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isEqualTo(defaultRateAppDialogChoice)
    }

    @Test
    fun getPreviousRequestTimeInMillis_returnsDefaultPreviousRequestTimeInMillis() {
        val defaultPreviousRequestTimeInMillis = 0L
        testViewModel.getPreviousRequestTimeInMillis()

        val responseEvent = testViewModel.previousRequestTimeInMillisLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled())
            .isEqualTo(defaultPreviousRequestTimeInMillis)
    }

    @Test
    fun setRateAppDialogChoice_returnsNewRateAppDialogChoice() {
        val newRateAppDialogChoice = 1
        testViewModel.setRateAppDialogChoice(newRateAppDialogChoice)

        val responseEvent = testViewModel.rateAppDialogChoiceLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isEqualTo(newRateAppDialogChoice)
    }
}