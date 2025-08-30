package com.xeniac.warrantyroster_manager.feature_auth.register.domain.validation

import com.xeniac.warrantyroster_manager.core.domain.utils.Constants
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.RegisterWithEmailError

class ValidatePassword {
    operator fun invoke(
        password: String?
    ): RegisterWithEmailError? {
        if (password.isNullOrBlank()) {
            return RegisterWithEmailError.BlankPassword
        }

        val isPasswordShort = password.length < Constants.MIN_PASSWORD_LENGTH
        if (isPasswordShort) {
            return RegisterWithEmailError.ShortPassword
        }

        return null
    }
}