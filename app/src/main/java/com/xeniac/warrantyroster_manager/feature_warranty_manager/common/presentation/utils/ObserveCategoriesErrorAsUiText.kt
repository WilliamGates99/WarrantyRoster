package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.errors.ObserveCategoriesError

fun ObserveCategoriesError.asUiText(): UiText = when (this) {
    ObserveCategoriesError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    ObserveCategoriesError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    ObserveCategoriesError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    ObserveCategoriesError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    is ObserveCategoriesError.Network.FirebaseFirestoreException -> message

    ObserveCategoriesError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}