package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors.UpsertWarrantyError

fun UpsertWarrantyError.asUiText(): UiText = when (this) {
    UpsertWarrantyError.InvalidWarranty -> UiText.StringResource(R.string.upsert_warranty_error_invalid_warranty)

    UpsertWarrantyError.BlankTitle -> UiText.StringResource(R.string.upsert_warranty_error_blank_title)

    UpsertWarrantyError.BlankStartingAndExpiryDate -> UiText.StringResource(R.string.upsert_warranty_error_blank_starting_and_expiry_date)
    UpsertWarrantyError.BlankStartingDate -> UiText.StringResource(R.string.upsert_warranty_error_blank_starting_date)
    UpsertWarrantyError.BlankExpiryDate -> UiText.StringResource(R.string.upsert_warranty_error_blank_expiry_date)
    UpsertWarrantyError.InvalidExpiryDate -> UiText.StringResource(R.string.upsert_warranty_error_invalid_expiry_date)

    UpsertWarrantyError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    UpsertWarrantyError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    UpsertWarrantyError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    UpsertWarrantyError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    is UpsertWarrantyError.Network.FirebaseFirestoreException -> message

    UpsertWarrantyError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}