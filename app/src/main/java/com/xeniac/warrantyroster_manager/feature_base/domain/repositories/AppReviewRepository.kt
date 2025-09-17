package com.xeniac.warrantyroster_manager.feature_base.domain.repositories

import com.google.android.play.core.review.ReviewInfo
import kotlinx.coroutines.flow.Flow

interface AppReviewRepository {
    fun requestInAppReviews(): Flow<ReviewInfo?>
}