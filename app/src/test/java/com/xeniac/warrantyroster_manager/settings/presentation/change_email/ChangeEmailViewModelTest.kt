package com.xeniac.warrantyroster_manager.settings.presentation.change_email

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.util.Resource
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
    fun validateChangeEmailInputsWithBlankFields_returnsError() {
        testViewModel.validateChangeEmailInputs("", "")

        val responseEvent = testViewModel.validateInputsLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateChangeEmailInputsWithBlankPassword_returnsError() {
        testViewModel.validateChangeEmailInputs("", "email@test.com")

        val responseEvent = testViewModel.validateInputsLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateChangeEmailInputsWithBlankNewEmail_returnsError() {
        testViewModel.validateChangeEmailInputs("password", "")

        val responseEvent = testViewModel.validateInputsLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateChangeEmailInputsWithInvalidNewEmail_returnsError() {
        testViewModel.validateChangeEmailInputs("password", "email")

        val responseEvent = testViewModel.validateInputsLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateChangeEmailInputsWithSimilarNewEmail_returnsError() {
        val email = "email@test.com"
        val password = "password"

        fakeUserRepository.addUser(email, password)
        testViewModel.validateChangeEmailInputs(password, email)

        val responseEvent = testViewModel.validateInputsLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateChangeEmailInputsWithValidInputs_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"
        val newEmail = "new_email@test.com"

        fakeUserRepository.addUser(email, password)
        testViewModel.validateChangeEmailInputs(password, newEmail)

        val responseEvent = testViewModel.validateInputsLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun reAuthenticateUserWithIncorrectCredentials_returnsError() {
        fakeUserRepository.addUser("email@test.com", "password")
        testViewModel.reAuthenticateUser("wrong_password")

        val responseEvent = testViewModel.reAuthenticateUserLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun reAuthenticateUserWithValidCredentialsWithNoInternet_returnsError() {
        val email = "email@test.com"
        val password = "password"

        fakeUserRepository.addUser(email, password)
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.reAuthenticateUser(password)

        val responseEvent = testViewModel.reAuthenticateUserLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun reAuthenticateUserWithValidCredentials_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"

        fakeUserRepository.addUser(email, password)
        testViewModel.reAuthenticateUser(password)

        val responseEvent = testViewModel.reAuthenticateUserLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun changeUserEmailWithNoInternet_returnsError() {
        val email = "email@test.com"
        val password = "password"

        fakeUserRepository.addUser(email, password)
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.changeUserEmail("new_email@test.com")

        val responseEvent = testViewModel.changeUserEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun changeUserEmailWithValidInputs_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"
        val newEmail = "new_email@test.com"

        fakeUserRepository.addUser(email, password)
        testViewModel.changeUserEmail(newEmail)

        val responseEvent = testViewModel.changeUserEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }
}