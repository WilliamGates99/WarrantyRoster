package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_COUNTRY_IRAN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_COUNTRY_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_ENGLISH
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_PERSIAN
import com.xeniac.warrantyroster_manager.utils.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeUserRepository: FakeUserRepository

    private lateinit var testViewModel: SettingsViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()

        testViewModel = SettingsViewModel(
            fakeUserRepository,
            FakePreferencesRepository()
        )
    }

    /*
    @Test
    fun getCurrentAppLocale_returnsArrayOfDefaultLocale() {
        val defaultLanguage = LOCALE_LANGUAGE_ENGLISH
        val defaultCountry = LOCALE_COUNTRY_UNITED_STATES
        val defaultLocale = arrayOf(defaultLanguage, defaultCountry)

        testViewModel.getCurrentAppLocale()

        val responseEvent = testViewModel.currentAppLocaleLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isEqualTo(defaultLocale)
    }

    @Test
    fun getCurrentAppTheme_returnsDefaultTheme() {
        val defaultThemeIndex = 0
        testViewModel.getCurrentAppTheme()

        val responseEvent = testViewModel.currentAppThemeLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isEqualTo(defaultThemeIndex)
    }

    @Test
    fun getRateAppDialogChoice_returnsDefaultRateAppDialogChoice() {
        val defaultRateAppDialogChoice = 0
        testViewModel.getRateAppDialogChoice()

        val responseEvent = testViewModel.rateAppDialogChoiceLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isEqualTo(defaultRateAppDialogChoice)
    }

    @Test
    fun getPreviousRequestTimeInMillis_returnsDefaultPreviousRequestTimeInMillis() {
        val defaultPreviousRequestTimeInMillis = 0L
        testViewModel.getPreviousRequestTimeInMillis()

        val responseEvent = testViewModel.previousRequestTimeInMillisLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled())
            .isEqualTo(defaultPreviousRequestTimeInMillis)
    }

    @Test
    fun setAppLocale_returnsNewAppLocale() {
        testViewModel.setAppLocale(2)

        val newLanguage = LOCALE_LANGUAGE_PERSIAN
        val newCountry = LOCALE_COUNTRY_IRAN
        val newLocale = arrayOf(newLanguage, newCountry)

        val responseEvent = testViewModel.setAppLocaleLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.data).isEqualTo(newLocale)
    }

    @Test
    fun setAppTheme_returnsNewAppTheme() {
        val newThemeIndex = 1
        testViewModel.setAppTheme(newThemeIndex)

        val responseEvent = testViewModel.currentAppThemeLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isEqualTo(newThemeIndex)
    }

    @Test
    fun setRateAppDialogChoice_returnsNewRateAppDialogChoice() {
        val newRateAppDialogChoice = 1
        testViewModel.setRateAppDialogChoice(newRateAppDialogChoice)

        val responseEvent = testViewModel.rateAppDialogChoiceLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isEqualTo(newRateAppDialogChoice)
    }

    @Test
    fun safeSendVerificationEmailWithNoInternet_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.sendVerificationEmail()

        var responseEvent = testViewModel.sendVerificationEmailLiveData.getOrAwaitValue()
        while (responseEvent.peekContent().status == Status.LOADING) {
            responseEvent = testViewModel.sendVerificationEmailLiveData.getOrAwaitValue()
        }

        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun safeSendVerificationEmailInternet_returnsSuccess() {
        testViewModel.sendVerificationEmail()

        var responseEvent = testViewModel.sendVerificationEmailLiveData.getOrAwaitValue()
        while (responseEvent.peekContent().status == Status.LOADING) {
            responseEvent = testViewModel.sendVerificationEmailLiveData.getOrAwaitValue()
        }

        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun logoutUser_setsIsUserLoggedInToFalse() {
        testViewModel.logoutUser()

        val logOutResponse = testViewModel.logoutLiveData.getOrAwaitValue()
        val isUserLoggedIn = testViewModel.isUserLoggedIn()

        assertThat(logOutResponse.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
        assertThat(isUserLoggedIn).isEqualTo(false)
    }

    @Test
    fun reAuthenticateUserWithNoInternet_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.reAuthenticateUser("password")

        val responseEvent = testViewModel.reAuthenticateUserLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun reAuthenticateUserWithIncorrectPassword_returnsError() {
        fakeUserRepository.addUser("email@test.com", "password")
        testViewModel.reAuthenticateUser("wrong_password")

        val responseEvent = testViewModel.reAuthenticateUserLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun reAuthenticateUserWithCorrectPassword_returnsSuccess() {
        val password = "password"
        fakeUserRepository.addUser("email@test.com", password)
        testViewModel.reAuthenticateUser(password)

        val responseEvent = testViewModel.reAuthenticateUserLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun checkChangeEmailInputsWithBlankFields_returnsError() {
        fakeUserRepository.addUser("email@test.com", "password")
        testViewModel.checkChangeEmailInputs("", "new_email@test.com")

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkChangeEmailInputsWithInvalidEmail_returnsError() {
        val password = "password"
        fakeUserRepository.addUser("email@test.com", password)
        testViewModel.checkChangeEmailInputs(password, "email")

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkChangeEmailInputsWithSameEmail_returnsError() {
        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)
        testViewModel.checkChangeEmailInputs(password, email)

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun changeUserEmailWithNoInternet_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.changeUserEmail("new_email@test.com")

        val responseEvent = testViewModel.changeUserEmailLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun changeUserEmailWithValidInputs_returnsSuccess() {
        fakeUserRepository.addUser("email@test.com", "password")
        testViewModel.changeUserEmail("new_email@test.com")

        val responseEvent = testViewModel.changeUserEmailLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun checkChangePasswordInputsWithBlankFields_returnsError() {
        fakeUserRepository.addUser("email@test.com", "password")
        testViewModel.checkChangePasswordInputs("", "", "")

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkChangePasswordInputsWithShortPassword_returnsError() {
        val password = "password"
        val shortPassword = "1234"
        fakeUserRepository.addUser("email@test.com", password)
        testViewModel.checkChangePasswordInputs(password, shortPassword, shortPassword)

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkChangePasswordInputsWitInvalidRetypePassword_returnsError() {
        val password = "password"
        fakeUserRepository.addUser("email@test.com", password)
        testViewModel.checkChangePasswordInputs(password, "new_password", "1234")

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun changeUserPasswordWithNoInternet_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.changeUserPassword("new_password")

        val responseEvent = testViewModel.changeUserPasswordLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun changeUserPasswordWithValidInputs_returnsSuccess() {
        fakeUserRepository.addUser("email@test.com", "password")
        testViewModel.changeUserPassword("new_password")

        val responseEvent = testViewModel.changeUserPasswordLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }
     */
}