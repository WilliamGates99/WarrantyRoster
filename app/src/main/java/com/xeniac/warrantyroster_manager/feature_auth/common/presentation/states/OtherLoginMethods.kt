package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.xeniac.warrantyroster_manager.R

enum class OtherLoginMethods(
    @param:DrawableRes val iconId: Int,
    @param:StringRes val contentDescriptionId: Int
) {
    GOOGLE(
        iconId = R.drawable.ic_auth_method_google,
        contentDescriptionId = R.string.auth_other_login_methods_google
    ),
    X(
        iconId = R.drawable.ic_auth_method_x,
        contentDescriptionId = R.string.auth_other_login_methods_x
    ),
    FACEBOOK(
        iconId = R.drawable.ic_auth_method_facebook,
        contentDescriptionId = R.string.auth_other_login_methods_facebook
    )
}