package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.RateAppOption
import com.xeniac.warrantyroster_manager.core.domain.repositories.MiscellaneousDataStoreRepository
import kotlinx.coroutines.flow.Flow

class GetSelectedRateAppOptionUseCase(
    private val miscellaneousDataStoreRepository: MiscellaneousDataStoreRepository
) {
    operator fun invoke(): Flow<RateAppOption> = miscellaneousDataStoreRepository
        .getSelectedRateAppOption()
}