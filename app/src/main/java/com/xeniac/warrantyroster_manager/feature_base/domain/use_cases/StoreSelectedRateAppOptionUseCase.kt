package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.RateAppOption
import com.xeniac.warrantyroster_manager.core.domain.repositories.MiscellaneousDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoreSelectedRateAppOptionUseCase(
    private val miscellaneousDataStoreRepository: MiscellaneousDataStoreRepository
) {
    operator fun invoke(
        rateAppOption: RateAppOption
    ): Flow<Unit> = flow {
        return@flow emit(miscellaneousDataStoreRepository.storeSelectedRateAppOption(rateAppOption))
    }
}