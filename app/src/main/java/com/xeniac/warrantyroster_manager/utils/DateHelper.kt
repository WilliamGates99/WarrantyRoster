package com.xeniac.warrantyroster_manager.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateHelper {

    fun isStartingDateValid(startingDateInMillis: Long, expiryDateInMillis: Long): Boolean =
        expiryDateInMillis >= startingDateInMillis

    fun getDayWithSuffix(day: Int): String {
        if (day in 11..13) {
            return "${day}th"
        }

        return when (day % 10) {
            1 -> "${day}st"
            2 -> "${day}nd"
            3 -> "${day}rd"
            else -> "${day}th"
        }
    }

    fun getDaysUntilExpiry(expiryCalendar: Calendar): Long {
        val todayCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return TimeUnit.MILLISECONDS.toDays(expiryCalendar.timeInMillis - todayCalendar.timeInMillis)
    }

    fun getDaysUntilExpiry(expiryDate: String, dateFormat: SimpleDateFormat): Long {
        val expiryCalendar = Calendar.getInstance()

        dateFormat.parse(expiryDate)?.let {
            expiryCalendar.time = it
        }

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