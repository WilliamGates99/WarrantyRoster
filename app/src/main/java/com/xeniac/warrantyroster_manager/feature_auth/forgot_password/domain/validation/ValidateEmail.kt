package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.validation

import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.errors.SendResetPasswordEmailError
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ValidateEmail @Inject constructor() {
    operator fun invoke(
        email: String?,
        regex: Regex = Regex(pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")
    ): SendResetPasswordEmailError? {
        if (email.isNullOrBlank()) {
            return SendResetPasswordEmailError.BlankEmail
        }

        val isEmail = regex.matches(email)
        if (!isEmail) {
            return SendResetPasswordEmailError.InvalidEmail
        }

        return null
    }
}