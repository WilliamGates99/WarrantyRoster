package com.xeniac.warrantyroster_manager.ui.landing

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.BaseApplication
import com.xeniac.warrantyroster_manager.di.LoginPrefs
import com.xeniac.warrantyroster_manager.repositories.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_IS_LOGGED_IN_KEY
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import com.xeniac.warrantyroster_manager.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository,
    @LoginPrefs private val loginPrefs: SharedPreferences
) : AndroidViewModel(application) {

    private val _registerLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val registerLiveData: LiveData<Event<Resource<Nothing>>> = _registerLiveData

    private val _loginLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val loginLiveData: LiveData<Event<Resource<Nothing>>> = _loginLiveData

    private val _forgotPwLiveData: MutableLiveData<Event<Resource<String>>> = MutableLiveData()
    val forgotPwLiveData: LiveData<Event<Resource<String>>> = _forgotPwLiveData

    fun registerViaEmail(email: String, password: String) = viewModelScope.launch {
        safeRegisterViaEmail(email, password)
    }

    fun loginViaEmail(email: String, password: String) = viewModelScope.launch {
        safeLoginViaEmail(email, password)
    }

    fun sendResetPasswordEmail(email: String) = viewModelScope.launch {
        safeSendResetPasswordEmail(email)
    }

    private suspend fun safeRegisterViaEmail(email: String, password: String) {
        _registerLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                userRepository.registerViaEmail(email, password).await()
                userRepository.sendVerificationEmail()
                loginPrefs.edit().putBoolean(PREFERENCE_IS_LOGGED_IN_KEY, true).apply()
                _registerLiveData.postValue(Event(Resource.success(null)))
                Timber.i("$email registered successfully.")
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _registerLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Timber.e("SafeRegisterViaEmail Exception: ${e.message}")
            _registerLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeLoginViaEmail(email: String, password: String) {
        _loginLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                userRepository.loginViaEmail(email, password).await().apply {
                    user?.let {
                        loginPrefs.edit().putBoolean(PREFERENCE_IS_LOGGED_IN_KEY, true).apply()
                        _loginLiveData.postValue(Event(Resource.success(null)))
                        Timber.i("$email logged in successfully.")
                    }
                }
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _loginLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Timber.e("SafeLoginViaEmail Exception: ${e.message}")
            _loginLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeSendResetPasswordEmail(email: String) {
        _forgotPwLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                userRepository.sendResetPasswordEmail(email).await().apply {
                    _forgotPwLiveData.postValue(Event(Resource.success(email)))
                    Timber.i("Reset password email successfully sent to ${email}.")
                }
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _forgotPwLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Timber.e("SafeSendResetPasswordEmail Exception: ${e.message}")
            _forgotPwLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }
}