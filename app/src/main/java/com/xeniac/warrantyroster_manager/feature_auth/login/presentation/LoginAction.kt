package com.xeniac.warrantyroster_manager.feature_auth.login.presentation

import androidx.compose.ui.text.input.TextFieldValue

sealed interface LoginAction {
    data class EmailChanged(val newValue: TextFieldValue) : LoginAction
    data class PasswordChanged(val newValue: TextFieldValue) : LoginAction

    data object LoginWithEmail : LoginAction
    data object LoginWithGoogle : LoginAction
    data object LoginWithX : LoginAction
    data object LoginWithFacebook : LoginAction
}