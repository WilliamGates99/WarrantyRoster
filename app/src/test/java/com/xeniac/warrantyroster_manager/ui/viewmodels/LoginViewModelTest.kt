package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeUserRepository: FakeUserRepository

    private lateinit var testViewModel: LoginViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()

        testViewModel = LoginViewModel(
            fakeUserRepository,
            FakePreferencesRepository()
        )
    }

    @Test
    fun checkLoginInputsWithBlankFields_returnsError() {
        testViewModel.validateLoginInputs("email", "")

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkLoginInputsWithInvalidEmail_returnsError() {
        testViewModel.validateLoginInputs("email", "password")

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkLoginInputsWithValidInputs_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)
        testViewModel.validateLoginInputs(email, password)

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun loginWithNoInternet_returnsError() {
        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.loginViaEmail(email, password)

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun loginWithIncorrectCredentials_returnsError() {
        val email = "email@test.com"
        fakeUserRepository.addUser(email, "password")
        testViewModel.loginViaEmail(email, "wrong_password")

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun loginWithNonExistingEmail_returnsError() {
        testViewModel.loginViaEmail("email@test.com", "password")

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }
}