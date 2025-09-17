package com.xeniac.warrantyroster_manager.core.data.utils

object FirebaseErrorsHelper {

    fun isFirebase403Error(
        exceptionMessage: String?
    ): Boolean = exceptionMessage?.contains(
        other = "403",
        ignoreCase = true
    ) == true

    fun isFirebaseCancellationException(
        exceptionMessage: String?
    ): Boolean = exceptionMessage?.contains(
        other = "The web operation was canceled",
        ignoreCase = true
    ) == true

    fun isAnotherAuthOperationInProgress(
        exceptionMessage: String?
    ): Boolean = exceptionMessage?.contains(
        other = "A headful operation is already in progress",
        ignoreCase = true
    ) == true
}