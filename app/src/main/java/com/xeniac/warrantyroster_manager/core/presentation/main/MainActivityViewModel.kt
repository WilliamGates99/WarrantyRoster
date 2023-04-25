package com.xeniac.warrantyroster_manager.core.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.PreferencesRepository
import com.xeniac.warrantyroster_manager.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _rateAppDialogChoiceLiveData: MutableLiveData<Event<Int>> = MutableLiveData()
    val rateAppDialogChoiceLiveData: LiveData<Event<Int>> = _rateAppDialogChoiceLiveData

    private val _previousRequestTimeInMillisLiveData:
            MutableLiveData<Event<Long>> = MutableLiveData()
    val previousRequestTimeInMillisLiveData:
            LiveData<Event<Long>> = _previousRequestTimeInMillisLiveData

    fun isUserLoggedIn() = preferencesRepository.isUserLoggedInSynchronously()

    fun getRateAppDialogChoice() = viewModelScope.launch {
        safeGetRateAppDialogChoice()
    }

    private suspend fun safeGetRateAppDialogChoice() {
        _rateAppDialogChoiceLiveData.postValue(Event(preferencesRepository.getRateAppDialogChoice()))
    }

    fun getPreviousRequestTimeInMillis() = viewModelScope.launch {
        safeGetPreviousRequestTimeInMillis()
    }

    private suspend fun safeGetPreviousRequestTimeInMillis() {
        _previousRequestTimeInMillisLiveData.postValue(Event(preferencesRepository.getPreviousRequestTimeInMillis()))
    }

    fun setRateAppDialogChoice(value: Int) = viewModelScope.launch {
        safeSetRateAppDialogChoice(value)
    }

    private suspend fun safeSetRateAppDialogChoice(value: Int) {
        preferencesRepository.setRateAppDialogChoice(value)
        _rateAppDialogChoiceLiveData.postValue(Event(value))
    }

    fun setPreviousRequestTimeInMillis() = viewModelScope.launch {
        safeSetPreviousRequestTimeInMillis()
    }

    private suspend fun safeSetPreviousRequestTimeInMillis() {
        preferencesRepository.setPreviousRequestTimeInMillis(Calendar.getInstance().timeInMillis)
    }
}