package com.xeniac.warrantyroster_manager.feature_change_email.domain.validation

import com.xeniac.warrantyroster_manager.feature_change_email.domain.errors.ChangeUserEmailError
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ValidatePassword @Inject constructor() {
    operator fun invoke(
        password: String?
    ): ChangeUserEmailError? {
        if (password.isNullOrBlank()) {
            return ChangeUserEmailError.BlankPassword
        }

        return null
    }
}