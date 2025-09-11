package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.mappers

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.remote.WarrantyDto
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun WarrantyDto.toWarranty(
    /**
     * DateTime Format: yyyy-mm-dd
     * Sample: 2025-09-24
     */
    dateFormat: DateTimeFormat<LocalDate> = LocalDate.Formats.ISO,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): Warranty = Warranty(
    id = id,
    title = title.orEmpty(),
    brand = brand.orEmpty(),
    model = model.orEmpty(),
    serialNumber = serialNumber.orEmpty(),
    description = description.orEmpty(),
    isLifetime = isLifetime ?: false,
    categoryId = categoryId ?: "10",
    startingDate = startingDate?.let {
        LocalDate.parse(
            input = startingDate,
            format = dateFormat
        )
    } ?: Clock.System.now().toLocalDateTime(timeZone = timeZone).date,
    expiryDate = expiryDate?.let {
        LocalDate.parse(
            input = expiryDate,
            format = dateFormat
        )
    } ?: Clock.System.now().toLocalDateTime(timeZone = timeZone).date
)

fun Warranty.toWarrantyDto(
    dateFormat: DateTimeFormat<LocalDate> = LocalDate.Formats.ISO
): WarrantyDto = WarrantyDto(
    id = id,
    title = title,
    brand = brand,
    model = model,
    serialNumber = serialNumber,
    description = description,
    isLifetime = isLifetime,
    categoryId = categoryId,
    startingDate = startingDate.format(format = dateFormat),
    expiryDate = expiryDate.format(format = dateFormat)
)