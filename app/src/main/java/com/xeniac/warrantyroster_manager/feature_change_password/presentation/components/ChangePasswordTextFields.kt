package com.xeniac.warrantyroster_manager.feature_change_password.presentation.components

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
import com.xeniac.warrantyroster_manager.core.presentation.common.states.PasswordTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomOutlinedTextField
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.outlinedTextFieldColors
import com.xeniac.warrantyroster_manager.feature_change_password.presentation.ChangePasswordAction

@Composable
fun ChangePasswordTextFields(
    isChangeUserPasswordLoading: Boolean,
    currentPasswordState: PasswordTextFieldState,
    newPasswordState: PasswordTextFieldState,
    confirmNewPasswordState: ConfirmPasswordTextFieldState,
    modifier: Modifier = Modifier,
    onAction: (action: ChangePasswordAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        CustomOutlinedTextField(
            isLoading = isChangeUserPasswordLoading,
            value = currentPasswordState.value,
            title = stringResource(id = R.string.change_password_textfield_current_password_title),
            placeholder = stringResource(id = R.string.change_password_textfield_current_password_hint),
            errorText = currentPasswordState.errorText,
            isPasswordTextField = true,
            contentType = ContentType.Password,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
            onValueChange = { newValue ->
                onAction(ChangePasswordAction.CurrentPasswordChanged(newValue))
            },
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            isLoading = isChangeUserPasswordLoading,
            value = newPasswordState.value,
            title = stringResource(id = R.string.change_password_textfield_confirm_new_password_title),
            placeholder = stringResource(id = R.string.change_password_textfield_confirm_new_password_hint),
            supportingText = newPasswordState.strength?.label?.asString(),
            errorText = newPasswordState.errorText,
            colors = newPasswordState.strength?.let { passwordStrength ->
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
            contentType = ContentType.NewPassword,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
            onValueChange = { newValue ->
                onAction(ChangePasswordAction.NewPasswordChanged(newValue))
            },
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            isLoading = isChangeUserPasswordLoading,
            value = confirmNewPasswordState.value,
            title = stringResource(id = R.string.change_password_textfield_confirm_new_password_title),
            placeholder = stringResource(id = R.string.change_password_textfield_confirm_new_password_hint),
            errorText = confirmNewPasswordState.errorText,
            colors = confirmNewPasswordState.passwordMatchingState?.let { matchingState ->
                outlinedTextFieldColors().copy(
                    focusedIndicatorColor = matchingState.color,
                    unfocusedIndicatorColor = matchingState.color,
                    disabledIndicatorColor = matchingState.color
                )
            } ?: outlinedTextFieldColors(),
            isPasswordTextField = true,
            contentType = ContentType.NewPassword,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            onValueChange = { newValue ->
                onAction(ChangePasswordAction.ConfirmNewPasswordChanged(newValue))
            },
            keyboardAction = {
                focusManager.clearFocus()
                onAction(ChangePasswordAction.ChangeUserPassword)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}