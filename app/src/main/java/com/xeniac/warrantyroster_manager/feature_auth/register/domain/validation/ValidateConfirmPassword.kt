package com.xeniac.warrantyroster_manager.feature_auth.register.domain.validation

import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.RegisterWithEmailError
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ValidateConfirmPassword @Inject constructor() {
    operator fun invoke(
        password: String?,
        confirmPassword: String?
    ): RegisterWithEmailError? {
        if (confirmPassword.isNullOrBlank()) {
            return RegisterWithEmailError.BlankConfirmPassword
        }

        val doesPasswordNotMatchConfirmPassword = password != confirmPassword
        if (doesPasswordNotMatchConfirmPassword) {
            return RegisterWithEmailError.NotMatchingPasswords
        }

        return null
    }
}