package com.xeniac.warrantyroster_manager.feature_settings.presentation.states

import android.os.Parcelable
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsState(
    val currentAppLocale: AppLocale? = null,
    val currentAppTheme: AppTheme? = null,
    val isLocaleBottomSheetVisible: Boolean = false,
    val isThemeBottomSheetVisible: Boolean = false,
    val isVerificationEmailSentDialogVisible: Boolean = false,
    val isSendVerificationEmailLoading: Boolean = false
) : Parcelable