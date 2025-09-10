package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkGoogleAccountError

fun UnlinkGoogleAccountError.asUiText(): UiText = when (this) {
    UnlinkGoogleAccountError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    UnlinkGoogleAccountError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    UnlinkGoogleAccountError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    UnlinkGoogleAccountError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    UnlinkGoogleAccountError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}