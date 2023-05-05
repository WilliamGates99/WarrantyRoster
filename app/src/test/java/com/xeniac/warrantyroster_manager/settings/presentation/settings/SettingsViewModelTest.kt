package com.xeniac.warrantyroster_manager.settings.presentation.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.core.domain.model.UserInfo
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Constants.THEME_INDEX_DARK
import com.xeniac.warrantyroster_manager.util.Constants.THEME_INDEX_DEFAULT
import com.xeniac.warrantyroster_manager.util.Resource
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
    private lateinit var fakePreferencesRepository: FakePreferencesRepository

    private lateinit var testViewModel: SettingsViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        fakePreferencesRepository = FakePreferencesRepository()

        testViewModel = SettingsViewModel(
            fakeUserRepository,
            fakePreferencesRepository
        )
    }

    @Test
    fun getCurrentAppLocaleIndex_returnsDefaultAppLocaleIndex() {
        val defaultAppLocaleIndex = LOCALE_INDEX_ENGLISH_UNITED_STATES

        testViewModel.getCurrentAppLocaleIndex()

        val responseEvent = testViewModel.currentAppLocaleIndexLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
        assertThat(responseEvent.peekContent().data).isEqualTo(defaultAppLocaleIndex)
    }

    @Test
    fun getCurrentAppLocaleUiText_returnsDefaultAppLocaleUiText() {
        testViewModel.getCurrentAppLocaleUiText()

        val responseEvent = testViewModel.currentAppLocaleUiTextLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun getCurrentAppThemeIndex_returnsDefaultAppThemeIndex() {
        val defaultAppThemeIndex = THEME_INDEX_DEFAULT

        testViewModel.getCurrentAppThemeIndex()

        val responseEvent = testViewModel.currentAppThemeIndexLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
        assertThat(responseEvent.peekContent().data).isEqualTo(defaultAppThemeIndex)
    }

    @Test
    fun getCurrentAppThemeUiText_returnsDefaultAppThemeUiText() {
        testViewModel.getCurrentAppThemeUiText()

        val responseEvent = testViewModel.currentAppThemeUiTextLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun changeCurrentAppLocale_returnsNewAppLocaleIndex() {
        val testIndex = LOCALE_INDEX_ENGLISH_GREAT_BRITAIN

        testViewModel.changeCurrentAppLocale(testIndex)
        testViewModel.getCurrentAppLocaleIndex()

        val responseEvent = testViewModel.currentAppLocaleIndexLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
        assertThat(responseEvent.peekContent().data).isEqualTo(testIndex)
    }

    @Test
    fun changeCurrentAppTheme_returnsNewAppThemeIndex() {
        val newThemeIndex = THEME_INDEX_DARK

        testViewModel.changeCurrentAppTheme(newThemeIndex)
        testViewModel.getCurrentAppThemeIndex()

        val responseEvent = testViewModel.currentAppThemeIndexLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
        assertThat(responseEvent.peekContent().data).isEqualTo(newThemeIndex)
    }

    @Test
    fun getCachedUserInfo_returnCachedUserInfo() {
        val email = "email@test.com"
        val password = "password"
        val isEmailVerified = false
        val cachedUserInfo = UserInfo("1", email, isEmailVerified)

        fakeUserRepository.addUser(email, password)
        testViewModel.getCachedUserInfo()

        val responseEvent = testViewModel.userInfoLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
        assertThat(responseEvent.peekContent().data).isEqualTo(cachedUserInfo)
    }

    @Test
    fun getReloadedUserInfo_returnReloadedUserInfo() {
        val email = "email@test.com"
        val password = "password"
        val isEmailVerified = false
        val cachedUserInfo = UserInfo("1", email, isEmailVerified)

        fakeUserRepository.addUser(email, password)
        testViewModel.getReloadedUserInfo()

        val responseEvent = testViewModel.userInfoLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
        assertThat(responseEvent.peekContent().data).isEqualTo(cachedUserInfo)
    }

    @Test
    fun sendVerificationEmailWithNoInternet_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.sendVerificationEmail()

        val responseEvent = testViewModel.sendVerificationEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun sendVerificationEmail_returnsSuccess() {
        testViewModel.sendVerificationEmail()

        val responseEvent = testViewModel.sendVerificationEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun logoutUser_setsIsUserLoggedInToFalse() {
        testViewModel.logoutUser()

        val logOutResponse = testViewModel.logoutLiveData.getOrAwaitValue()
        val isUserLoggedIn = fakePreferencesRepository.isUserLoggedInSynchronously()

        assertThat(logOutResponse.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
        assertThat(isUserLoggedIn).isFalse()
    }
}