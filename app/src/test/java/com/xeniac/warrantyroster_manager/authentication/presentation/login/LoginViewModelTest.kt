package com.xeniac.warrantyroster_manager.authentication.presentation.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Resource
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
    fun getCurrentAppLanguage_returnsDefaultAppLanguage() {
        val defaultAppLanguage = LOCALE_TAG_ENGLISH_UNITED_STATES

        testViewModel.getCurrentAppLanguage()

        val responseEvent = testViewModel.currentAppLanguageLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
        assertThat(responseEvent.peekContent().data).isEqualTo(defaultAppLanguage)
    }

    @Test
    fun validateLoginWithEmailInputsWithBlankFields_returnsError() {
        testViewModel.validateLoginWithEmailInputs("", "")

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateLoginWithEmailInputsWithBlankEmail_returnsError() {
        testViewModel.validateLoginWithEmailInputs("", "password")

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateLoginWithEmailInputsWithBlankPassword_returnsError() {
        testViewModel.validateLoginWithEmailInputs("email@test.com", "")

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateLoginWithEmailInputsWithInvalidEmail_returnsError() {
        testViewModel.validateLoginWithEmailInputs("email", "password")

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateLoginWithEmailInputsWithValidInputs_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"

        fakeUserRepository.addUser(email, password)
        testViewModel.validateLoginWithEmailInputs(email, password)

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun loginWithEmailWithNonExistingEmail_returnsError() {
        testViewModel.loginWithEmail("email@test.com", "password")

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun loginWithEmailWithIncorrectCredentials_returnsError() {
        val email = "email@test.com"

        fakeUserRepository.addUser(email, "password")
        testViewModel.loginWithEmail(email, "wrong_password")

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun loginWithEmailWithValidCredentialsWithNoInternet_returnsError() {
        val email = "email@test.com"
        val password = "password"

        fakeUserRepository.addUser(email, password)
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.loginWithEmail(email, password)

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun loginWithEmailWithValidCredentials_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"

        fakeUserRepository.addUser(email, password)
        testViewModel.loginWithEmail(email, password)

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }
}