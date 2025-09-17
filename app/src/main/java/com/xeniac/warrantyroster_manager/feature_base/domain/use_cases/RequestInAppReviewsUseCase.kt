package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.google.android.play.core.review.ReviewInfo
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppReviewRepository
import kotlinx.coroutines.flow.Flow

class RequestInAppReviewsUseCase(
    private val appReviewRepository: AppReviewRepository
) {
    operator fun invoke(): Flow<ReviewInfo?> = appReviewRepository.requestInAppReviews()
}