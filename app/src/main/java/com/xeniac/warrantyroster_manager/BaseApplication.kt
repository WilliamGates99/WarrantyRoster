package com.xeniac.warrantyroster_manager

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.toArgb
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import coil3.util.DebugLogger
import com.xeniac.warrantyroster_manager.core.domain.models.AppTheme
import com.xeniac.warrantyroster_manager.core.domain.repositories.ConnectivityObserver
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.BlueNotificationLight
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.NetworkObserverHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(), SingletonImageLoader.Factory {

    companion object {
        const val NOTIFICATION_CHANNEL_GROUP_ID_FCM = "group_fcm"
        const val NOTIFICATION_CHANNEL_ID_FCM_MISCELLANEOUS = "channel_fcm_miscellaneous"
    }

    @Inject
    lateinit var currentAppTheme: AppTheme

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate() {
        super.onCreate()

        setupTimber()
        setAppTheme()
        observeNetworkConnection()
        initFirebaseAppCheck()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createFcmNotificationChannelGroup()
            createMiscellaneousFcmNotificationChannel()
        }
    }

    private fun setupTimber() = Timber.plant(Timber.DebugTree())

    private fun setAppTheme() = currentAppTheme.setAppTheme()

    private fun observeNetworkConnection() {
        NetworkObserverHelper.observeNetworkConnection(connectivityObserver)
    }

    private fun initFirebaseAppCheck() {
//        FirebaseApp.initializeApp(this)
//        val firebaseAppCheck = FirebaseAppCheck.getInstance()
//
//        if (BuildConfig.DEBUG) {
//            firebaseAppCheck.installAppCheckProviderFactory(
//                DebugAppCheckProviderFactory.getInstance()
//            )
//        } else {
//            firebaseAppCheck.installAppCheckProviderFactory(
//                PlayIntegrityAppCheckProviderFactory.getInstance()
//            )
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createFcmNotificationChannelGroup() {
        val notificationChannelGroup = NotificationChannelGroup(
            /* id = */ NOTIFICATION_CHANNEL_GROUP_ID_FCM,
            /* name = */ getString(R.string.notification_fcm_channel_group_name)
        )

        notificationManager.createNotificationChannelGroup(notificationChannelGroup)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMiscellaneousFcmNotificationChannel() {
        val miscellaneousNotificationChannel = NotificationChannel(
            /* id = */ NOTIFICATION_CHANNEL_ID_FCM_MISCELLANEOUS,
            /* name = */ getString(R.string.notification_fcm_channel_name_miscellaneous),
            /* importance = */ NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            group = NOTIFICATION_CHANNEL_GROUP_ID_FCM
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            lightColor = BlueNotificationLight.toArgb()
            enableLights(true)
        }

        notificationManager.createNotificationChannel(miscellaneousNotificationChannel)
    }

    override fun newImageLoader(
        context: PlatformContext
    ): ImageLoader = ImageLoader.Builder(context).apply {
        components {
            add(factory = SvgDecoder.Factory()) // SVGs
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) { // GIFs
                add(factory = AnimatedImageDecoder.Factory())
            } else {
                add(factory = GifDecoder.Factory())
            }
        }
        crossfade(enable = true)
        networkCachePolicy(policy = CachePolicy.ENABLED)
        memoryCachePolicy(policy = CachePolicy.ENABLED)
        diskCachePolicy(policy = CachePolicy.ENABLED)
        memoryCache {
            MemoryCache.Builder()
                // Set the max size to 25% of the app's available memory.
                .maxSizePercent(
                    context = context,
                    percent = 0.25
                )
                .build()
        }
        diskCache {
            DiskCache.Builder()
                // Set cache directory folder name
                .directory(cacheDir.resolve(relative = "image_cache"))
                .maxSizePercent(percent = 0.03) // Set the max size to 3% of the device's free disk space.
                .build()
        }
        if (BuildConfig.DEBUG) logger(logger = DebugLogger())
    }.build()
}