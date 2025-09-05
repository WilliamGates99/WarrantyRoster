package com.xeniac.warrantyroster_manager.core.presentation.base_screen

import com.xeniac.warrantyroster_manager.core.domain.models.RateAppOption

sealed interface BaseAction {
    data object CheckIsAppUpdateStalled : BaseAction
    data object CheckFlexibleUpdateDownloadState : BaseAction
    data object CheckForAppUpdates : BaseAction
    data object GetLatestAppVersion : BaseAction
    data object DismissAppUpdateSheet : BaseAction

    data object RequestInAppReviews : BaseAction
    data class CheckSelectedRateAppOption(val selectedRateAppOption: RateAppOption) : BaseAction
    data object LaunchInAppReview : BaseAction
    data object SetSelectedRateAppOptionToNever : BaseAction
    data object SetSelectedRateAppOptionToRemindLater : BaseAction
    data object DismissAppReviewDialog : BaseAction

    data class OnPermissionResult(
        val permission: String,
        val isGranted: Boolean
    ) : BaseAction

    data class DismissPermissionDialog(val permission: String) : BaseAction
}