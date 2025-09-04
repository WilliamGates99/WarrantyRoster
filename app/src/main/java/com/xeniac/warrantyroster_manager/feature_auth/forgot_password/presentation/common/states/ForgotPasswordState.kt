package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.states

import android.os.Parcelable
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import kotlinx.parcelize.Parcelize

@Parcelize
data class ForgotPasswordState(
    val emailState: CustomTextFieldState = CustomTextFieldState(),
    val sentResetPasswordEmailsCount: Int = 1,
    val isTimerTicking: Boolean = true,
    val isSendResetPasswordEmailLoading: Boolean = false
) : Parcelable