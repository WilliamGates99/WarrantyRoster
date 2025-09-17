package com.xeniac.warrantyroster_manager.feature_change_email.domain.validation

import com.xeniac.warrantyroster_manager.feature_change_email.domain.errors.ChangeUserEmailError

class ValidatePassword {
    operator fun invoke(
        password: String?
    ): ChangeUserEmailError? {
        if (password.isNullOrBlank()) {
            return ChangeUserEmailError.BlankPassword
        }

        return null
    }
}