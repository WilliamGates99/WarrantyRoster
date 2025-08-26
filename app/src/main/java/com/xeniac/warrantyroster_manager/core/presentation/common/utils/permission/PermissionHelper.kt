package com.xeniac.warrantyroster_manager.core.presentation.common.utils.permission

import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText

interface PermissionHelper {
    fun getMessage(isPermanentlyDeclined: Boolean): UiText
}