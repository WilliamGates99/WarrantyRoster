package com.xeniac.warrantyroster_manager.feature_auth.register.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_auth.register.domain.models.RegisterWithEmailResult
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.repositories.RegisterRepository
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.validation.ValidateConfirmPassword
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.validation.ValidateEmail
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.validation.ValidatePassword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RegisterWithEmailUseCase(
    private val registerRepository: RegisterRepository,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateConfirmPassword: ValidateConfirmPassword
) {
    operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): Flow<RegisterWithEmailResult> = flow {
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)
        val confirmPasswordError = validateConfirmPassword(
            password = password,
            confirmPassword = confirmPassword
        )

        val hasError = listOf(
            emailError,
            passwordError,
            confirmPasswordError
        ).any { it != null }

        if (hasError) {
            return@flow emit(
                RegisterWithEmailResult(
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            )
        }

        return@flow emit(
            RegisterWithEmailResult(
                result = registerRepository.registerWithEmail(
                    email = email,
                    password = password
                )
            )
        )
    }
}