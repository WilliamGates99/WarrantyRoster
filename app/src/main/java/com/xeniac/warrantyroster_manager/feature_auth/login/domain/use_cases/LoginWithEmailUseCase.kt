package com.xeniac.warrantyroster_manager.feature_auth.login.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_auth.login.domain.models.LoginWithEmailResult
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.repositories.LoginWithEmailRepository
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.validation.ValidateEmail
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.validation.ValidatePassword
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class LoginWithEmailUseCase @Inject constructor(
    private val loginWithEmailRepository: LoginWithEmailRepository,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword
) {
    operator fun invoke(
        email: String,
        password: String
    ): Flow<LoginWithEmailResult> = flow {
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)

        val hasError = listOf(
            emailError,
            passwordError
        ).any { it != null }

        if (hasError) {
            return@flow emit(
                LoginWithEmailResult(
                    emailError = emailError,
                    passwordError = passwordError
                )
            )
        }

        return@flow emit(
            loginWithEmailRepository.loginWithEmail(
                email = email,
                password = password
            )
        )
    }
}