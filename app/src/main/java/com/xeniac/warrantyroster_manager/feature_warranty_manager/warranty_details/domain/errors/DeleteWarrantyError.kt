package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText

sealed class DeleteWarrantyError : Error() {
    sealed class Network : DeleteWarrantyError() {
        data object FirebaseAuthUnauthorizedUser : Network()

        data object FirebaseNetworkException : Network()
        data object FirebaseTooManyRequestsException : Network()
        data object Firebase403 : Network()

        data class FirebaseFirestoreException(val message: UiText) : Network()

        data object SomethingWentWrong : Network()
    }
}