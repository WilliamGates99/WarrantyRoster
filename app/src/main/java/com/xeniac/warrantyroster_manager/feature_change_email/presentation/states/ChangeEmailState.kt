package com.xeniac.warrantyroster_manager.feature_change_email.presentation.states

import android.os.Parcelable
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.states.PasswordTextFieldState
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChangeEmailState(
    val passwordState: PasswordTextFieldState = PasswordTextFieldState(),
    val newEmailState: CustomTextFieldState = CustomTextFieldState(),
    val isEmailChangedSuccessfullyDialogVisible: Boolean = false,
    val isChangeUserEmailLoading: Boolean = false
) : Parcelable