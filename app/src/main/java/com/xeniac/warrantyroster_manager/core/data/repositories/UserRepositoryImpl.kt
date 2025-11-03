package com.xeniac.warrantyroster_manager.core.data.repositories

import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.xeniac.warrantyroster_manager.core.data.mappers.toUserProfile
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.domain.errors.GetUserProfileError
import com.xeniac.warrantyroster_manager.core.domain.errors.LogoutUserError
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.models.UserProfile
import com.xeniac.warrantyroster_manager.core.domain.repositories.UserRepository
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import dagger.Lazy
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val credentialManager: Lazy<CredentialManager>,
    private val firebaseAuth: Lazy<FirebaseAuth>,
    private val warrantyRosterDataStoreRepository: Lazy<WarrantyRosterDataStoreRepository>
) : UserRepository {

    override suspend fun getUserProfile(): Result<UserProfile, GetUserProfileError> {
        return try {
            firebaseAuth.get().currentUser?.let { currentUser ->
                return Result.Success(currentUser.toUserProfile())
            }

            Result.Error(GetUserProfileError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Get user profile FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(GetUserProfileError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseNetworkException) {
            Timber.e("Get user profile FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(GetUserProfileError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Get user profile FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(GetUserProfileError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Get user profile FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(GetUserProfileError.Network.Firebase403)
                else -> Result.Error(GetUserProfileError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e("Get user profile Exception:")
            e.printStackTrace()
            Result.Error(GetUserProfileError.Network.SomethingWentWrong)
        }
    }

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
            currentCoroutineContext().ensureActive()
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
            currentCoroutineContext().ensureActive()
            Timber.e("Force logout unauthorized user Exception:")
            e.printStackTrace()
            Result.Error(LogoutUserError.Local.SomethingWentWrong)
        }
    }
}