package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common

import androidx.compose.ui.text.input.TextFieldValue

sealed interface ForgotPasswordAction {
    data class EmailChanged(val newValue: TextFieldValue) : ForgotPasswordAction

    data object SendResetPasswordEmail : ForgotPasswordAction
}