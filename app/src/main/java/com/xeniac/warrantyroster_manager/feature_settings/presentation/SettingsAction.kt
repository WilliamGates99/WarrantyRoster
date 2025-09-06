package com.xeniac.warrantyroster_manager.feature_settings.presentation

import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.AppTheme

sealed interface SettingsAction {
    data object ShowLocaleBottomSheet : SettingsAction
    data object DismissLocaleBottomSheet : SettingsAction
    data object ShowThemeBottomSheet : SettingsAction
    data object DismissThemeBottomSheet : SettingsAction

    data class SetCurrentAppLocale(val newAppLocale: AppLocale) : SettingsAction
    data class SetCurrentAppTheme(val newAppTheme: AppTheme) : SettingsAction
}