package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    private val _linkGoogleLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val linkGoogleLiveData: LiveData<Event<Resource<Nothing>>> = _linkGoogleLiveData

    private val _linkTwitterLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val linkTwitterLiveData: LiveData<Event<Resource<Nothing>>> = _linkTwitterLiveData

    private val _linkFacebookLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val linkFacebookLiveData: LiveData<Event<Resource<Nothing>>> = _linkFacebookLiveData

    fun getCurrentAppLanguage() = viewModelScope.launch {
        safeGetCurrentAppLanguage()
    }

    private fun safeGetCurrentAppLanguage() {
        val localeList = AppCompatDelegate.getApplicationLocales()

        if (localeList.isEmpty) {
            Timber.i("Locale list is Empty.")
        } else {
            val currentLanguage = localeList[0]!!.language
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
        _linkGoogleLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.linkGoogleAccount(account)
            _linkGoogleLiveData.postValue(Event(Resource.Success()))
            Timber.i("Google account linked successfully.")
        } catch (e: Exception) {
            Timber.e("safeLinkGoogleAccount Exception: ${e.message}")
            _linkGoogleLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun linkTwitterAccount(credential: AuthCredential) = viewModelScope.launch {
        safeLinkTwitterAccount(credential)
    }

    private suspend fun safeLinkTwitterAccount(credential: AuthCredential) {
        _linkTwitterLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.linkTwitterAccount(credential)
            _linkTwitterLiveData.postValue(Event(Resource.Success()))
            Timber.i("Twitter account linked successfully.")
        } catch (e: Exception) {
            Timber.e("safeLinkTwitterAccount Exception: ${e.message}")
            _linkTwitterLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun linkFacebookAccount() = viewModelScope.launch {
        safeLinkFacebookAccount()
    }

    private suspend fun safeLinkFacebookAccount() {
        _linkFacebookLiveData.postValue(Event(Resource.Loading()))
        try {
            delay(3000)
            _linkFacebookLiveData.postValue(Event(Resource.Success()))
            Timber.i("Facebook account linked successfully.")
        } catch (e: Exception) {
            Timber.e("safeLinkFacebookAccount Exception: ${e.message}")
            _linkFacebookLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }
}