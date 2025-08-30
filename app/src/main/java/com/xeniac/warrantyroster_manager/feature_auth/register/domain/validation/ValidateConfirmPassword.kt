package com.xeniac.warrantyroster_manager.feature_auth.register.domain.validation

import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.RegisterWithEmailError

class ValidateConfirmPassword {
    operator fun invoke(
        password: String?,
        confirmPassword: String?
    ): RegisterWithEmailError? {
        if (confirmPassword.isNullOrBlank()) {
            return RegisterWithEmailError.BlankConfirmPassword
        }

        val doesPasswordMatchConfirmPassword = password != confirmPassword
        if (doesPasswordMatchConfirmPassword) {
            return RegisterWithEmailError.NotMatchingPasswords
        }

        return null
    }
}