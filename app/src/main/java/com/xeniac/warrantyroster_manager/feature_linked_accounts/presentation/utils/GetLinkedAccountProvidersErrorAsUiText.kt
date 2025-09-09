package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.GetLinkedAccountProvidersError

fun GetLinkedAccountProvidersError.asUiText(): UiText = when (this) {
    GetLinkedAccountProvidersError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    GetLinkedAccountProvidersError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(
        R.string.error_firebase_device_blocked
    )
    GetLinkedAccountProvidersError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    GetLinkedAccountProvidersError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    GetLinkedAccountProvidersError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}