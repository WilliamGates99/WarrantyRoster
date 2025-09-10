package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkGithubAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkGoogleAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkXAccountError

interface UnlinkAccountsRepository {

    suspend fun unlinkGoogleAccount(): Result<Unit, UnlinkGoogleAccountError>

    suspend fun unlinkXAccount(): Result<Unit, UnlinkXAccountError>

    suspend fun unlinkGithubAccount(): Result<Unit, UnlinkGithubAccountError>
}