package com.xeniac.warrantyroster_manager.core.presentation.main_activity.states

import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale

data class MainState(
    val currentAppLocale: AppLocale = AppLocale.DEFAULT,
    val isSplashScreenLoading: Boolean = true,
    val postSplashDestination: Any? = null
)