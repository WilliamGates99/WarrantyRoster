package com.xeniac.warrantyroster_manager.feature_linked_accounts.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.GetLinkedAccountProvidersError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.models.AccountProviders
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkedAccountsRepository
import dagger.Lazy
import kotlinx.coroutines.ensureActive
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class LinkedAccountsRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>
) : LinkedAccountsRepository {

    override suspend fun getLinkedAccountProviders(): Result<Set<AccountProviders>, GetLinkedAccountProvidersError> {
        return try {
            val linkedAccountProviders = mutableSetOf<AccountProviders>()

            firebaseAuth.get().currentUser?.providerData?.forEach { userInfo ->
                when (userInfo.providerId) {
                    AccountProviders.GOOGLE.id -> linkedAccountProviders.add(AccountProviders.GOOGLE)
                    AccountProviders.X.id -> linkedAccountProviders.add(AccountProviders.X)
                    AccountProviders.GITHUB.id -> linkedAccountProviders.add(AccountProviders.GITHUB)
                }
            }

            Result.Success(linkedAccountProviders)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Get linked account providers FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(GetLinkedAccountProvidersError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseNetworkException) {
            Timber.e("Get linked account providers FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(GetLinkedAccountProvidersError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Get linked account providers FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(GetLinkedAccountProvidersError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Get linked account providers FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(GetLinkedAccountProvidersError.Network.Firebase403)
                else -> Result.Error(GetLinkedAccountProvidersError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Get linked account providers Exception:")
            e.printStackTrace()
            Result.Error(GetLinkedAccountProvidersError.Network.SomethingWentWrong)
        }
    }
}