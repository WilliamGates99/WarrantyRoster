package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.utils.Constants.THEME_INDEX_DARK
import com.xeniac.warrantyroster_manager.utils.Constants.THEME_INDEX_DEFAULT
import com.xeniac.warrantyroster_manager.utils.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakePreferencesRepository: FakePreferencesRepository

    private lateinit var testViewModel: SettingsViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        fakePreferencesRepository = FakePreferencesRepository()

        testViewModel = SettingsViewModel(
            fakeUserRepository,
            fakePreferencesRepository
        )
    }

    @Test
    fun getCurrentAppTheme_returnsDefaultThemeIndex() {
        val defaultThemeIndex = THEME_INDEX_DEFAULT
        testViewModel.getCurrentAppTheme()

        val responseEvent = testViewModel.currentAppThemeLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isEqualTo(defaultThemeIndex)
    }

    @Test
    fun changeCurrentTheme_returnsNewThemeIndex() {
        val newThemeIndex = THEME_INDEX_DARK
        testViewModel.changeCurrentTheme(newThemeIndex)

        val responseEvent = testViewModel.currentAppThemeLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isEqualTo(newThemeIndex)
    }

    @Test
    fun safeSendVerificationEmailWithNoInternet_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.sendVerificationEmail()

        var responseEvent = testViewModel.sendVerificationEmailLiveData.getOrAwaitValue()
        while (responseEvent.peekContent().status == Status.LOADING) {
            responseEvent = testViewModel.sendVerificationEmailLiveData.getOrAwaitValue()
        }

        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun safeSendVerificationEmailInternet_returnsSuccess() {
        testViewModel.sendVerificationEmail()

        var responseEvent = testViewModel.sendVerificationEmailLiveData.getOrAwaitValue()
        while (responseEvent.peekContent().status == Status.LOADING) {
            responseEvent = testViewModel.sendVerificationEmailLiveData.getOrAwaitValue()
        }

        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun logoutUser_setsIsUserLoggedInToFalse() {
        testViewModel.logoutUser()

        val logOutResponse = testViewModel.logoutLiveData.getOrAwaitValue()
        val isUserLoggedIn = fakePreferencesRepository.isUserLoggedInSynchronously()

        assertThat(logOutResponse.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
        assertThat(isUserLoggedIn).isEqualTo(false)
    }
}