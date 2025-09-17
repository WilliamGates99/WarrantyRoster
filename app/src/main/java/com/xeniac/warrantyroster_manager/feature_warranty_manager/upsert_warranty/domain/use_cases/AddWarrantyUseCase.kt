package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.models.UpsertWarrantyResult
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.models.UpsertingWarranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.repositories.AddWarrantyRepository
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateBrand
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateDescription
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateModel
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateSelectedCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateSerialNumber
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateStartingAndExpiryDates
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateTitle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class AddWarrantyUseCase(
    private val addWarrantyRepository: AddWarrantyRepository,
    private val validateTitle: ValidateTitle,
    private val validateBrand: ValidateBrand,
    private val validateModel: ValidateModel,
    private val validateSerialNumber: ValidateSerialNumber,
    private val validateDescription: ValidateDescription,
    private val validateSelectedCategory: ValidateSelectedCategory,
    private val validateStartingAndExpiryDates: ValidateStartingAndExpiryDates
) {
    operator fun invoke(
        title: String,
        brand: String,
        model: String,
        serialNumber: String,
        description: String,
        selectedCategory: WarrantyCategory?,
        isLifetime: Boolean,
        selectedStartingDate: Instant?,
        selectedExpiryDate: Instant?
    ): Flow<UpsertWarrantyResult> = flow {
        val titleError = validateTitle(title)
        val brandError = validateBrand(brand)
        val modelError = validateModel(model)
        val serialNumberError = validateSerialNumber(serialNumber)
        val descriptionError = validateDescription(description)
        val selectedCategoryError = validateSelectedCategory(selectedCategory)
        val startingAndExpiryDatesError = validateStartingAndExpiryDates(
            isLifetime = isLifetime,
            startingDate = selectedStartingDate,
            expiryDate = selectedExpiryDate
        )

        val hasError = listOf(
            titleError,
            brandError,
            modelError,
            serialNumberError,
            descriptionError,
            selectedCategoryError,
            startingAndExpiryDatesError
        ).any { it != null }

        if (hasError) {
            return@flow emit(
                UpsertWarrantyResult(
                    titleError = titleError,
                    brandError = brandError,
                    modelError = modelError,
                    serialNumberError = serialNumberError,
                    descriptionError = descriptionError,
                    selectedCategoryError = selectedCategoryError,
                    startingAndExpiryDatesError = startingAndExpiryDatesError
                )
            )
        }

        return@flow emit(
            UpsertWarrantyResult(
                result = addWarrantyRepository.addWarranty(
                    addingWarranty = UpsertingWarranty(
                        title = title,
                        brand = brand,
                        model = model,
                        serialNumber = serialNumber,
                        description = description,
                        selectedCategory = selectedCategory,
                        isLifetime = isLifetime,
                        selectedStartingDate = selectedStartingDate!!,
                        selectedExpiryDate = selectedExpiryDate
                    )
                )
            )
        )
    }
}