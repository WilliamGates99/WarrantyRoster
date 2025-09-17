package com.xeniac.warrantyroster_manager.feature_base.data.repositories

import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppReviewRepository
import dagger.Lazy
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AppReviewRepositoryImpl @Inject constructor(
    private val reviewManager: Lazy<ReviewManager>
) : AppReviewRepository {

    override fun requestInAppReviews(): Flow<ReviewInfo?> = callbackFlow {
        reviewManager.get().requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.i("In-App Reviews request was successful.")
                launch { send(task.result) }
            } else {
                Timber.e("In-App Reviews request was not successful:")
                task.exception?.printStackTrace()
                launch { send(null) }
            }
        }

        awaitClose { }
    }
}