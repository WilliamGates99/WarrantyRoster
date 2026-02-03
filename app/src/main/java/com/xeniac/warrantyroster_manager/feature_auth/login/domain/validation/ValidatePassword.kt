package com.xeniac.warrantyroster_manager.feature_auth.login.domain.validation

import com.xeniac.warrantyroster_manager.feature_auth.login.domain.errors.LoginWithEmailError
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ValidatePassword @Inject constructor() {
    operator fun invoke(
        password: String?
    ): LoginWithEmailError? {
        if (password.isNullOrBlank()) {
            return LoginWithEmailError.BlankPassword
        }

        return null
    }
}