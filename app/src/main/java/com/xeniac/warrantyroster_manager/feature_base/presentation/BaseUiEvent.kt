package com.xeniac.warrantyroster_manager.feature_base.presentation

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Event

sealed class BaseUiEvent : Event() {
    data class StartAppUpdateFlow(val appUpdateInfo: AppUpdateInfo) : BaseUiEvent()
    data object ShowCompleteAppUpdateSnackbar : BaseUiEvent()
    data object CompleteFlexibleAppUpdate : BaseUiEvent()
    data object LaunchInAppReview : BaseUiEvent()
}