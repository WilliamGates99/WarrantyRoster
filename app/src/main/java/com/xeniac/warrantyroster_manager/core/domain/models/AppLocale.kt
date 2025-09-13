package com.xeniac.warrantyroster_manager.core.domain.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.unit.LayoutDirection
import com.xeniac.warrantyroster_manager.R

enum class AppLocale(
    val index: Int,
    val languageTag: String?,
    val localeString: String?,
    val layoutDirectionCompose: LayoutDirection,
    val layoutDirection: Int,
    @param:StringRes val titleId: Int,
    @param:StringRes val fullTitleId: Int,
    @param:DrawableRes val flagIconId: Int
) {
    DEFAULT(
        index = 0,
        languageTag = "en-US",
        localeString = "en_US",
        layoutDirectionCompose = LayoutDirection.Ltr,
        layoutDirection = -1, // LayoutDirection.UNDEFINED
        titleId = R.string.core_locale_title_default,
        fullTitleId = R.string.core_locale_title_full_default,
        flagIconId = R.drawable.ic_core_locale_default
    ),
    ENGLISH_US(
        index = 1,
        languageTag = "en-US",
        localeString = "en_US",
        layoutDirectionCompose = LayoutDirection.Ltr,
        layoutDirection = android.util.LayoutDirection.LTR,
        titleId = R.string.core_locale_title_english_us,
        fullTitleId = R.string.core_locale_title_full_english_us,
        flagIconId = R.drawable.ic_core_locale_en_us
    ),
    ENGLISH_GB(
        index = 2,
        languageTag = "en-GB",
        localeString = "en_GB",
        layoutDirectionCompose = LayoutDirection.Ltr,
        layoutDirection = android.util.LayoutDirection.LTR,
        titleId = R.string.core_locale_title_english_gb,
        fullTitleId = R.string.core_locale_title_full_english_gb,
        flagIconId = R.drawable.ic_core_locale_en_gb
    ),
    FARSI_IR(
        index = 3,
        languageTag = "fa-IR",
        localeString = "fa_IR",
        layoutDirectionCompose = LayoutDirection.Rtl,
        layoutDirection = android.util.LayoutDirection.RTL,
        titleId = R.string.core_locale_title_farsi_ir,
        fullTitleId = R.string.core_locale_title_full_farsi_ir,
        flagIconId = R.drawable.ic_core_locale_fa_ir
    )
}