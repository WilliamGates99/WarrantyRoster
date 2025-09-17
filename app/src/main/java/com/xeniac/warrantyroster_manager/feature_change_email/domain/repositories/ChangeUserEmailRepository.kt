package com.xeniac.warrantyroster_manager.feature_change_email.domain.repositories

import com.xeniac.warrantyroster_manager.feature_change_email.domain.models.ChangeUserEmailResult

interface ChangeUserEmailRepository {

    suspend fun changeUserEmail(
        password: String,
        newEmail: String
    ): ChangeUserEmailResult
}