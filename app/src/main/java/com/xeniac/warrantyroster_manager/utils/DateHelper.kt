package com.xeniac.warrantyroster_manager.utils

import android.content.Context
import java.util.*
import java.util.concurrent.TimeUnit

object DateHelper {

    fun getDaysFromFirstInstallTime(context: Context): Long {
        val currentTimeInMillis = Calendar.getInstance().timeInMillis

        val firstInstallTimeInMillis = context.packageManager
            .getPackageInfo(context.packageName, 0).firstInstallTime

        return TimeUnit.MILLISECONDS.toDays(currentTimeInMillis - firstInstallTimeInMillis)
    }

    fun getDaysFromPreviousRequestTime(previousRequestTimeInMillis: Long): Long {
        val currentTimeInMillis = Calendar.getInstance().timeInMillis

        return TimeUnit.MILLISECONDS.toDays(currentTimeInMillis - previousRequestTimeInMillis)
    }

    fun isStartingDateValid(startingDateInMillis: Long, expiryDateInMillis: Long): Boolean =
        expiryDateInMillis >= startingDateInMillis

    fun getDaysUntilExpiry(expiryCalendar: Calendar): Long {
        val todayCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return TimeUnit.MILLISECONDS.toDays(expiryCalendar.timeInMillis - todayCalendar.timeInMillis)
    }

    fun getTimeZoneOffsetInMillis() = TimeZone.getDefault().getOffset(Date().time)
}