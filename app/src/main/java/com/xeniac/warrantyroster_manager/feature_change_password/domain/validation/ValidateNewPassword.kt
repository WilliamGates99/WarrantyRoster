package com.xeniac.warrantyroster_manager.feature_change_password.domain.validation

import com.xeniac.warrantyroster_manager.core.domain.utils.Constants
import com.xeniac.warrantyroster_manager.feature_change_password.domain.errors.ChangeUserPasswordError

class ValidateNewPassword {
    operator fun invoke(
        newPassword: String?
    ): ChangeUserPasswordError? {
        if (newPassword.isNullOrBlank()) {
            return ChangeUserPasswordError.BlankNewPassword
        }

        val isPasswordShort = newPassword.length < Constants.MIN_PASSWORD_LENGTH
        if (isPasswordShort) {
            return ChangeUserPasswordError.ShortNewPassword
        }

        return null
    }
}