package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.authentication.presentation.register.RegisterViewModel
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RegisterViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeUserRepository: FakeUserRepository

    private lateinit var testViewModel: RegisterViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()

        testViewModel = RegisterViewModel(
            fakeUserRepository,
            FakePreferencesRepository()
        )
    }

    @Test
    fun checkRegisterInputsWithBlankFields_returnsError() {
        testViewModel.validateRegisterInputs("email", "password", "")

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkRegisterInputsWithInvalidEmail_returnsError() {
        testViewModel.validateRegisterInputs("email", "password", "password")

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkRegisterInputsWithShortPassword_returnsError() {
        testViewModel.validateRegisterInputs("email", "123", "123")

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkRegisterInputsWithInvalidRetypePassword_returnsError() {
        testViewModel.validateRegisterInputs("email", "password", "123")

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkRegisterInputsWithValidInputs_returnsSuccess() {
        testViewModel.validateRegisterInputs("email@test.com", "password", "password")

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun registerWithNoInternet_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.registerViaEmail("email@test.com", "password")

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun registerWithExistingEmail_returnsError() {
        val email = "email@test.com"
        val password = "password"
        testViewModel.registerViaEmail(email, password)
        testViewModel.registerViaEmail(email, password)

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }
}