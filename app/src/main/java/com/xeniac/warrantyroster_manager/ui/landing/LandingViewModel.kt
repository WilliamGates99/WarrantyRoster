package com.xeniac.warrantyroster_manager.ui.landing

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.BaseApplication
import com.xeniac.warrantyroster_manager.repositories.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_IS_LOGGED_IN_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_LOGIN
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import com.xeniac.warrantyroster_manager.utils.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LandingViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {

    private val _registerLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val registerLiveData: LiveData<Event<Resource<Nothing>>> = _registerLiveData

    private val _loginLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val loginLiveData: LiveData<Event<Resource<Nothing>>> = _loginLiveData

    private val _forgotPwLiveData: MutableLiveData<Event<Resource<String>>> = MutableLiveData()
    val forgotPwLiveData: LiveData<Event<Resource<String>>> = _forgotPwLiveData

    companion object {
        private const val TAG = "LandingViewModel"
    }

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

                getApplication<BaseApplication>()
                    .getSharedPreferences(PREFERENCE_LOGIN, Context.MODE_PRIVATE)
                    .edit().apply {
                        putBoolean(PREFERENCE_IS_LOGGED_IN_KEY, true)
                        apply()
                    }

                _registerLiveData.postValue(Event(Resource.success(null)))
                Log.i(TAG, "$email registered successfully.")
            } else {
                Log.e(TAG, ERROR_NETWORK_CONNECTION)
                _registerLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "SafeRegisterViaEmail Exception: ${e.message}")
            _registerLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeLoginViaEmail(email: String, password: String) {
        _loginLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                userRepository.loginViaEmail(email, password).await().apply {
                    user?.let {
                        getApplication<BaseApplication>()
                            .getSharedPreferences(PREFERENCE_LOGIN, Context.MODE_PRIVATE)
                            .edit().apply {
                                putBoolean(PREFERENCE_IS_LOGGED_IN_KEY, true)
                                apply()
                            }

                        _loginLiveData.postValue(Event(Resource.success(null)))
                    }
                }
            } else {
                Log.e(TAG, ERROR_NETWORK_CONNECTION)
                _loginLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "SafeLoginViaEmail Exception: ${e.message}")
            _loginLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeSendResetPasswordEmail(email: String) {
        _forgotPwLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                userRepository.sendResetPasswordEmail(email).await().apply {
                    Log.i(TAG, "Reset password email successfully sent to ${email}.")
                    _forgotPwLiveData.postValue(Event(Resource.success(email)))
                }
            } else {
                Log.e(TAG, ERROR_NETWORK_CONNECTION)
                _forgotPwLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "SafeSendResetPasswordEmail Exception: ${e.message}")
            _forgotPwLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }
}