package com.xeniac.warrantyroster_manager.feature_auth.login.presentation

import androidx.compose.ui.text.input.TextFieldValue
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

sealed interface LoginAction {
    data class EmailChanged(val newValue: TextFieldValue) : LoginAction
    data class PasswordChanged(val newValue: TextFieldValue) : LoginAction

    data object LoginWithEmail : LoginAction

    data object LoginWithGoogle : LoginAction
    data object CheckPendingLoginWithX : LoginAction
    data class LoginWithX(val loginWithXTask: Task<AuthResult>) : LoginAction
    data object LoginWithFacebook : LoginAction
}