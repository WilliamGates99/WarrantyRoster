package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.PreviousRateAppRequestDateTime
import com.xeniac.warrantyroster_manager.core.domain.repositories.MiscellaneousDataStoreRepository
import kotlinx.coroutines.flow.Flow

class GetPreviousRateAppRequestDateTimeUseCase(
    private val miscellaneousDataStoreRepository: MiscellaneousDataStoreRepository
) {
    operator fun invoke(): Flow<PreviousRateAppRequestDateTime?> = miscellaneousDataStoreRepository
        .getPreviousRateAppRequestDateTime()
}