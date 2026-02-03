package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.google.android.play.core.review.ReviewInfo
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppReviewRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class RequestInAppReviewsUseCase @Inject constructor(
    private val appReviewRepository: AppReviewRepository
) {
    operator fun invoke(): Flow<ReviewInfo?> = appReviewRepository.requestInAppReviews()
}