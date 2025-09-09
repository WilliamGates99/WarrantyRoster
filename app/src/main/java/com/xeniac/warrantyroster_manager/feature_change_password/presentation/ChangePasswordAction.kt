package com.xeniac.warrantyroster_manager.feature_change_password.presentation

import androidx.compose.ui.text.input.TextFieldValue

sealed interface ChangePasswordAction {
    data object DismissPasswordChangedDialog : ChangePasswordAction

    data class CurrentPasswordChanged(val newValue: TextFieldValue) : ChangePasswordAction
    data class NewPasswordChanged(val newValue: TextFieldValue) : ChangePasswordAction
    data class ConfirmNewPasswordChanged(val newValue: TextFieldValue) : ChangePasswordAction

    data object ChangeUserPassword : ChangePasswordAction
}