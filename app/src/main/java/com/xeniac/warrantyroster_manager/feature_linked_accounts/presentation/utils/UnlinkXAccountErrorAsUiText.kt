package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkXAccountError

fun UnlinkXAccountError.asUiText(): UiText = when (this) {
    UnlinkXAccountError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    UnlinkXAccountError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    UnlinkXAccountError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    UnlinkXAccountError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    UnlinkXAccountError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}