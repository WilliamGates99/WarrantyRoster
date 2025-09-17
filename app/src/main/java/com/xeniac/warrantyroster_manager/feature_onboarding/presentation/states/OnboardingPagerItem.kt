package com.xeniac.warrantyroster_manager.feature_onboarding.presentation.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.xeniac.warrantyroster_manager.R

enum class OnboardingPagerItem(
    @param:DrawableRes val imageId: Int,
    @param:StringRes val descriptionId: Int
) {
    FIRST_PAGE(
        imageId = R.drawable.ic_onboarding_1st,
        descriptionId = R.string.onboarding_1st_description
    ),
    SECOND(
        imageId = R.drawable.ic_onboarding_2nd,
        descriptionId = R.string.onboarding_2nd_description
    ),
    THIRD(
        imageId = R.drawable.ic_onboarding_3rd,
        descriptionId = R.string.onboarding_3rd_description
    ),
    FOURTH(
        imageId = R.drawable.ic_onboarding_4th,
        descriptionId = R.string.onboarding_4th_description
    )
}