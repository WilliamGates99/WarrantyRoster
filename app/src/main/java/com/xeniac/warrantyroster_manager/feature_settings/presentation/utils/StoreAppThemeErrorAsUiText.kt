package com.xeniac.warrantyroster_manager.feature_settings.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_settings.domain.errors.StoreAppThemeError

fun StoreAppThemeError.asUiText(): UiText = when (this) {
    StoreAppThemeError.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}