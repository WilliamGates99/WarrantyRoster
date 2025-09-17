package com.xeniac.warrantyroster_manager.feature_base.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.permission.PermissionHelper

class PostNotificationPermissionHelper : PermissionHelper {
    override fun getMessage(isPermanentlyDeclined: Boolean): UiText = when {
        isPermanentlyDeclined -> UiText.StringResource(R.string.permissions_error_notification_declined_permanently)
        else -> UiText.StringResource(R.string.permissions_error_notification_declined)
    }
}