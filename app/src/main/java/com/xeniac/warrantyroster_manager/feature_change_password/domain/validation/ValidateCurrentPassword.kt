package com.xeniac.warrantyroster_manager.feature_change_password.domain.validation

import com.xeniac.warrantyroster_manager.feature_change_password.domain.errors.ChangeUserPasswordError

class ValidateCurrentPassword {
    operator fun invoke(
        currentPassword: String?
    ): ChangeUserPasswordError? {
        if (currentPassword.isNullOrBlank()) {
            return ChangeUserPasswordError.BlankCurrentPassword
        }

        return null
    }
}