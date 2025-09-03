package com.xeniac.warrantyroster_manager.feature_auth.register.presentation.components

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
import com.xeniac.warrantyroster_manager.core.presentation.common.states.ConfirmPasswordTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.states.PasswordTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomOutlinedTextField
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.outlinedTextFieldColors
import com.xeniac.warrantyroster_manager.feature_auth.register.presentation.RegisterAction

@Composable
fun RegisterTextFields(
    isRegisterLoading: Boolean,
    emailState: CustomTextFieldState,
    passwordState: PasswordTextFieldState,
    confirmPasswordState: ConfirmPasswordTextFieldState,
    modifier: Modifier = Modifier,
    onAction: (action: RegisterAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        CustomOutlinedTextField(
            isLoading = isRegisterLoading,
            value = emailState.value,
            title = stringResource(id = R.string.register_textfield_email_title),
            placeholder = stringResource(id = R.string.register_textfield_email_hint),
            errorText = emailState.errorText,
            contentType = ContentType.Username,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            onValueChange = { newValue ->
                onAction(RegisterAction.EmailChanged(newValue))
            },
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            isLoading = isRegisterLoading,
            value = passwordState.value,
            title = stringResource(id = R.string.register_textfield_password_title),
            placeholder = stringResource(id = R.string.register_textfield_password_hint),
            supportingText = passwordState.strength?.label?.asString(),
            errorText = passwordState.errorText,
            colors = passwordState.strength?.let { passwordStrength ->
                outlinedTextFieldColors().copy(
                    focusedIndicatorColor = passwordStrength.color,
                    unfocusedIndicatorColor = passwordStrength.color,
                    disabledIndicatorColor = passwordStrength.color,
                    focusedSupportingTextColor = passwordStrength.color,
                    unfocusedSupportingTextColor = passwordStrength.color,
                    disabledSupportingTextColor = passwordStrength.color
                )
            } ?: outlinedTextFieldColors(),
            isPasswordTextField = true,
            contentType = ContentType.Password,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
            onValueChange = { newValue ->
                onAction(RegisterAction.PasswordChanged(newValue))
            },
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            isLoading = isRegisterLoading,
            value = confirmPasswordState.value,
            title = stringResource(id = R.string.register_textfield_confirm_password_title),
            placeholder = stringResource(id = R.string.register_textfield_confirm_password_hint),
            errorText = confirmPasswordState.errorText,
            colors = confirmPasswordState.passwordMatchingState?.let { matchingState ->
                outlinedTextFieldColors().copy(
                    focusedIndicatorColor = matchingState.color,
                    unfocusedIndicatorColor = matchingState.color,
                    disabledIndicatorColor = matchingState.color
                )
            } ?: outlinedTextFieldColors(),
            isPasswordTextField = true,
            contentType = ContentType.Password,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            onValueChange = { newValue ->
                onAction(RegisterAction.ConfirmPasswordChanged(newValue))
            },
            keyboardAction = {
                focusManager.clearFocus()
                onAction(RegisterAction.RegisterWithEmail)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}