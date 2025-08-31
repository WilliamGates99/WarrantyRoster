package com.xeniac.warrantyroster_manager.feature_auth.login.domain.validation

import com.xeniac.warrantyroster_manager.feature_auth.login.domain.errors.LoginWithEmailError

class ValidateEmail {
    operator fun invoke(
        email: String?,
        regex: Regex = Regex(pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")
    ): LoginWithEmailError? {
        if (email.isNullOrBlank()) {
            return LoginWithEmailError.BlankEmail
        }

        val isEmail = regex.matches(email)
        if (!isEmail) {
            return LoginWithEmailError.InvalidEmail
        }

        return null
    }
}