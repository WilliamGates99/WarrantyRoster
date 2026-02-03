package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation

import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors.UpsertWarrantyError
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@ViewModelScoped
class ValidateStartingAndExpiryDates @Inject constructor() {
    operator fun invoke(
        isLifetime: Boolean,
        startingDate: Instant?,
        expiryDate: Instant?
    ): UpsertWarrantyError? {
        if (isLifetime && startingDate == null) {
            return UpsertWarrantyError.BlankStartingDate
        }

        if (!isLifetime) {
            if (startingDate == null && expiryDate == null) {
                return UpsertWarrantyError.BlankStartingAndExpiryDate
            }

            if (startingDate == null) {
                return UpsertWarrantyError.BlankStartingDate
            }

            if (expiryDate == null) {
                return UpsertWarrantyError.BlankExpiryDate
            }

            val isExpiryDateBeforeStartingDate = expiryDate < startingDate
            if (isExpiryDateBeforeStartingDate) {
                return UpsertWarrantyError.InvalidExpiryDate
            }
        }

        return null
    }
}