package com.xeniac.warrantyroster_manager.feature_change_email.domain.validation

import com.xeniac.warrantyroster_manager.feature_change_email.domain.errors.ChangeUserEmailError
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ValidateNewEmail @Inject constructor() {
    operator fun invoke(
        newEmail: String?,
        regex: Regex = Regex(pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")
    ): ChangeUserEmailError? {
        if (newEmail.isNullOrBlank()) {
            return ChangeUserEmailError.BlankNewEmail
        }

        val isEmail = regex.matches(newEmail)
        if (!isEmail) {
            return ChangeUserEmailError.InvalidNewEmail
        }

        return null
    }
}