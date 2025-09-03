package com.xeniac.warrantyroster_manager.feature_auth.register.presentation.states

import android.os.Parcelable
import com.xeniac.warrantyroster_manager.core.presentation.common.states.ConfirmPasswordTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.states.PasswordTextFieldState
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterState(
    val emailState: CustomTextFieldState = CustomTextFieldState(),
    val passwordState: PasswordTextFieldState = PasswordTextFieldState(),
    val confirmPasswordState: ConfirmPasswordTextFieldState = ConfirmPasswordTextFieldState(),
    val isRegisterWithEmailLoading: Boolean = false,
    val isLoginWithGoogleLoading: Boolean = false,
    val isLoginWithXLoading: Boolean = false,
    val isLoginWithGithubLoading: Boolean = false
) : Parcelable