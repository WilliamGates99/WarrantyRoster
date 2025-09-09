package com.xeniac.warrantyroster_manager.feature_change_email.presentation

import androidx.compose.ui.text.input.TextFieldValue

sealed interface ChangeEmailAction {
    data object DismissEmailChangedSuccessfullyDialog : ChangeEmailAction

    data class PasswordChanged(val newValue: TextFieldValue) : ChangeEmailAction
    data class NewEmailChanged(val newValue: TextFieldValue) : ChangeEmailAction

    data object ChangeUserEmail : ChangeEmailAction
}