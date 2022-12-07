package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LinkedAccountsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _linkedAccountsLiveData: MutableLiveData<Event<Resource<List<String>>>> =
        MutableLiveData()
    val linkedAccountsLiveData: LiveData<Event<Resource<List<String>>>> = _linkedAccountsLiveData

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
}