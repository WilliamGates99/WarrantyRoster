package com.xeniac.warrantyroster_manager.feature_auth.common.presentation

import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Event

sealed class AuthUiEvent : Event() {
    data object NavigateToBaseScreen : AuthUiEvent()
    data object StartActivityForLoginWithX : AuthUiEvent()
}