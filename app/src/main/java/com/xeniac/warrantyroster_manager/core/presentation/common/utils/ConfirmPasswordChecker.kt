package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import com.xeniac.warrantyroster_manager.core.presentation.common.states.ConfirmPasswordMatchingState

class ConfirmPasswordChecker {
    operator fun invoke(
        password: String?,
        confirmPassword: String?
    ): ConfirmPasswordMatchingState {
        if (password.isNullOrBlank() && confirmPassword.isNullOrBlank()) {
            return ConfirmPasswordMatchingState.BLANK_CONFIRM_PASSWORD
        }

        val doesPasswordMatchConfirmPassword = password == confirmPassword
        return when (doesPasswordMatchConfirmPassword) {
            true -> ConfirmPasswordMatchingState.MATCHING
            false -> ConfirmPasswordMatchingState.NOT_MATCHING
        }
    }
}