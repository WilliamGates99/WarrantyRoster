package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class SearchWarrantiesError : Error() {
    data object SomethingWentWrong : SearchWarrantiesError()
}