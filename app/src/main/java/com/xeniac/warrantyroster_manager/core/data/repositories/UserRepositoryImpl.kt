package com.xeniac.warrantyroster_manager.core.data.repositories

import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.core.domain.errors.LogoutUserError
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.repositories.UserRepository
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import dagger.Lazy
import kotlinx.coroutines.ensureActive
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class UserRepositoryImpl @Inject constructor(
    private val credentialManager: Lazy<CredentialManager>,
    private val firebaseAuth: Lazy<FirebaseAuth>,
    private val warrantyRosterDataStoreRepository: Lazy<WarrantyRosterDataStoreRepository>
) : UserRepository {

    override suspend fun logoutUser(): Result<Unit, LogoutUserError> {
        return try {
            firebaseAuth.get().signOut()

            // Clear the current user credential state from all credential providers
            val clearRequest = ClearCredentialStateRequest()
            credentialManager.get().clearCredentialState(request = clearRequest)

            warrantyRosterDataStoreRepository.get().isUserLoggedIn(isLoggedIn = false)

            Result.Success(Unit)
        } catch (e: ClearCredentialException) {
            Timber.e("Logout user ClearCredentialException:")
            e.printStackTrace()
            Result.Error(LogoutUserError.Network.SomethingWentWrong)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Logout user Exception:")
            e.printStackTrace()
            Result.Error(LogoutUserError.Network.SomethingWentWrong)
        }
    }

    override suspend fun forceLogoutUnauthorizedUser(): Result<Unit, LogoutUserError> {
        return try {
            firebaseAuth.get().signOut()
            warrantyRosterDataStoreRepository.get().isUserLoggedIn(isLoggedIn = false)
            Result.Success(Unit)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Force logout unauthorized user Exception:")
            e.printStackTrace()
            Result.Error(LogoutUserError.Local.SomethingWentWrong)
        }
    }
}