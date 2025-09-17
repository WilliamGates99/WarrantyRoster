package com.xeniac.warrantyroster_manager.feature_firebase_cloud_messaging.services

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.xeniac.warrantyroster_manager.BaseApplication
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.di.entrypoints.requireNotificationManager
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.BlueNotificationLight
import com.xeniac.warrantyroster_manager.feature_firebase_cloud_messaging.services.utils.getBitmapFromUrl
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.random.Random

@AndroidEntryPoint
class FirebaseCloudMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.i("New firebase registration token generated.")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val cancelNotificationPendingIntent = PendingIntent.getActivity(
            /* context = */ this,
            /* requestCode = */ 0,
            /* intent = */ Intent(/* action = */ ""),
            /* flags = */ PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            /* context = */ this,
            /* channelId = */ BaseApplication.NOTIFICATION_CHANNEL_ID_FCM_MISCELLANEOUS
        ).apply {
            setAutoCancel(true)
            setContentIntent(cancelNotificationPendingIntent)
            setSmallIcon(R.drawable.ic_notification)
            setContentTitle(message.notification?.title)
            setContentText(message.notification?.body)

            setStyle(NotificationCompat.BigTextStyle().bigText(message.notification?.body))

            message.notification?.imageUrl?.let { imageUrl ->
                val bitmap = getBitmapFromUrl(imageUrl.toString())
                setLargeIcon(bitmap)
                setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null as Bitmap?)
                )
            }

            /*
            On Android 8.0 and above these values are ignored in favor of the values set on the notification's channel.
            On older platforms, these values are still used, so it is still required for apps supporting those platforms.
            */
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setLights(
                /* argb = */ BlueNotificationLight.toArgb(),
                /* onMs = */ 1000,
                /* offMs = */ 1000
            )
            setVibrate(null)
        }.build()

        requireNotificationManager(context = this).notify(
            /* id = */ Random.nextInt(),
            /* notification = */ notification
        )
    }
}