package com.xeniac.warrantyroster_manager.feature_auth.register.presentation

import androidx.compose.ui.text.input.TextFieldValue

sealed interface RegisterAction {
    data class EmailChanged(val newValue: TextFieldValue) : RegisterAction
    data class PasswordChanged(val newValue: TextFieldValue) : RegisterAction
    data class ConfirmPasswordChanged(val newValue: TextFieldValue) : RegisterAction

    data object RegisterWithEmail : RegisterAction
}