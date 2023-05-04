package com.xeniac.warrantyroster_manager.authentication.presentation.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.util.Constants
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
    fun getCurrentAppLanguage_returnsDefaultAppLanguage() {
        val defaultAppLanguage = Constants.LOCALE_TAG_ENGLISH_UNITED_STATES

        testViewModel.getCurrentAppLanguage()

        val responseEvent = testViewModel.currentAppLanguageLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
        assertThat(responseEvent.peekContent().data).isEqualTo(defaultAppLanguage)
    }

    @Test
    fun validateRegisterWithEmailInputsWithBlankFields_returnsError() {
        testViewModel.validateRegisterWithEmailInputs("email", "password", "")

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateRegisterWithEmailInputsWithBlankEmail_returnsError() {
        testViewModel.validateRegisterWithEmailInputs("", "password", "password")

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateRegisterWithEmailInputsWithBlankPassword_returnsError() {
        testViewModel.validateRegisterWithEmailInputs("email@test.com", "", "password")

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateRegisterWithEmailInputsWithBlankRetypePassword_returnsError() {
        testViewModel.validateRegisterWithEmailInputs("email@test.com", "password", "")

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateRegisterWithEmailInputsWithInvalidEmail_returnsError() {
        testViewModel.validateRegisterWithEmailInputs("email", "password", "password")

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateRegisterWithEmailInputsWithShortPassword_returnsError() {
        testViewModel.validateRegisterWithEmailInputs("email", "123", "123")

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateRegisterWithEmailInputsWithInvalidRetypePassword_returnsError() {
        testViewModel.validateRegisterWithEmailInputs("email", "password", "123")

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateRegisterWithEmailInputsWithValidInputs_returnsSuccess() {
        testViewModel.validateRegisterWithEmailInputs("email@test.com", "password", "password")

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun registerWithEmailWithExistingEmail_returnsError() {
        val email = "email@test.com"
        val password = "password"

        fakeUserRepository.addUser(email, password)
        testViewModel.registerWithEmail(email, password)

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun registerWithEmailValidInputsWithNoInternet_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.registerWithEmail("email@test.com", "password")

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun registerWithEmailWithValidInputs_returnsError() {
        testViewModel.registerWithEmail("email@test.com", "password")

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }
}