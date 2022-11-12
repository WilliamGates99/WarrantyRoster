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
    fun checkChangePasswordInputsWithBlankFields_returnsError() {
        fakeUserRepository.addUser("email@test.com", "password")
        testViewModel.validateChangePasswordInputs("", "", "")

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkChangePasswordInputsWithShortPassword_returnsError() {
        val password = "password"
        val shortPassword = "1234"
        fakeUserRepository.addUser("email@test.com", password)
        testViewModel.validateChangePasswordInputs(password, shortPassword, shortPassword)

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkChangePasswordInputsWitInvalidRetypePassword_returnsError() {
        val password = "password"
        fakeUserRepository.addUser("email@test.com", password)
        testViewModel.validateChangePasswordInputs(password, "new_password", "1234")

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun changeUserPasswordWithNoInternet_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.changeUserPassword("new_password")

        val responseEvent = testViewModel.changeUserPasswordLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun changeUserPasswordWithValidInputs_returnsSuccess() {
        fakeUserRepository.addUser("email@test.com", "password")
        testViewModel.changeUserPassword("new_password")

        val responseEvent = testViewModel.changeUserPasswordLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }
}