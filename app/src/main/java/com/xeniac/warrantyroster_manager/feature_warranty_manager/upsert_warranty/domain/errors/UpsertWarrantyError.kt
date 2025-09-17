package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText

sealed class UpsertWarrantyError : Error() {
    data object InvalidWarranty : UpsertWarrantyError()

    data object BlankTitle : UpsertWarrantyError()

    data object BlankStartingAndExpiryDate : UpsertWarrantyError()
    data object BlankStartingDate : UpsertWarrantyError()
    data object BlankExpiryDate : UpsertWarrantyError()
    data object InvalidExpiryDate : UpsertWarrantyError()

    sealed class Network : UpsertWarrantyError() {
        data object FirebaseAuthUnauthorizedUser : Network()

        data object FirebaseNetworkException : Network()
        data object FirebaseTooManyRequestsException : Network()
        data object Firebase403 : Network()

        data class FirebaseFirestoreException(val message: UiText) : Network()

        data object SomethingWentWrong : Network()
    }
}