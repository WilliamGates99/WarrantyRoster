package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkGithubAccountError

fun UnlinkGithubAccountError.asUiText(): UiText = when (this) {
    UnlinkGithubAccountError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    UnlinkGithubAccountError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    UnlinkGithubAccountError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    UnlinkGithubAccountError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    UnlinkGithubAccountError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}