package com.xeniac.warrantyroster_manager.utils

import java.util.*
import java.util.concurrent.TimeUnit

object DateHelper {

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