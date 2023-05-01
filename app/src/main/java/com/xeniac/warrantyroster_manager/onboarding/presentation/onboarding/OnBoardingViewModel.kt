package com.xeniac.warrantyroster_manager.onboarding.presentation.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_DEFAULT_OR_EMPTY
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Event
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _currentAppLocaleIndexLiveData: MutableLiveData<Event<Resource<Int>>> =
        MutableLiveData()
    val currentAppLocaleIndexLiveData: LiveData<Event<Resource<Int>>> =
        _currentAppLocaleIndexLiveData

    private val _changeCurrentAppLocaleLiveData: MutableLiveData<Event<Resource<Boolean>>> =
        MutableLiveData()
    val changeCurrentAppLocaleLiveData: LiveData<Event<Resource<Boolean>>> =
        _changeCurrentAppLocaleLiveData

    fun getCurrentAppLocaleIndex() = viewModelScope.launch {
        safeGetCurrentAppLocaleIndex()
    }

    private suspend fun safeGetCurrentAppLocaleIndex() {
        _currentAppLocaleIndexLiveData.postValue(Event(Resource.Loading()))
        try {
            val localeIndex = preferencesRepository.getCurrentAppLocaleIndex()
            Timber.i("Current locale index is $localeIndex")

            if (localeIndex == LOCALE_INDEX_DEFAULT_OR_EMPTY) {
                changeCurrentAppLocale(LOCALE_INDEX_ENGLISH_UNITED_STATES)
            } else {
                _currentAppLocaleIndexLiveData.postValue(Event(Resource.Success(localeIndex)))
            }
        } catch (e: Exception) {
            Timber.e("safeGetCurrentAppLocaleIndex Exception: ${e.message}")
            _currentAppLocaleIndexLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun changeCurrentAppLocale(index: Int) = viewModelScope.launch {
        safeChangeCurrentAppLocale(index)
    }

    private suspend fun safeChangeCurrentAppLocale(index: Int) {
        _currentAppLocaleIndexLiveData.postValue(Event(Resource.Loading()))
        try {
            val isActivityRestartNeeded = preferencesRepository.setCurrentAppLocale(index)
            _changeCurrentAppLocaleLiveData.postValue(Event(Resource.Success(isActivityRestartNeeded)))
            Timber.i("App locale index changed to $index and isActivityRestartNeeded = $isActivityRestartNeeded}")
        } catch (e: Exception) {
            Timber.e("safeChangeCurrentAppLocale Exception: ${e.message}")
            _changeCurrentAppLocaleLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }
}