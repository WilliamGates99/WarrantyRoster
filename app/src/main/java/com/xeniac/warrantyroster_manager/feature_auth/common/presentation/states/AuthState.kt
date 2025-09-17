package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.states

import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale

data class AuthState(
    val currentAppLocale: AppLocale? = null,
    val isLocaleBottomSheetVisible: Boolean = false
)