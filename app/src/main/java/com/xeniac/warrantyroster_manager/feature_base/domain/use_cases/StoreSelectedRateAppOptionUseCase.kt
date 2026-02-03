package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.RateAppOption
import com.xeniac.warrantyroster_manager.core.domain.repositories.MiscellaneousDataStoreRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class StoreSelectedRateAppOptionUseCase @Inject constructor(
    private val miscellaneousDataStoreRepository: MiscellaneousDataStoreRepository
) {
    operator fun invoke(
        rateAppOption: RateAppOption
    ): Flow<Unit> = flow {
        return@flow emit(miscellaneousDataStoreRepository.storeSelectedRateAppOption(rateAppOption))
    }
}