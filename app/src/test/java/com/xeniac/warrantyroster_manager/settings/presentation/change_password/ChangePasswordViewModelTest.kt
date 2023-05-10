package com.xeniac.warrantyroster_manager.settings.presentation.change_password

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
class ChangePasswordViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeUserRepository: FakeUserRepository

    private lateinit var testViewModel: ChangePasswordViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        testViewModel = ChangePasswordViewModel(fakeUserRepository)
    }

    @Test
    fun validateChangePasswordInputsWithBlankFields_returnsError() {
        testViewModel.validateChangePasswordInputs(
            currentPassword = "",
            newPassword = "",
            retypeNewPassword = ""
        )

        val responseEvent = testViewModel.validateInputsLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateChangePasswordInputsWithBlankCurrentPassword_returnsError() {
        val newPassword = "new_password"

        testViewModel.validateChangePasswordInputs(
            currentPassword = "",
            newPassword = newPassword,
            retypeNewPassword = newPassword
        )

        val responseEvent = testViewModel.validateInputsLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateChangePasswordInputsWithBlankNewPassword_returnsError() {
        testViewModel.validateChangePasswordInputs(
            currentPassword = "password",
            newPassword = "",
            retypeNewPassword = "new_password"
        )

        val responseEvent = testViewModel.validateInputsLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateChangePasswordInputsWithBlankRetypeNewPassword_returnsError() {
        testViewModel.validateChangePasswordInputs(
            currentPassword = "password",
            newPassword = "new_password",
            retypeNewPassword = ""
        )

        val responseEvent = testViewModel.validateInputsLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateChangePasswordInputsWithShortNewPassword_returnsError() {
        val currentPassword = "password"
        val shortNewPassword = "1234"

        testViewModel.validateChangePasswordInputs(
            currentPassword = currentPassword,
            newPassword = shortNewPassword,
            retypeNewPassword = shortNewPassword
        )

        val responseEvent = testViewModel.validateInputsLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateChangePasswordInputsWithInvalidRetypePassword_returnsError() {
        testViewModel.validateChangePasswordInputs(
            currentPassword = "password",
            newPassword = "new_password",
            retypeNewPassword = "invalid_new_password"
        )

        val responseEvent = testViewModel.validateInputsLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateChangePasswordInputsWithValidInputs_returnsSuccess() {
        val email = "email@test.com"
        val currentPassword = "password"
        val newPassword = "new_password"

        fakeUserRepository.addUser(email, currentPassword)
        testViewModel.validateChangePasswordInputs(
            currentPassword = currentPassword,
            newPassword = newPassword,
            retypeNewPassword = newPassword
        )

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
    fun changeUserPasswordWithNoInternet_returnsError() {
        val email = "email@test.com"
        val password = "password"

        fakeUserRepository.addUser(email, password)
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.changeUserPassword("new_password")

        val responseEvent = testViewModel.changeUserPasswordLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun changeUserPasswordWithValidInputs_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"
        val newPassword = "new_password"

        fakeUserRepository.addUser(email, password)
        testViewModel.changeUserPassword(newPassword)

        val responseEvent = testViewModel.changeUserPasswordLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }
}