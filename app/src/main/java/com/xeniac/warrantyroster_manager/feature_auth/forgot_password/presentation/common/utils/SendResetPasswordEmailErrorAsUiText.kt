package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.utils

import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.errors.SendResetPasswordEmailError

fun SendResetPasswordEmailError.asUiText(): UiText = when (this) {
    // TODO: MODIFY
    else -> UiText.DynamicString("Something went wrong")
}