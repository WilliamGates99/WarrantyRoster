package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.data.mappers

import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.data.remote.UpsertingWarrantyDto
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.models.UpsertingWarranty
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun UpsertingWarranty.toUpsertingWarrantyDto(
    uuid: String,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    dateFormat: DateTimeFormat<LocalDate> = LocalDate.Formats.ISO
): UpsertingWarrantyDto = UpsertingWarrantyDto(
    uuid = uuid,
    title = title.trim(),
    brand = brand.ifBlank { null }?.trim(),
    model = model.ifBlank { null }?.trim(),
    serialNumber = serialNumber.ifBlank { null }?.trim(),
    description = description.ifBlank { null }?.trim(),
    categoryId = selectedCategory?.id ?: "10",
    isLifetime = isLifetime,
    startingDate = selectedStartingDate.toLocalDateTime(
        timeZone = timeZone
    ).date.format(format = dateFormat),
    expiryDate = selectedExpiryDate?.toLocalDateTime(
        timeZone = timeZone
    )?.date?.format(format = dateFormat)
)