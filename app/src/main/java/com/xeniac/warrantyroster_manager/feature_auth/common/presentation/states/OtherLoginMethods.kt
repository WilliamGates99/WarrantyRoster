package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.xeniac.warrantyroster_manager.R

enum class OtherLoginMethods(
    @param:DrawableRes val iconId: Int,
    @param:StringRes val contentDescriptionId: Int
) {
    GOOGLE(
        iconId = R.drawable.ic_core_social_google,
        contentDescriptionId = R.string.auth_other_login_methods_google
    ),
    X(
        iconId = R.drawable.ic_core_social_x,
        contentDescriptionId = R.string.auth_other_login_methods_x
    ),
    GITHUB(
        iconId = R.drawable.ic_core_social_github,
        contentDescriptionId = R.string.auth_other_login_methods_github
    )
}