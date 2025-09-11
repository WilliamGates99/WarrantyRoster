package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.errors.ObserveWarrantiesError

fun ObserveWarrantiesError.asUiText(): UiText = when (this) {
    ObserveWarrantiesError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    ObserveWarrantiesError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    ObserveWarrantiesError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    ObserveWarrantiesError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    is ObserveWarrantiesError.Network.FirebaseFirestoreException -> message

    ObserveWarrantiesError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}