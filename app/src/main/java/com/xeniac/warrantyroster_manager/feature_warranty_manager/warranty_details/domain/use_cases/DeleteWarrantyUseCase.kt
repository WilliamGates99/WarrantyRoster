package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.errors.DeleteWarrantyError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.repositories.DeleteWarrantyRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class DeleteWarrantyUseCase @Inject constructor(
    private val deleteWarrantyRepository: DeleteWarrantyRepository
) {
    operator fun invoke(
        warrantyId: String
    ): Flow<Result<Unit, DeleteWarrantyError>> = flow {
        return@flow emit(deleteWarrantyRepository.deleteWarranty(warrantyId = warrantyId))
    }
}