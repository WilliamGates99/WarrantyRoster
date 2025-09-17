package com.xeniac.warrantyroster_manager.feature_base.domain.repositories

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_base.domain.errors.GetLatestAppVersionError
import com.xeniac.warrantyroster_manager.feature_base.domain.models.LatestAppUpdateInfo
import kotlinx.coroutines.flow.Flow

typealias UpdateType = Int
typealias IsUpdateDownloaded = Boolean

interface AppUpdateRepository {

    fun checkFlexibleUpdateDownloadState(): Flow<IsUpdateDownloaded>

    fun checkIsFlexibleUpdateStalled(): Flow<IsUpdateDownloaded>

    fun checkIsImmediateUpdateStalled(): Flow<AppUpdateInfo?>

    fun checkForAppUpdates(): Flow<AppUpdateInfo?>

    suspend fun getLatestAppVersion(): Result<LatestAppUpdateInfo?, GetLatestAppVersionError>

    sealed class EndPoints(val url: String) {
        data object GetLatestAppVersion : EndPoints(
            url = "${BuildConfig.HTTP_BASE_URL}/app_version.json"
        )
    }
}