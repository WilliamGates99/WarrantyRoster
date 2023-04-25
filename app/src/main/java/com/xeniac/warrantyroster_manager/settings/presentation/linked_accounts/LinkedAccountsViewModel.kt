package com.xeniac.warrantyroster_manager.settings.presentation.linked_accounts

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_ENGLISH
import com.xeniac.warrantyroster_manager.util.Event
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LinkedAccountsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentLanguageLiveData: MutableLiveData<Event<String>> = MutableLiveData()
    val currentLanguageLiveData: LiveData<Event<String>> = _currentLanguageLiveData

    private val _linkedAccountsLiveData: MutableLiveData<Event<Resource<List<String>>>> =
        MutableLiveData()
    val linkedAccountsLiveData: LiveData<Event<Resource<List<String>>>> = _linkedAccountsLiveData

    private val _linkGoogleAccountLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val linkGoogleAccountLiveData: LiveData<Event<Resource<Nothing>>> = _linkGoogleAccountLiveData

    private val _unlinkGoogleAccountLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val unlinkGoogleAccountLiveData: LiveData<Event<Resource<Nothing>>> =
        _unlinkGoogleAccountLiveData

    private val _linkTwitterAccountLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val linkTwitterAccountLiveData: LiveData<Event<Resource<Nothing>>> = _linkTwitterAccountLiveData

    private val _unlinkTwitterAccountLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val unlinkTwitterAccountLiveData:
            LiveData<Event<Resource<Nothing>>> = _unlinkTwitterAccountLiveData

    private val _linkFacebookAccountLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val linkFacebookAccountLiveData:
            LiveData<Event<Resource<Nothing>>> = _linkFacebookAccountLiveData

    private val _unlinkFacebookAccountLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val unlinkFacebookAccountLiveData:
            LiveData<Event<Resource<Nothing>>> = _unlinkFacebookAccountLiveData

    fun getCurrentAppLanguage() = viewModelScope.launch {
        safeGetCurrentAppLanguage()
    }

    private fun safeGetCurrentAppLanguage() {
        val localeList = AppCompatDelegate.getApplicationLocales()

        if (localeList.isEmpty) {
            _currentLanguageLiveData.postValue(Event(LOCALE_ENGLISH))
            Timber.i("Locale list is Empty. -> Current app language is $LOCALE_ENGLISH")
        } else {
            val currentLanguage = localeList[0]!!.language
            _currentLanguageLiveData.postValue(Event(currentLanguage))
            Timber.i("Current app language is $currentLanguage")
        }
    }

    fun getLinkedAccounts() = viewModelScope.launch {
        safeGetLinkedAccounts()
    }

    private suspend fun safeGetLinkedAccounts() {
        _linkedAccountsLiveData.postValue(Event(Resource.Loading()))
        try {
            val providerIds = userRepository.getCurrentUserProviderIds()
            _linkedAccountsLiveData.postValue(Event(Resource.Success(providerIds)))
            Timber.i("Linked Accounts IDs successfully retrieved: $providerIds")
        } catch (e: Exception) {
            Timber.e("safeGetLinkedAccounts Exception: ${e.message}")
            _linkedAccountsLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun linkGoogleAccount(account: GoogleSignInAccount) = viewModelScope.launch {
        safeLinkGoogleAccount(account)
    }

    private suspend fun safeLinkGoogleAccount(account: GoogleSignInAccount) {
        _linkGoogleAccountLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.linkGoogleAccount(account)
            _linkGoogleAccountLiveData.postValue(Event(Resource.Success()))
            Timber.i("Google account linked successfully.")
        } catch (e: Exception) {
            Timber.e("safeLinkGoogleAccount Exception: ${e.message}")
            _linkGoogleAccountLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun unlinkGoogleAccount() = viewModelScope.launch {
        safeUnlinkGoogleAccount()
    }

    private suspend fun safeUnlinkGoogleAccount() {
        _unlinkGoogleAccountLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.unlinkGoogleAccount()
            _unlinkGoogleAccountLiveData.postValue(Event(Resource.Success()))
            Timber.i("Google account unlinked successfully.")
        } catch (e: Exception) {
            Timber.e("safeUnlinkGoogleAccount Exception: ${e.message}")
            _unlinkGoogleAccountLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun linkTwitterAccount(credential: AuthCredential) = viewModelScope.launch {
        safeLinkTwitterAccount(credential)
    }

    private suspend fun safeLinkTwitterAccount(credential: AuthCredential) {
        _linkTwitterAccountLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.linkTwitterAccount(credential)
            _linkTwitterAccountLiveData.postValue(Event(Resource.Success()))
            Timber.i("Twitter account linked successfully.")
        } catch (e: Exception) {
            Timber.e("safeLinkTwitterAccount Exception: ${e.message}")
            _linkTwitterAccountLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun unlinkTwitterAccount() = viewModelScope.launch {
        safeUnlinkTwitterAccount()
    }

    private suspend fun safeUnlinkTwitterAccount() {
        _unlinkTwitterAccountLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.unlinkTwitterAccount()
            _unlinkTwitterAccountLiveData.postValue(Event(Resource.Success()))
            Timber.i("Twitter account unlinked successfully.")
        } catch (e: Exception) {
            Timber.e("safeUnlinkTwitterAccount Exception: ${e.message}")
            _unlinkTwitterAccountLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun linkFacebookAccount(accessToken: AccessToken) = viewModelScope.launch {
        safeLinkFacebookAccount(accessToken)
    }

    private suspend fun safeLinkFacebookAccount(accessToken: AccessToken) {
        _linkFacebookAccountLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.linkFacebookAccount(accessToken)
            _linkFacebookAccountLiveData.postValue(Event(Resource.Success()))
            Timber.i("Facebook account linked successfully.")
        } catch (e: Exception) {
            Timber.e("safeLinkFacebookAccount Exception: ${e.message}")
            _linkFacebookAccountLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun unlinkFacebookAccount() = viewModelScope.launch {
        safeUnlinkFacebookAccount()
    }

    private suspend fun safeUnlinkFacebookAccount() {
        _unlinkFacebookAccountLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.unlinkFacebookAccount()
            _unlinkFacebookAccountLiveData.postValue(Event(Resource.Success()))
            Timber.i("Facebook account unlinked successfully.")
        } catch (e: Exception) {
            Timber.e("safeUnlinkFacebookAccount Exception: ${e.message}")
            _unlinkFacebookAccountLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }
}