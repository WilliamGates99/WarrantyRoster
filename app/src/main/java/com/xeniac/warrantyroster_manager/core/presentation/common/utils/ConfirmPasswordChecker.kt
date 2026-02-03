package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import com.xeniac.warrantyroster_manager.core.presentation.common.states.ConfirmPasswordMatchingState
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ConfirmPasswordChecker @Inject constructor() {
    operator fun invoke(
        password: String?,
        confirmPassword: String?
    ): ConfirmPasswordMatchingState? {
        if (password.isNullOrBlank() && confirmPassword.isNullOrBlank()) {
            return null
        }

        val doesPasswordMatchConfirmPassword = password == confirmPassword
        return when (doesPasswordMatchConfirmPassword) {
            true -> ConfirmPasswordMatchingState.MATCHING
            false -> ConfirmPasswordMatchingState.NOT_MATCHING
        }
    }
}