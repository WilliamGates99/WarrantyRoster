package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.errors.StoreAppLocaleError

fun StoreAppLocaleError.asUiText(): UiText = when (this) {
    StoreAppLocaleError.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}