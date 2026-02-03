package com.xeniac.warrantyroster_manager.feature_change_password.domain.validation

import com.xeniac.warrantyroster_manager.feature_change_password.domain.errors.ChangeUserPasswordError
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ValidateConfirmNewPassword @Inject constructor() {
    operator fun invoke(
        newPassword: String?,
        confirmNewPassword: String?
    ): ChangeUserPasswordError? {
        if (confirmNewPassword.isNullOrBlank()) {
            return ChangeUserPasswordError.BlankConfirmNewPassword
        }

        val doesPasswordNotMatchConfirmPassword = newPassword != confirmNewPassword
        if (doesPasswordNotMatchConfirmPassword) {
            return ChangeUserPasswordError.NotMatchingNewPasswords
        }

        return null
    }
}