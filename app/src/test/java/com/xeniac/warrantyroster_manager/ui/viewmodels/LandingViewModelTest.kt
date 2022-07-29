package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.utils.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LandingViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeUserRepository: FakeUserRepository

    private lateinit var testViewModel: LandingViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()

        testViewModel = LandingViewModel(
            fakeUserRepository,
            FakePreferencesRepository()
        )
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
    fun registerWithNoInternet_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.registerViaEmail("email@test.com", "password")

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun registerWithExistingEmail_returnsError() {
        val email = "email@test.com"
        val password = "password"
        testViewModel.registerViaEmail(email, password)
        testViewModel.registerViaEmail(email, password)

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
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
    fun checkLoginInputsWithValidInputs_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"
        testViewModel.registerViaEmail(email, password)
        testViewModel.checkLoginInputs(email, password)

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun loginWithNoInternet_returnsError() {
        val email = "email@test.com"
        val password = "password"
        testViewModel.registerViaEmail(email, password)

        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.loginViaEmail(email, password)

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun loginWithIncorrectCredentials_returnsError() {
        val email = "email@test.com"
        testViewModel.registerViaEmail(email, "password")
        testViewModel.loginViaEmail(email, "wrong_password")

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun loginWithNonExistingEmail_returnsError() {
        testViewModel.loginViaEmail("email@test.com", "password")

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkForgotPwInputsWithBlankFields_returnsError() {
        testViewModel.checkForgotPwInputs("", false)

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkForgotPwInputsWithInvalidEmail_returnsError() {
        testViewModel.checkForgotPwInputs("email", false)

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkForgotPwInputsWithValidInputs_returnsSuccess() = runTest {
        val email = "email@test.com"
        testViewModel.registerViaEmail(email, "password")
        testViewModel.checkForgotPwInputs(email, false)

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun sendResetPasswordEmailWithNoInternet_returnsError() {
        val email = "email@test.com"
        testViewModel.registerViaEmail(email, "password")

        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.sendResetPasswordEmail(email, false)

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun sendResetPasswordEmailWithNonExistingEmail_returnsError() {
        testViewModel.sendResetPasswordEmail("email@test.com", false)

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun sendResetPasswordEmailWhileTimerIsNotZero_returnsError() {
        val email = "email@test.com"
        testViewModel.forgotPwEmail = email
        testViewModel.timerInMillis = 1L
        testViewModel.registerViaEmail(email, "password")
        testViewModel.sendResetPasswordEmail(email, false)

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }
}