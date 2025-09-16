package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.presentation.utils

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.presentation.states.WarrantyExpiryStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun calculateWarrantyExpiryStatus(
    isLifetime: Boolean,
    expiryDate: LocalDate?,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): WarrantyExpiryStatus {
    if (isLifetime || expiryDate == null) {
        return WarrantyExpiryStatus.VALID
    }

    val todayDate = Clock.System.now().toLocalDateTime(
        timeZone = timeZone
    ).date

    val daysUntilExpiry = todayDate.daysUntil(other = expiryDate)

    return when {
        daysUntilExpiry < 0 -> WarrantyExpiryStatus.EXPIRED
        daysUntilExpiry <= 30 -> WarrantyExpiryStatus.SOON
        else -> WarrantyExpiryStatus.VALID
    }
}