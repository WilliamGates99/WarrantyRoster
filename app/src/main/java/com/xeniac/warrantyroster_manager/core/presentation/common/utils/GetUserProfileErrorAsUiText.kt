package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.errors.GetUserProfileError

fun GetUserProfileError.asUiText(): UiText = when (this) {
    GetUserProfileError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    GetUserProfileError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    GetUserProfileError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    GetUserProfileError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    GetUserProfileError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}