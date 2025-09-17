package com.xeniac.warrantyroster_manager.feature_onboarding.presentation

import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale

sealed interface OnboardingAction {
    data object ShowLocaleBottomSheet : OnboardingAction
    data object DismissLocaleBottomSheet : OnboardingAction

    data class SetCurrentAppLocale(val newAppLocale: AppLocale) : OnboardingAction
}