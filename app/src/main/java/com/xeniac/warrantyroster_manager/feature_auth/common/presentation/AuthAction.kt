package com.xeniac.warrantyroster_manager.feature_auth.common.presentation

import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale

sealed interface AuthAction {
    data object ShowLocaleBottomSheet : AuthAction
    data object DismissLocaleBottomSheet : AuthAction

    data class SetCurrentAppLocale(val newAppLocale: AppLocale) : AuthAction
}