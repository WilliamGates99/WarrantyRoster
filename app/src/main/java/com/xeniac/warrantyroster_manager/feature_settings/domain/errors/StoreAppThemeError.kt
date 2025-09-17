package com.xeniac.warrantyroster_manager.feature_settings.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class StoreAppThemeError : Error() {
    data object SomethingWentWrong : StoreAppThemeError()
}