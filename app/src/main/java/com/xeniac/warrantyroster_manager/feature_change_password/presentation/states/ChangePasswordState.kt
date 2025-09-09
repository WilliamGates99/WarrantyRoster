package com.xeniac.warrantyroster_manager.feature_change_password.presentation.states

import android.os.Parcelable
import com.xeniac.warrantyroster_manager.core.presentation.common.states.ConfirmPasswordTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.states.PasswordTextFieldState
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChangePasswordState(
    val currentPasswordState: PasswordTextFieldState = PasswordTextFieldState(),
    val newPasswordState: PasswordTextFieldState = PasswordTextFieldState(),
    val confirmNewPasswordState: ConfirmPasswordTextFieldState = ConfirmPasswordTextFieldState(),
    val isPasswordChangedDialogVisible: Boolean? = null,
    val isChangeUserPasswordLoading: Boolean = false
) : Parcelable