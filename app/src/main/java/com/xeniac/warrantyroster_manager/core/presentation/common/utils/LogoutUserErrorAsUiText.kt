package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.errors.LogoutUserError

fun LogoutUserError.asUiText(): UiText = when (this) {
    LogoutUserError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)

    LogoutUserError.Local.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}