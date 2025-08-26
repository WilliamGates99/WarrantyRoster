@file:Suppress("KotlinConstantConditions")

package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import com.xeniac.warrantyroster_manager.BuildConfig

fun isAppInstalledFromPlayStore() = when (BuildConfig.FLAVOR) {
    "playStore" -> true
    else -> false
}

fun isAppInstalledFromGitHub() = when (BuildConfig.FLAVOR) {
    "gitHub" -> true
    else -> false
}

fun isAppInstalledFromMyket() = when (BuildConfig.FLAVOR) {
    "myket" -> true
    else -> false
}