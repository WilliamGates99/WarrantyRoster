package com.xeniac.warrantyroster_manager.core.data.utils

object FirebaseErrorsHelper {

    fun isFirebase403Error(
        exceptionMessage: String?
    ): Boolean = exceptionMessage?.contains(
        other = "403",
        ignoreCase = true
    ) == true
}