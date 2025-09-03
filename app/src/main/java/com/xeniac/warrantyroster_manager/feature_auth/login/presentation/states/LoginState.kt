package com.xeniac.warrantyroster_manager.feature_auth.login.presentation.states

import android.os.Parcelable
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.states.PasswordTextFieldState
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginState(
    val emailState: CustomTextFieldState = CustomTextFieldState(),
    val passwordState: PasswordTextFieldState = PasswordTextFieldState(),
    val isLoginWithEmailLoading: Boolean = false,
    val isLoginWithGoogleLoading: Boolean = false,
    val isLoginWithXLoading: Boolean = false,
    val isLoginWithGithubLoading: Boolean = false
) : Parcelable