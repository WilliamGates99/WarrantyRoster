package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.mappers

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.remote.WarrantyDto
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
fun WarrantyDto.toWarranty(
    category: WarrantyCategory,
    /**
     * DateTime Format: yyyy-mm-dd
     * Sample: 2025-09-24
     */
    dateFormat: DateTimeFormat<LocalDate> = LocalDate.Formats.ISO,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): Warranty = Warranty(
    id = id ?: Uuid.random().toHexString(),
    title = title.orEmpty(),
    brand = brand.orEmpty(),
    model = model.orEmpty(),
    serialNumber = serialNumber.orEmpty(),
    description = description.orEmpty(),
    isLifetime = isLifetime ?: false,
    category = category,
    startingDate = startingDate?.let { startingDate ->
        LocalDate.parse(
            input = startingDate,
            format = dateFormat
        )
    } ?: Clock.System.now().toLocalDateTime(timeZone = timeZone).date,
    expiryDate = expiryDate?.let { expiryDate ->
        LocalDate.parse(
            input = expiryDate,
            format = dateFormat
        )
    } ?: Clock.System.now().toLocalDateTime(timeZone = timeZone).date
)