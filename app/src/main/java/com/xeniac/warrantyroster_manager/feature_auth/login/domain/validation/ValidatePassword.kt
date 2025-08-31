package com.xeniac.warrantyroster_manager.feature_auth.login.domain.validation

import com.xeniac.warrantyroster_manager.feature_auth.login.domain.errors.LoginWithEmailError

class ValidatePassword {
    operator fun invoke(
        password: String?
    ): LoginWithEmailError? {
        if (password.isNullOrBlank()) {
            return LoginWithEmailError.BlankPassword
        }

        return null
    }
}