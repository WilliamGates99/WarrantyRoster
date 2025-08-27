package com.xeniac.warrantyroster_manager.feature_onboarding.presentation.states

import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale

data class OnboardingState(
    val currentAppLocale: AppLocale? = null,
    val isLocaleBottomSheetVisible: Boolean = false
)