package com.xeniac.warrantyroster_manager.core.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class RateAppOption(
    val value: String
) : Parcelable {
    NOT_SHOWN_YET(value = "notShownYet"),
    NEVER(value = "never"),
    REMIND_LATER(value = "remindLater"),
    RATE_NOW(value = "rateNow")
}