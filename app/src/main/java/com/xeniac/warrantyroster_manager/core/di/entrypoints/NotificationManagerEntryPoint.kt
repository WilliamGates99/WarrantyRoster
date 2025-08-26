package com.xeniac.warrantyroster_manager.core.di.entrypoints

import android.app.NotificationManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NotificationManagerEntryPoint {
    val notificationManager: NotificationManager
}