package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.authentication.presentation.forgot_password.ForgotPwViewModel
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ForgotPwViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeUserRepository: FakeUserRepository

    private lateinit var testViewModel: ForgotPwViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        testViewModel = ForgotPwViewModel(fakeUserRepository)
    }

    @Test
    fun checkForgotPwInputsWithBlankFields_returnsError() {
        testViewModel.validateSendResetPasswordEmailInputs("", false)

        val responseEvent = testViewModel.sendResetPasswordEmailLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkForgotPwInputsWithInvalidEmail_returnsError() {
        testViewModel.validateSendResetPasswordEmailInputs("email", false)

        val responseEvent = testViewModel.sendResetPasswordEmailLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkForgotPwInputsWithValidInputs_returnsSuccess() = runTest {
        val email = "email@test.com"
        fakeUserRepository.addUser(email, "password")
        testViewModel.validateSendResetPasswordEmailInputs(email, false)

        val responseEvent = testViewModel.sendResetPasswordEmailLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun sendResetPasswordEmailWithNoInternet_returnsError() {
        val email = "email@test.com"
        fakeUserRepository.addUser(email, "password")

        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.sendResetPasswordEmail(email, false)

        val responseEvent = testViewModel.sendResetPasswordEmailLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun sendResetPasswordEmailWithNonExistingEmail_returnsError() {
        testViewModel.sendResetPasswordEmail("email@test.com", false)

        val responseEvent = testViewModel.sendResetPasswordEmailLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun sendResetPasswordEmailWhileTimerIsNotZero_returnsError() {
        val email = "email@test.com"
        testViewModel.forgotPwEmail = email
        testViewModel.timerMillisUntilFinished = 1L
        fakeUserRepository.addUser(email, "password")
        testViewModel.sendResetPasswordEmail(email, false)

        val responseEvent = testViewModel.sendResetPasswordEmailLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }
}