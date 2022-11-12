package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ChangeEmailViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeUserRepository: FakeUserRepository

    private lateinit var testViewModel: ChangeEmailViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        testViewModel = ChangeEmailViewModel(fakeUserRepository)
    }

    @Test
    fun reAuthenticateUserWithNoInternet_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.reAuthenticateUser("password")

        val responseEvent = testViewModel.reAuthenticateUserLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun reAuthenticateUserWithIncorrectPassword_returnsError() {
        fakeUserRepository.addUser("email@test.com", "password")
        testViewModel.reAuthenticateUser("wrong_password")

        val responseEvent = testViewModel.reAuthenticateUserLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun reAuthenticateUserWithCorrectPassword_returnsSuccess() {
        val password = "password"
        fakeUserRepository.addUser("email@test.com", password)
        testViewModel.reAuthenticateUser(password)

        val responseEvent = testViewModel.reAuthenticateUserLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun checkChangeEmailInputsWithBlankFields_returnsError() {
        fakeUserRepository.addUser("email@test.com", "password")
        testViewModel.validateChangeEmailInputs("", "new_email@test.com")

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkChangeEmailInputsWithInvalidEmail_returnsError() {
        val password = "password"
        fakeUserRepository.addUser("email@test.com", password)
        testViewModel.validateChangeEmailInputs(password, "email")

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkChangeEmailInputsWithSameEmail_returnsError() {
        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)
        testViewModel.validateChangeEmailInputs(password, email)

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun changeUserEmailWithNoInternet_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.changeUserEmail("new_email@test.com")

        val responseEvent = testViewModel.changeUserEmailLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun changeUserEmailWithValidInputs_returnsSuccess() {
        fakeUserRepository.addUser("email@test.com", "password")
        testViewModel.changeUserEmail("new_email@test.com")

        val responseEvent = testViewModel.changeUserEmailLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }
}