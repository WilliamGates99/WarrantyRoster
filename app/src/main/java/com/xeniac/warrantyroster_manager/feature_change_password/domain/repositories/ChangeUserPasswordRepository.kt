package com.xeniac.warrantyroster_manager.feature_change_password.domain.repositories

import com.xeniac.warrantyroster_manager.feature_change_password.domain.models.ChangeUserPasswordResult

interface ChangeUserPasswordRepository {

    suspend fun changeUserPassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): ChangeUserPasswordResult
}