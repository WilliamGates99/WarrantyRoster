package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common

import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Event

sealed class ForgotPasswordUiEvent : Event() {
    data object NavigateToResetPwInstructionScreen : ForgotPasswordUiEvent()
}