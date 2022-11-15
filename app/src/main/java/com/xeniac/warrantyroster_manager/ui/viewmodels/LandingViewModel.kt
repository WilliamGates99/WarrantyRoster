package com.xeniac.warrantyroster_manager.ui.viewmodels

import android.os.Build
import android.util.LayoutDirection
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.text.layoutDirection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class LandingViewModel : ViewModel() {

    private val _currentLocaleIndexLiveData: MutableLiveData<Event<Int>> = MutableLiveData()
    val currentLocaleIndexLiveData: LiveData<Event<Int>> = _currentLocaleIndexLiveData

    private val _changeCurrentLocaleLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val changeCurrentLocaleLiveData: LiveData<Event<Boolean>> = _changeCurrentLocaleLiveData

    fun getCurrentLanguage() = viewModelScope.launch {
        safeGetCurrentLanguage()
    }

    private fun safeGetCurrentLanguage() {
        val localeList = AppCompatDelegate.getApplicationLocales()

        if (localeList.isEmpty) {
            changeCurrentLocale(0)
            Timber.i("Locale list is Empty.")
        } else {
            val localeString = localeList[0].toString()
            Timber.i("Current language is $localeString")

            when (localeString) {
                "en_US" -> {
                    _currentLocaleIndexLiveData.postValue(Event(LOCALE_INDEX_ENGLISH_UNITED_STATES))
                    Timber.i("Current locale index is 0 (en_US).")
                }
                "en_GB" -> {
                    _currentLocaleIndexLiveData.postValue(Event(LOCALE_INDEX_ENGLISH_GREAT_BRITAIN))
                    Timber.i("Current locale index is 1 (en_GB).")
                }
                "fa_IR" -> {
                    _currentLocaleIndexLiveData.postValue(Event(LOCALE_INDEX_PERSIAN_IRAN))
                    Timber.i("Current locale index is 2 (fa_IR).")
                }
                else -> {
                    changeCurrentLocale(0)
                    Timber.i("Current language is System Default.")
                }
            }
        }
    }

    fun changeCurrentLocale(index: Int) = viewModelScope.launch {
        safeChangeCurrentLocale(index)
    }

    private fun safeChangeCurrentLocale(index: Int) {
        var isActivityRestartNeeded = false

        when (index) {
            0 -> {
                isActivityRestartNeeded = isActivityRestartNeeded(LayoutDirection.LTR)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(LOCALE_ENGLISH_UNITED_STATES)
                )
            }
            1 -> {
                isActivityRestartNeeded = isActivityRestartNeeded(LayoutDirection.LTR)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(LOCALE_ENGLISH_GREAT_BRITAIN)
                )
            }
            2 -> {
                isActivityRestartNeeded = isActivityRestartNeeded(LayoutDirection.RTL)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(LOCALE_PERSIAN_IRAN)
                )
            }
        }

        _changeCurrentLocaleLiveData.postValue(Event(isActivityRestartNeeded))
        Timber.i("isActivityRestartNeeded = $isActivityRestartNeeded}")
        Timber.i("App locale index changed to $index")
    }

    private fun isActivityRestartNeeded(newLayoutDirection: Int): Boolean {
        val currentLocale = AppCompatDelegate.getApplicationLocales()[0]
        val currentLayoutDirection = currentLocale?.layoutDirection

        return if (Build.VERSION.SDK_INT >= 33) {
            false
        } else currentLayoutDirection != newLayoutDirection
    }
}