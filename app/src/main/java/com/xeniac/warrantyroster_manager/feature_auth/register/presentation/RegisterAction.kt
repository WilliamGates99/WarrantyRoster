package com.xeniac.warrantyroster_manager.feature_auth.register.presentation

import androidx.compose.ui.text.input.TextFieldValue
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

sealed interface RegisterAction {
    data class EmailChanged(val newValue: TextFieldValue) : RegisterAction
    data class PasswordChanged(val newValue: TextFieldValue) : RegisterAction
    data class ConfirmPasswordChanged(val newValue: TextFieldValue) : RegisterAction

    data object RegisterWithEmail : RegisterAction

    data object LoginWithGoogle : RegisterAction
    data object CheckPendingLoginWithX : RegisterAction
    data class LoginWithX(val loginWithXTask: Task<AuthResult>) : RegisterAction
    data object LoginWithGithub : RegisterAction
}