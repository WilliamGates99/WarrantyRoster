package com.xeniac.warrantyroster_manager.feature_base.data.repositories

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.totalBytesToDownload
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.repositories.MiscellaneousDataStoreRepository
import com.xeniac.warrantyroster_manager.feature_base.data.remote.LatestAppVersionDto
import com.xeniac.warrantyroster_manager.feature_base.data.utils.Constants
import com.xeniac.warrantyroster_manager.feature_base.domain.errors.GetLatestAppVersionError
import com.xeniac.warrantyroster_manager.feature_base.domain.models.LatestAppUpdateInfo
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppUpdateRepository
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.IsUpdateDownloaded
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.UpdateType
import dagger.Lazy
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.net.UnknownHostException
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException

class AppUpdateRepositoryImpl @Inject constructor(
    private val appUpdateType: Lazy<UpdateType>,
    private val appUpdateManager: Lazy<AppUpdateManager>,
    private val httpClient: Lazy<HttpClient>,
    private val miscellaneousDataStoreRepository: Lazy<MiscellaneousDataStoreRepository>
) : AppUpdateRepository {

    override fun checkFlexibleUpdateDownloadState(): Flow<IsUpdateDownloaded> = callbackFlow {
        when (appUpdateType.get()) {
            AppUpdateType.FLEXIBLE -> {
                val installStateUpdatedListener = InstallStateUpdatedListener { state ->
                    val isUpdateDownloading = state.installStatus == InstallStatus.DOWNLOADING
                    val isUpdateDownloaded = state.installStatus == InstallStatus.DOWNLOADED

                    when {
                        isUpdateDownloading -> {
                            val bytesDownloaded = state.bytesDownloaded
                            val totalBytesToDownload = state.totalBytesToDownload
                            Timber.i("$bytesDownloaded/$totalBytesToDownload downloaded.")
                        }
                        isUpdateDownloaded -> launch { send(true) }
                    }
                }

                appUpdateManager.get().registerListener(installStateUpdatedListener)

                awaitClose {
                    appUpdateManager.get().unregisterListener(installStateUpdatedListener)
                }
            }
            else -> {
                awaitClose { }
            }
        }
    }

    override fun checkIsFlexibleUpdateStalled(): Flow<IsUpdateDownloaded> = callbackFlow {
        appUpdateManager.get().appUpdateInfo.addOnSuccessListener { updateInfo ->
            val isUpdateDownloadedButNotInstalled =
                updateInfo.installStatus == InstallStatus.DOWNLOADED

            launch {
                if (isUpdateDownloadedButNotInstalled) {
                    send(true)
                } else send(false)
            }
        }

        awaitClose {}
    }

    override fun checkIsImmediateUpdateStalled(): Flow<AppUpdateInfo?> = callbackFlow {
        appUpdateManager.get().appUpdateInfo.addOnSuccessListener { updateInfo ->
            val isUpdateStalled =
                updateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS

            launch {
                if (isUpdateStalled) {
                    send(updateInfo)
                } else send(null)
            }
        }

        awaitClose {}
    }

    override fun checkForAppUpdates(): Flow<AppUpdateInfo?> = callbackFlow {
        appUpdateManager.get().appUpdateInfo.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.i("Check for in-app update was successful.")
                val updateInfo = task.result

                val isUpdateAvailable = updateInfo.updateAvailability() ==
                        UpdateAvailability.UPDATE_AVAILABLE

                val isUpdateAllowed = when (appUpdateType.get()) {
                    AppUpdateType.IMMEDIATE -> updateInfo.isImmediateUpdateAllowed
                    AppUpdateType.FLEXIBLE -> updateInfo.isFlexibleUpdateAllowed
                    else -> false
                }

                val stalenessDays = updateInfo.clientVersionStalenessDays ?: -0
                val hasUpdateStalenessDaysReached = stalenessDays >=
                        Constants.IN_APP_UPDATES_DAYS_FOR_FLEXIBLE_UPDATE

                val shouldStartUpdateFlow = isUpdateAvailable &&
                        isUpdateAllowed && hasUpdateStalenessDaysReached

                launch {
                    if (shouldStartUpdateFlow) {
                        send(updateInfo)
                    } else send(null)
                }
            } else {
                Timber.e("Check for in-app update was not successful:")
                task.exception?.printStackTrace()
                launch { send(null) }
            }
        }

        awaitClose { }
    }

    override suspend fun getLatestAppVersion(): Result<LatestAppUpdateInfo?, GetLatestAppVersionError> {
        return try {
            val httpResponse = httpClient.get().get(
                urlString = AppUpdateRepository.EndPoints.GetLatestAppVersion.url
            )

            Timber.i("Get latest app version response call = ${httpResponse.request.call}")

            return when (httpResponse.status) {
                HttpStatusCode.OK -> { // Code: 200
                    val latestVersionDto = Json.decodeFromString<LatestAppVersionDto>(
                        httpResponse.bodyAsText()
                    )

                    val currentVersionCode = BuildConfig.VERSION_CODE

                    val isAppUpdated = currentVersionCode >= latestVersionDto.versionCode
                    if (isAppUpdated) {
                        with(miscellaneousDataStoreRepository.get()) {
                            storeAppUpdateDialogShowCount(count = 0)
                            removeAppUpdateDialogShowDateTime()
                        }

                        return Result.Success(null)
                    }

                    with(miscellaneousDataStoreRepository.get()) {
                        val updateDialogShowCount = getAppUpdateDialogShowCount().first()
                        val isAppUpdateDialogShownToday = isAppUpdateDialogShownToday().first()

                        val shouldShowAppUpdateDialog = when {
                            isAppUpdateDialogShownToday && updateDialogShowCount < 2 -> true
                            !isAppUpdateDialogShownToday -> true
                            else -> false
                        }

                        if (shouldShowAppUpdateDialog) {
                            storeAppUpdateDialogShowCount(
                                count = when {
                                    isAppUpdateDialogShownToday -> updateDialogShowCount + 1
                                    else -> 0
                                }
                            )
                            storeAppUpdateDialogShowDateTime()

                            return@with Result.Success(
                                LatestAppUpdateInfo(
                                    versionCode = latestVersionDto.versionCode,
                                    versionName = latestVersionDto.versionName
                                )
                            )
                        }

                        Result.Success(null)
                    }
                }
                else -> Result.Error(GetLatestAppVersionError.Network.SomethingWentWrong)
            }
        } catch (e: UnresolvedAddressException) { // When device is offline
            Timber.e("Get latest app version UnresolvedAddressException:}")
            e.printStackTrace()
            Result.Error(GetLatestAppVersionError.Network.Offline)
        } catch (e: UnknownHostException) { // When device is offline
            Timber.e("Get latest app version UnknownHostException:")
            e.printStackTrace()
            Result.Error(GetLatestAppVersionError.Network.Offline)
        } catch (e: ConnectTimeoutException) {
            Timber.e("Get latest app version ConnectTimeoutException:")
            e.printStackTrace()
            Result.Error(GetLatestAppVersionError.Network.ConnectTimeoutException)
        } catch (e: HttpRequestTimeoutException) {
            Timber.e("Get latest app version HttpRequestTimeoutException:")
            e.printStackTrace()
            Result.Error(GetLatestAppVersionError.Network.HttpRequestTimeoutException)
        } catch (e: SocketTimeoutException) {
            Timber.e("Get latest app version SocketTimeoutException:")
            e.printStackTrace()
            Result.Error(GetLatestAppVersionError.Network.SocketTimeoutException)
        } catch (e: RedirectResponseException) { // 3xx responses
            Timber.e("Get latest app version RedirectResponseException:")
            e.printStackTrace()
            Result.Error(GetLatestAppVersionError.Network.RedirectResponseException)
        } catch (e: ClientRequestException) { // 4xx responses
            Timber.e("Get latest app version ClientRequestException:")
            e.printStackTrace()
            when (e.response.status) {
                HttpStatusCode.TooManyRequests -> Result.Error(GetLatestAppVersionError.Network.TooManyRequests)
                else -> Result.Error(GetLatestAppVersionError.Network.ClientRequestException)
            }
        } catch (e: ServerResponseException) { // 5xx responses
            Timber.e("Get latest app version ServerResponseException:")
            e.printStackTrace()
            Result.Error(GetLatestAppVersionError.Network.ServerResponseException)
        } catch (e: SerializationException) {
            Timber.e("Get latest app version SerializationException:")
            e.printStackTrace()
            Result.Error(GetLatestAppVersionError.Network.SerializationException)
        } catch (e: JsonConvertException) {
            Timber.e("Get latest app version JsonConvertException:")
            e.printStackTrace()
            Result.Error(GetLatestAppVersionError.Network.JsonConvertException)
        } catch (e: SSLHandshakeException) {
            Timber.e("Get latest app version SSLHandshakeException:")
            e.printStackTrace()
            Result.Error(GetLatestAppVersionError.Network.SSLHandshakeException)
        } catch (e: CertPathValidatorException) {
            Timber.e("Get latest app version CertPathValidatorException:")
            e.printStackTrace()
            Result.Error(GetLatestAppVersionError.Network.CertPathValidatorException)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e("Get latest app version Exception:")
            e.printStackTrace()
            Result.Error(GetLatestAppVersionError.Network.SomethingWentWrong)
        }
    }
}