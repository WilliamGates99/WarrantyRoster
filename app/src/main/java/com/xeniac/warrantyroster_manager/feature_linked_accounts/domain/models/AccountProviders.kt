package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.xeniac.warrantyroster_manager.R

enum class AccountProviders(
    val id: String,
    @param:DrawableRes val iconId: Int,
    @param:StringRes val titleId: Int
) {
    GOOGLE(
        id = "google.com",
        iconId = R.drawable.ic_core_social_google,
        titleId = R.string.linked_accounts_providers_google_title
    ),
    X(
        id = "twitter.com",
        iconId = R.drawable.ic_core_social_x,
        titleId = R.string.linked_accounts_providers_x_title
    ),
    GITHUB(
        id = "github.com", // TODO: CHANGE
        iconId = R.drawable.ic_core_social_github,
        titleId = R.string.linked_accounts_providers_github_title
    ),
    // FACEBOOK(
    //     id = "facebook.com",
    //     iconId = R.drawable.ic_core_social_facebook,
    //     titleId = R.string.linked_accounts_providers_facebook_title
    // )
}