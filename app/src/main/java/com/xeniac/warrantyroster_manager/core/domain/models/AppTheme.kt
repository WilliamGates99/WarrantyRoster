package com.xeniac.warrantyroster_manager.core.domain.models

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.xeniac.warrantyroster_manager.R

enum class AppTheme(
    val index: Int,
    @StringRes val titleId: Int,
    val setAppTheme: () -> Unit
) {
    DEFAULT(
        index = 0,
        titleId = R.string.core_theme_title_default,
        setAppTheme = { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }
    ),
    LIGHT(
        index = 1,
        titleId = R.string.core_theme_title_light,
        setAppTheme = { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) }
    ),
    DARK(
        index = 2,
        titleId = R.string.core_theme_title_dark,
        setAppTheme = { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
    )
}