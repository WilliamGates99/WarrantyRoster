package com.xeniac.warrantyroster_manager.settings.presentation.linked_accounts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_GOOGLE
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_TWITTER
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LinkedAccountsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeUserRepository: FakeUserRepository

    private lateinit var testViewModel: LinkedAccountsViewModel

    private val email = "email@test.com"
    private val password = "password"

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()

        testViewModel = LinkedAccountsViewModel(
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
    fun getLinkedAccounts_returnsUsersProviderData() {
        val providerIds = mutableListOf(
            FIREBASE_AUTH_PROVIDER_ID_GOOGLE,
            FIREBASE_AUTH_PROVIDER_ID_TWITTER
        )

        fakeUserRepository.addUser(
            email = email,
            password = password,
            providerIds = providerIds
        )
        testViewModel.getLinkedAccounts()

        val responseEvent = testViewModel.linkedAccountsLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
        assertThat(responseEvent.peekContent().data).isEqualTo(providerIds)
    }

    @Test
    fun unlinkGoogleAccountWithNoLinkedGoogleAccount_returnsError() {
        fakeUserRepository.addUser(
            email = email,
            password = password
        )
        testViewModel.unlinkGoogleAccount()

        val responseEvent = testViewModel.unlinkGoogleAccountLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun unlinkGoogleAccountWithLinkedGoogleAccountWithNoInternet_returnsError() {
        fakeUserRepository.addUser(
            email = email,
            password = password,
            providerIds = mutableListOf(FIREBASE_AUTH_PROVIDER_ID_GOOGLE)
        )
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.unlinkGoogleAccount()

        val responseEvent = testViewModel.unlinkGoogleAccountLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun unlinkGoogleAccountWithLinkedGoogleAccount_returnsSuccess() {
        fakeUserRepository.addUser(
            email = email,
            password = password,
            providerIds = mutableListOf(FIREBASE_AUTH_PROVIDER_ID_GOOGLE)
        )
        testViewModel.unlinkGoogleAccount()

        val responseEvent = testViewModel.unlinkGoogleAccountLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun unlinkTwitterAccountWithNoLinkedTwitterAccount_returnsError() {
        fakeUserRepository.addUser(
            email = email,
            password = password
        )
        testViewModel.unlinkTwitterAccount()

        val responseEvent = testViewModel.unlinkTwitterAccountLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun unlinkTwitterAccountWithLinkedTwitterAccountWithNoInternet_returnsError() {
        fakeUserRepository.addUser(
            email = email,
            password = password,
            providerIds = mutableListOf(FIREBASE_AUTH_PROVIDER_ID_TWITTER)
        )
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.unlinkTwitterAccount()

        val responseEvent = testViewModel.unlinkTwitterAccountLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun unlinkTwitterAccountWithLinkedTwitterAccount_returnsSuccess() {
        fakeUserRepository.addUser(
            email = email,
            password = password,
            providerIds = mutableListOf(FIREBASE_AUTH_PROVIDER_ID_TWITTER)
        )
        testViewModel.unlinkTwitterAccount()

        val responseEvent = testViewModel.unlinkTwitterAccountLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun unlinkFacebookAccountWithNoLinkedFacebookAccount_returnsError() {
        fakeUserRepository.addUser(
            email = email,
            password = password
        )
        testViewModel.unlinkFacebookAccount()

        val responseEvent = testViewModel.unlinkFacebookAccountLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun unlinkFacebookAccountWithLinkedFacebookAccountWithNoInternet_returnsError() {
        fakeUserRepository.addUser(
            email = email,
            password = password,
            providerIds = mutableListOf(FIREBASE_AUTH_PROVIDER_ID_FACEBOOK)
        )
        fakeUserRepository.setShouldReturnNetworkError(true)
        testViewModel.unlinkFacebookAccount()

        val responseEvent = testViewModel.unlinkFacebookAccountLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun unlinkFacebookAccountWithLinkedFacebookAccount_returnsSuccess() {
        fakeUserRepository.addUser(
            email = email,
            password = password,
            providerIds = mutableListOf(FIREBASE_AUTH_PROVIDER_ID_FACEBOOK)
        )
        testViewModel.unlinkFacebookAccount()

        val responseEvent = testViewModel.unlinkFacebookAccountLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
    }
}