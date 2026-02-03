package com.xeniac.warrantyroster_manager.feature_onboarding.presentation.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.xeniac.warrantyroster_manager.R

enum class OnboardingPagerItem(
    val index: Int,
    @param:DrawableRes val imageId: Int,
    @param:StringRes val descriptionId: Int
) {
    PAGE_ONE(
        index = 0,
        imageId = R.drawable.ic_onboarding_1st,
        descriptionId = R.string.onboarding_1st_description
    ),
    PAGE_TWO(
        index = 1,
        imageId = R.drawable.ic_onboarding_2nd,
        descriptionId = R.string.onboarding_2nd_description
    ),
    PAGE_THREE(
        index = 2,
        imageId = R.drawable.ic_onboarding_3rd,
        descriptionId = R.string.onboarding_3rd_description
    ),
    PAGE_FOUR(
        index = 3,
        imageId = R.drawable.ic_onboarding_4th,
        descriptionId = R.string.onboarding_4th_description
    )
}