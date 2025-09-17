package com.xeniac.warrantyroster_manager.feature_auth.login.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.states.PasswordTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomOutlinedTextField
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.LoginAction

@Composable
fun LoginTextFields(
    isLoginLoading: Boolean,
    emailState: CustomTextFieldState,
    passwordState: PasswordTextFieldState,
    modifier: Modifier = Modifier,
    onAction: (action: LoginAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        CustomOutlinedTextField(
            isLoading = isLoginLoading,
            value = emailState.value,
            title = stringResource(id = R.string.login_textfield_email_title),
            placeholder = stringResource(id = R.string.login_textfield_email_hint),
            errorText = emailState.errorText,
            contentType = ContentType.Username,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            onValueChange = { newValue ->
                onAction(LoginAction.EmailChanged(newValue))
            },
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            isLoading = isLoginLoading,
            value = passwordState.value,
            title = stringResource(id = R.string.login_textfield_password_title),
            placeholder = stringResource(id = R.string.login_textfield_password_hint),
            errorText = passwordState.errorText,
            isPasswordTextField = true,
            contentType = ContentType.Password,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            onValueChange = { newValue ->
                onAction(LoginAction.PasswordChanged(newValue))
            },
            keyboardAction = {
                focusManager.clearFocus()
                onAction(LoginAction.LoginWithEmail)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}