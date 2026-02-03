package com.xeniac.warrantyroster_manager.core.domain.use_cases

import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class GetIsUserLoggedInUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val warrantyRosterDataStoreRepository: WarrantyRosterDataStoreRepository
) {
    operator fun invoke(): Flow<Boolean> = flow {
        val isPreviouslyLoggedIn = warrantyRosterDataStoreRepository.isUserLoggedIn()
        val isFirebaseUserLoggedIn = firebaseAuth.currentUser != null
        val isUserLoggedIn = isPreviouslyLoggedIn && isFirebaseUserLoggedIn
        return@flow emit(isUserLoggedIn)
    }
}