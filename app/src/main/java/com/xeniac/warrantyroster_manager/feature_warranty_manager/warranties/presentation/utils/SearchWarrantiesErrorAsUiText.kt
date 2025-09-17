package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.errors.SearchWarrantiesError

fun SearchWarrantiesError.asUiText(): UiText = when (this) {
    SearchWarrantiesError.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}