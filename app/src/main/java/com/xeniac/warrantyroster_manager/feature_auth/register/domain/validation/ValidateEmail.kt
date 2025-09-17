package com.xeniac.warrantyroster_manager.feature_auth.register.domain.validation

import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.RegisterWithEmailError

class ValidateEmail {
    operator fun invoke(
        email: String?,
        regex: Regex = Regex(pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")
    ): RegisterWithEmailError? {
        if (email.isNullOrBlank()) {
            return RegisterWithEmailError.BlankEmail
        }

        val isEmail = regex.matches(email)
        if (!isEmail) {
            return RegisterWithEmailError.InvalidEmail
        }

        return null
    }
}