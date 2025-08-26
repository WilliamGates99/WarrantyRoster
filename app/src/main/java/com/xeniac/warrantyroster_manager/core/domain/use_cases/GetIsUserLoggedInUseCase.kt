package com.xeniac.warrantyroster_manager.core.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetIsUserLoggedInUseCase(
    private val warrantyRosterDataStoreRepository: WarrantyRosterDataStoreRepository
) {
    operator fun invoke(): Flow<Boolean> = flow {
        return@flow emit(warrantyRosterDataStoreRepository.isUserLoggedIn())
    }
}