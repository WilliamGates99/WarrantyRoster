package com.xeniac.warrantyroster_manager.ui.landing.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.di.TestPreferencesRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.repositories.PreferencesRepository
import com.xeniac.warrantyroster_manager.repositories.FakeUserRepository
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class LandingViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    @TestPreferencesRepository
    lateinit var preferencesRepository: PreferencesRepository

    private lateinit var testViewModel: LandingViewModel

    @Before
    fun setUp() {
        hiltRule.inject()

        testViewModel = LandingViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeUserRepository(),
            preferencesRepository
        )
    }

    @After
    fun tearDown() {

    }

    @Test
    fun checkRegisterInputsWithBlankFields_returnsError() {
        testViewModel.checkRegisterInputs("email", "password", "")

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkRegisterInputsWithInvalidEmail_returnsError() {
        testViewModel.checkRegisterInputs("email", "password", "password")

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkRegisterInputsWithShortPassword_returnsError() {
        testViewModel.checkRegisterInputs("email", "123", "123")

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkRegisterInputsWithInvalidRetypePassword_returnsError() {
        testViewModel.checkRegisterInputs("email", "password", "123")

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkRegisterInputsWithValidInputs_returnsSuccess() {
        testViewModel.checkRegisterInputs("email@test.com", "password", "password")

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun checkLoginInputsWithBlankFields_returnsError() {
        testViewModel.checkLoginInputs("email", "")

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkLoginInputsWithInvalidEmail_returnsError() {
        testViewModel.checkLoginInputs("email", "password")

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkForgotPwInputsWithBlankFields_returnsError() {
        testViewModel.checkForgotPwInputs("")

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkForgotPwInputsWithInvalidEmail_returnsError() {
        testViewModel.checkForgotPwInputs("email")

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }
}