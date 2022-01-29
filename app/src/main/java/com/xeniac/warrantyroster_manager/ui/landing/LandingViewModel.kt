package com.xeniac.warrantyroster_manager.ui.landing

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.WarrantyRosterApplication
import com.xeniac.warrantyroster_manager.repositories.LandingRepository
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import com.xeniac.warrantyroster_manager.utils.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LandingViewModel(
    application: Application,
    private val landingRepository: LandingRepository
) : AndroidViewModel(application) {

    val registerLiveData: MutableLiveData<Resource<Any>> = MutableLiveData()
    val loginLiveData: MutableLiveData<Resource<Any>> = MutableLiveData()
    val forgotPwLiveData: MutableLiveData<Resource<String>> = MutableLiveData()

    private val TAG = "LandingViewModel"

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
        if (registerLiveData.value != null) {
            registerLiveData.value = null
        }
        registerLiveData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection(getApplication<WarrantyRosterApplication>())) {
                landingRepository.registerViaEmail(email, password).await().apply {
                    user?.let {
                        Log.i(TAG, "${it.email} registered successfully.")
                        sendVerificationEmail(it)
                        registerLiveData.postValue(Resource.Success(null))
                    }
                }
            } else {
                registerLiveData.postValue(
                    Resource.Error("Unable to connect to the internet")
                )
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Exception: ${t.message}")
            registerLiveData.postValue(Resource.Error(t.message.toString()))
        }
    }

    private fun sendVerificationEmail(user: FirebaseUser) {
        try {
            landingRepository.sendVerificationEmail(user)
        } catch (t: Throwable) {
            Log.e(TAG, "Exception: ${t.message}")
        }
    }

    private suspend fun safeLoginViaEmail(email: String, password: String) {
        if (loginLiveData.value != null) {
            loginLiveData.value = null
        }
        loginLiveData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection(getApplication<WarrantyRosterApplication>())) {
                landingRepository.loginViaEmail(email, password).await().apply {
                    user?.let {
                        Log.i(TAG, "${it.email} logged in successfully.")
                        loginLiveData.postValue(Resource.Success(null))
                    }
                }
            } else {
                loginLiveData.postValue(
                    Resource.Error("Unable to connect to the internet")
                )
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Exception: ${t.message}")
            loginLiveData.postValue(Resource.Error(t.message.toString()))
        }
    }

    private suspend fun safeSendResetPasswordEmail(email: String) {
        if (forgotPwLiveData.value != null) {
            forgotPwLiveData.value = null
        }
        forgotPwLiveData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection(getApplication<WarrantyRosterApplication>())) {
                landingRepository.sendResetPasswordEmail(email).await().apply {
                    Log.i(TAG, "Reset password email successfully sent to ${email}.")
                    forgotPwLiveData.postValue(Resource.Success(email))
                }
            } else {
                forgotPwLiveData.postValue(
                    Resource.Error("Unable to connect to the internet")
                )
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Exception: ${t.message}")
            forgotPwLiveData.postValue(Resource.Error(t.message.toString()))
        }
    }
}