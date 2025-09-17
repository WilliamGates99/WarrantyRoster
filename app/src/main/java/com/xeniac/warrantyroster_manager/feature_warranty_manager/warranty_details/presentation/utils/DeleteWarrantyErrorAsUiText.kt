package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.errors.DeleteWarrantyError

fun DeleteWarrantyError.asUiText(): UiText = when (this) {
    DeleteWarrantyError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    DeleteWarrantyError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    DeleteWarrantyError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    DeleteWarrantyError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    is DeleteWarrantyError.Network.FirebaseFirestoreException -> message

    DeleteWarrantyError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}