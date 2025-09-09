package com.xeniac.warrantyroster_manager.feature_change_password.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_change_password.domain.models.ChangeUserPasswordResult
import com.xeniac.warrantyroster_manager.feature_change_password.domain.repositories.ChangeUserPasswordRepository
import com.xeniac.warrantyroster_manager.feature_change_password.domain.validation.ValidateConfirmNewPassword
import com.xeniac.warrantyroster_manager.feature_change_password.domain.validation.ValidateCurrentPassword
import com.xeniac.warrantyroster_manager.feature_change_password.domain.validation.ValidateNewPassword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChangeUserPasswordUseCase(
    private val changeUserPasswordRepository: ChangeUserPasswordRepository,
    private val validateCurrentPassword: ValidateCurrentPassword,
    private val validateNewPassword: ValidateNewPassword,
    private val validateConfirmNewPassword: ValidateConfirmNewPassword
) {
    operator fun invoke(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): Flow<ChangeUserPasswordResult> = flow {
        val currentPasswordError = validateCurrentPassword(currentPassword)
        val newPasswordError = validateNewPassword(newPassword)
        val confirmNewPasswordError = validateConfirmNewPassword(
            newPassword = newPassword,
            confirmNewPassword = confirmNewPassword
        )

        val hasError = listOf(
            currentPasswordError,
            newPasswordError,
            confirmNewPasswordError
        ).any { it != null }

        if (hasError) {
            return@flow emit(
                ChangeUserPasswordResult(
                    currentPasswordError = currentPasswordError,
                    newPasswordError = newPasswordError,
                    confirmNewPasswordError = confirmNewPasswordError
                )
            )
        }

        return@flow emit(
            changeUserPasswordRepository.changeUserPassword(
                currentPassword = currentPassword,
                newPassword = newPassword,
                confirmNewPassword = confirmNewPassword
            )
        )
    }
}