package com.xeniac.warrantyroster_manager.feature_change_email.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_change_email.domain.models.ChangeUserEmailResult
import com.xeniac.warrantyroster_manager.feature_change_email.domain.repositories.ChangeUserEmailRepository
import com.xeniac.warrantyroster_manager.feature_change_email.domain.validation.ValidateNewEmail
import com.xeniac.warrantyroster_manager.feature_change_email.domain.validation.ValidatePassword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChangeUserEmailUseCase(
    private val changeUserEmailRepository: ChangeUserEmailRepository,
    private val validatePassword: ValidatePassword,
    private val validateNewEmail: ValidateNewEmail
) {
    operator fun invoke(
        password: String,
        newEmail: String
    ): Flow<ChangeUserEmailResult> = flow {
        val passwordError = validatePassword(password)
        val newEmailError = validateNewEmail(newEmail)

        val hasError = listOf(
            passwordError,
            newEmailError
        ).any { it != null }

        if (hasError) {
            return@flow emit(
                ChangeUserEmailResult(
                    passwordError = passwordError,
                    newEmailError = newEmailError
                )
            )
        }

        return@flow emit(
            changeUserEmailRepository.changeUserEmail(
                password = password,
                newEmail = newEmail
            )
        )
    }
}