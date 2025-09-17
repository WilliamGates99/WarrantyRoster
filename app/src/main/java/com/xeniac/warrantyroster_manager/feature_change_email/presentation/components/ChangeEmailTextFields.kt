package com.xeniac.warrantyroster_manager.feature_change_email.presentation.components

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
import com.xeniac.warrantyroster_manager.feature_change_email.presentation.ChangeEmailAction

@Composable
fun ChangeEmailTextFields(
    isSubmitLoading: Boolean,
    passwordState: PasswordTextFieldState,
    newEmailState: CustomTextFieldState,
    modifier: Modifier = Modifier,
    onAction: (action: ChangeEmailAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        CustomOutlinedTextField(
            isLoading = isSubmitLoading,
            value = passwordState.value,
            title = stringResource(id = R.string.change_email_textfield_password_title),
            placeholder = stringResource(id = R.string.change_email_textfield_password_hint),
            errorText = passwordState.errorText,
            isPasswordTextField = true,
            contentType = ContentType.Password,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
            onValueChange = { newValue ->
                onAction(ChangeEmailAction.PasswordChanged(newValue))
            },
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            isLoading = isSubmitLoading,
            value = newEmailState.value,
            title = stringResource(id = R.string.change_email_textfield_new_email_title),
            placeholder = stringResource(id = R.string.change_email_textfield_new_email_hint),
            errorText = newEmailState.errorText,
            contentType = ContentType.NewUsername,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            onValueChange = { newValue ->
                onAction(ChangeEmailAction.NewEmailChanged(newValue))
            },
            keyboardAction = {
                focusManager.clearFocus()
                onAction(ChangeEmailAction.ChangeUserEmail)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}