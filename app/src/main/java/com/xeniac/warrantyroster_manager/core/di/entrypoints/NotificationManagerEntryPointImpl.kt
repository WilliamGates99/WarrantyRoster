package com.xeniac.warrantyroster_manager.core.di.entrypoints

import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.EntryPointAccessors

private lateinit var notificationManagerEntryPoint: NotificationManagerEntryPoint

fun requireNotificationManager(context: Context): NotificationManager {
    if (!::notificationManagerEntryPoint.isInitialized) {
        notificationManagerEntryPoint = EntryPointAccessors.fromApplication(
            context = context,
            entryPoint = NotificationManagerEntryPoint::class.java
        )
    }
    return notificationManagerEntryPoint.notificationManager
}